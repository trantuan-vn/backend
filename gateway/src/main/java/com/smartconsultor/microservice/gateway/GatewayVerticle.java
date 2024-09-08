package com.smartconsultor.microservice.gateway; 

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.impl.OAuth2API;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.cluster.infinispan.ClusterHealthCheck;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CSPHandler;
import io.vertx.ext.web.handler.CSRFHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.HSTSHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.XFrameHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.sstore.infinispan.InfinispanSessionStore;
import io.vertx.micrometer.PrometheusScrapingHandler;
//import io.vertx.ext.web.sstore.redis.RedisSessionStore;
//import io.vertx.redis.client.Redis;
//import io.vertx.redis.client.RedisAPI;
//import io.vertx.redis.client.RedisOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.spi.cluster.ClusterManager;

import com.smartconsultor.microservice.common.RestAPIVerticle;
import com.smartconsultor.microservice.common.utils.RequestUtil;

public class GatewayVerticle extends RestAPIVerticle {
  private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class); 
  private OAuth2Auth keycloakAuthProvider; 
   
  // tag::start[]
  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);    
    setupRouter(router);
 
    JsonObject jsonServer=config().getJsonObject("server");
    // Create and start the HTTP server
    createHttpServer(router, jsonServer.getInteger("port"),jsonServer.getString("host"))
      .onSuccess(server -> {
        logger.info("Gateway Server started and listening on port {}", server.actualPort());
        startPromise.complete(); // Complete the verticle start promise
      })
      .onFailure(cause -> {
        logger.error("Failed to start the Gateway Server", cause);
        startPromise.fail(cause); // Fail the verticle start promise
      });  
  }
  // end::start[]
  // tag::router[]
  private void setupRouter(Router router) { 
    
    // body handler
    router.route().handler(BodyHandler.create());  

    // Store session information on the server side
    /* 
    String redisConnectionString= "redis://"+ config().getJsonObject("redis").getString("host") 
                                      + ":" + config().getJsonObject("redis").getString("port");
    RedisOptions options = new RedisOptions()
        .setConnectionString(redisConnectionString)
        .setPassword(config().getJsonObject("redis").getString("password"));
    Redis redisClient = Redis.createClient(vertx, options);
    RedisSessionStore redisSessionStore = RedisSessionStore.create(vertx, redisClient);    
    router.route().handler(SessionHandler.create(redisSessionStore));
    */
    JsonObject storeOptions = new JsonObject()
      .put("servers", new JsonArray().add(config().getJsonObject("infinispan")));
    SessionStore store = InfinispanSessionStore.create(vertx, storeOptions); 
    router.route().handler(SessionHandler.create(store)
                          .setCookieHttpOnlyFlag(true)
                          .setCookieSecureFlag(true)
                          );  
    // CSRF handler setup required for logout form
    String csrfSecret = generateCsrfSecret();
    CSRFHandler csrfHandler = CSRFHandler.create(vertx,csrfSecret);
    //router.route().handler(csrfHandler);
    //router.routeWithRegex("^(?!/csp-report-endpoint|/eventbus|/logout).*$").handler(csrfHandler);
    router.routeWithRegex("^(?!/csp-report-endpoint).*$").handler(csrfHandler);
    
        
    // HSTS Handler
    router.route().handler(HSTSHandler.create());

    // CSP handler
    router.route().handler(CSPHandler.create()
      .addDirective("default-src", "auth.smartconsultor.com")
      .addDirective("default-src", "'unsafe-inline'")
      .addDirective("default-src", "'unsafe-eval'")
      .addDirective("report-uri", "/csp-report-endpoint")
    );
    
    // Endpoint để nhận báo cáo CSP
    router.post("/csp-report-endpoint").handler(this::handleCspReport);  

    // XFrame handler
    router.route().handler(XFrameHandler.create(XFrameHandler.DENY));

    // Cors Handler
    enableCorsSupport(router); 
    // add security header
    addSecurityHeaders(router); 
    // static content
    router.route("/*").handler(StaticHandler.create().setCachingEnabled(true));
    
    // errorHandler
    router.route().failureHandler(ErrorHandler.create(vertx)); 

    // create a oauth2 handler for Keycloak
    // Tạo PemTrustOptions từ chứng chỉ CA
    PemTrustOptions trustOptions = new PemTrustOptions().addCertPath("ca.crt");
    // tạo client option
    HttpClientOptions httpClientOptions = new HttpClientOptions()
        .setSsl(true)
        .setTrustOptions(trustOptions);    
    JsonObject jsonKeycloak=config().getJsonObject("keycloak");
    String callbackUrl=jsonKeycloak.getString("callback_url");
    String callbackRoute=callbackUrl.substring(callbackUrl.indexOf('/', callbackUrl.indexOf("://") + 3));
    keycloakAuthProvider = KeycloakAuth.create(vertx,OAuth2FlowType.AUTH_CODE, jsonKeycloak, httpClientOptions);
    OAuth2AuthHandler keycloakOAuth2 = OAuth2AuthHandler
        .create(vertx, keycloakAuthProvider, callbackUrl)
        .setupCallback(router.route(callbackRoute));

    // protect "/api/*" by keycloakOAuth2
    router.route("/api/*").handler(keycloakOAuth2);  
    /*   
    // protect "/sys/*" by keycloakOAuth2
    router.route("/sys/*").handler(keycloakOAuth2).handler(ctx -> {
      // Kiểm tra xem user có role "admin" không
      User user = ctx.user();
      if (user != null && user.principal().getJsonArray("roles").contains("admin")) {
          // Lấy địa chỉ IP của client
          String ipAddress = ctx.request().remoteAddress().host();
          
          // Kiểm tra xem IP có phải là IP nội bộ trong k8s hay không
          if (RequestUtil.isInternalIp(ipAddress)) {
              // IP nằm trong dải nội bộ
              ctx.next(); // Cho phép tiếp tục xử lý
          } else {
              // IP không hợp lệ
              ctx.response().setStatusCode(403).end("Forbidden: Access is only allowed from internal IPs.");
          }
      } else {
          // User không phải là admin
          ctx.response().setStatusCode(403).end("Forbidden: Only admin can access this resource.");
      }
    });
    */       
    // check active
    enableHealthReadiness(router);
    // websocket
    router.route("/eventbus*").handler(keycloakOAuth2);            
    // Allow events for the designated addresses in/out of the event bus bridge
    SockJSBridgeOptions optsSockJSBridge = new SockJSBridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddress("Free"))
      .addOutboundPermitted(new PermittedOptions().setAddress("Vip"))
      .addOutboundPermitted(new PermittedOptions().setAddress("SuperVip"))
      .addOutboundPermitted(new PermittedOptions().setAddress("Diamond"));
    // Create the event bus bridge and add it to the router.
    //SockJSHandlerOptions optsSockJSHandler = new SockJSHandlerOptions()
    //  .setRegisterWriteHandler(true);
    //SockJSHandler ebHandler = SockJSHandler.create(vertx, optsSockJSHandler);
    SockJSHandler ebHandler = SockJSHandler.create(vertx);
    router.route("/eventbus*").subRouter(ebHandler.bridge(optsSockJSBridge, event -> {
      if (event.type() == BridgeEventType.SOCKET_CREATED) {
        String sessionID = event.socket().webSession().id();
        event.socket().webSession().put(sessionID,event.socket());
      } else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
        String sessionID = event.socket().webSession().id();
        event.socket().webSession().remove(sessionID);
      } else if (event.type() == BridgeEventType.RECEIVE) {
        JsonObject body = (JsonObject) event.getRawMessage().getValue("body");
        
        event.socket().write(new JsonObject().put("body", "Ack").encodePrettily());
      }
      // This signals that it's ok to process the event
      event.complete(true);      
    }));
    /* 
    EventBus eb = vertx.eventBus();
    // Register to listen for messages coming IN to the server
    eb.consumer("chat.to.server").handler(message -> {
      // Create a timestamp string
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
      // Send the message back out to all clients with the timestamp prepended.
      eb.publish("chat.to.client", timestamp + ": " + message.body());
    });
    */          
    // protect "/login" and redirect to home page after successful authentication
    router.route("/login").handler(keycloakOAuth2).handler(ctx -> {
      ctx.redirect("/"); // redirect to your desired URL after successful authentication
    });
    // Đường dẫn để xuất khẩu các chỉ số Prometheus
    router.route("/sys/metrics").handler(PrometheusScrapingHandler.create());
    // api test
    router.get("/api/*").handler(this::dispatchRequests); 
    // logout
    router.post("/logout").handler(this::logoutHandler);    
    //uaa    
    router.get("/uaa").handler(this::authUaaHandler);


    vertx.eventBus().consumer("cluster-status", message -> {
      ClusterManager clusterManager = ((VertxInternal) vertx).getClusterManager();
      List<String> nodeIds = clusterManager.getNodes();
      message.reply(nodeIds.toString());
    });

    // Để kiểm tra thông tin cluster node từ HTTP request
    router.get("/sys/cluster-status").handler(ctx -> {
      vertx.eventBus().<String>request("cluster-status", "", reply -> {
        if (reply.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(reply.result().body());
        } else {
          ctx.response().setStatusCode(500).end(reply.cause().getMessage());
        }
      });
    });    
  }
  // end::router[]
  @Override
  public void stop(Promise<Void> stopPromise) {
    // Perform any custom shutdown logic here
    logger.info("Shutting down GatewayVerticle...");
    // Complete the stop promise
    stopPromise.complete();
  }  

  // tag::dispatchRequests[]
  private void dispatchRequests(RoutingContext rc) {
    HttpServerRequest request = rc.request();

    int initialOffset = 5; // length of `/api/`
    String path = request.path();
    if (path.length() <= initialOffset) {
      notFound(rc);
      return;
    }
    String[] pathParts = path.split("/");
    String serviceAddress = pathParts[2]; 

    // Tạo JSON chứa thông tin cần thiết   
    JsonObject requestData = new JsonObject()
        .put("method", request.method().name())
        .put("params", request.params());

    vertx.eventBus().<String>request(serviceAddress, requestData.encode())
      .map(Message::body)
      .onSuccess(reply -> {
        rc.response()
        .putHeader("content-type", "application/json")
        .end(reply);
      })
      .onFailure(error -> {
        logger.error("Failed to receive reply from {}: {}", serviceAddress , error);
        rc.response().setStatusCode(500).end("Internal Server Error");
      }); 
  } 
  // end::dispatchRequests[]

  // CSRF code
  private static String generateCsrfSecret() {
      try {
          // Sử dụng SecureRandom để tạo một salt ngẫu nhiên
          SecureRandom random = new SecureRandom();
          byte[] salt = new byte[16];
          random.nextBytes(salt);

          // Tạo một CSRF secret từ salt và thời gian hiện tại
          String csrfSeed = Base64.getEncoder().encodeToString(salt) + System.currentTimeMillis();

          // Sử dụng SHA-256 để băm CSRF secret
          MessageDigest digest = MessageDigest.getInstance("SHA-256");
          byte[] hash = digest.digest(csrfSeed.getBytes());

          // Chuyển đổi kết quả băm thành một chuỗi hex
          StringBuilder hexString = new StringBuilder();
          for (byte b : hash) {
              String hex = Integer.toHexString(0xff & b);
              if (hex.length() == 1) hexString.append('0');
              hexString.append(hex);
          }
          return hexString.toString();
      } catch (NoSuchAlgorithmException e) {
          logger.error(e.getMessage());
          return null;
      }
  } 
  private void addSecurityHeaders(Router router) {
    router.route().handler(ctx -> { 
      ctx.response()
        .putHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
        .putHeader("X-Content-Type-Options", "nosniff")
        .putHeader("Referrer-Policy", "no-referrer-when-downgrade")
        .putHeader("Feature-Policy", "geolocation 'self'; microphone 'none'; camera 'none'")
        .putHeader("Permissions-Policy", "geolocation=(self), microphone=()");
      ctx.next();
    });
  }  
  // Phương thức xử lý báo cáo CSP
  private void handleCspReport(RoutingContext rc) {
    // Extract the report body
    JsonObject reportJson = rc.body().asJsonObject();
    // Optionally, you can log or process the CSP report
    logger.info("Received CSP report: " + reportJson.encodePrettily());
    // Respond with a success status
    rc.response()
      .setStatusCode(200)
      .end();
  }  
  // logout
  private void logoutHandler(RoutingContext context) {
    User user = context.user();
    if (user == null || user.principal() == null) {
        context.response().setStatusCode(401).end("Unauthorized");
        return;
    }

    String accessToken = user.principal().getString("access_token");
    String refreshToken = user.principal().getString("refresh_token");

    if (accessToken == null || refreshToken == null) {
        context.response().setStatusCode(400).end("Invalid tokens");
        return;
    }    
    // Tạo PemTrustOptions từ chứng chỉ CA
    PemTrustOptions trustOptions = new PemTrustOptions().addCertPath("ca.crt");
    // tạo client option
    HttpClientOptions httpClientOptions = new HttpClientOptions()
        .setSsl(true)
        .setTrustOptions(trustOptions);    

    JsonObject configKeycloak=config().getJsonObject("keycloak");
    OAuth2Options options = new OAuth2Options()
    .setClientId(configKeycloak.getString("resource"))
    .setClientSecret(configKeycloak.getJsonObject("credentials").getString("secret"))
    .setSite(configKeycloak.getString("auth-server-url")) 
    .setLogoutPath("/realms/" + configKeycloak.getString("realm") + "/protocol/openid-connect/logout")
    .setHttpClientOptions(httpClientOptions);
    
    OAuth2API oauth2API = new OAuth2API(vertx,options);
    oauth2API.logout(accessToken, refreshToken)
      .onSuccess(v -> {
        context.clearUser();
        context.session().destroy();
        context.response().setStatusCode(204).end();
      })
      .onFailure(err -> {
        // Log error
        logger.error(err.getMessage());
        context.response().setStatusCode(500).end(); 
      });
  }

  private void authUaaHandler(RoutingContext context) {
    if (context.user() != null) {
      String username = context.user().principal().getString("username");
      if (username == null) {
        context.fail(404);
      } else {
        // Tạo JSON chứa thông tin cần thiết   
        JsonObject requestData = new JsonObject()
            .put("method", "GET")
            .put("params", new JsonObject()
                              .put("username", username)
                              .put("type","one")); //one, all
        vertx.eventBus().<String>request("account", requestData.encode())
          .map(Message::body)
          .onSuccess(reply -> {
            context.response()
            .putHeader("content-type", "application/json")
            .end(reply);
          })
          .onFailure(error -> {
            logger.error("Failed to receive reply from account service: {}" , error);
            context.response().setStatusCode(500).end("Internal Server Error");
          }); 
      }
    } else {
      context.fail(401);
    }
  }  
}
