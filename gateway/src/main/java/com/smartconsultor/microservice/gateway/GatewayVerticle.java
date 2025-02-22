package com.smartconsultor.microservice.gateway; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionType;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
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
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.sstore.infinispan.InfinispanSessionStore;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import com.smartconsultor.microservice.common.RestAPIVerticle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.nixxcode.jvmbrotli.dec.BrotliInputStream;
import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;

@SuppressWarnings("deprecation")
public class GatewayVerticle extends RestAPIVerticle {
  private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class); 
  private PulsarClient client = null;
  private Producer<byte[]> producer=null;
  private Consumer<byte[]> consumer=null;
  private String gatewayId=null; 
  private Cache<String, SockJSSocket> clientSockets = null;  
  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
    "(?i)(.*(--|;|\\/\\*|\\*\\/|union select|drop table|xp_cmdshell|exec|insert|update|delete).*|.*['\"].*)"
  );
  private static final Pattern CMD_INJECTION_PATTERN = Pattern.compile(
    ".*[&|;`$><!*{}()].*"
  );
  private final Map<String, AtomicInteger> ipConnections = new ConcurrentHashMap<>();
  private static final Cache<String, Boolean> receivedNonces = CacheBuilder.newBuilder()
    .expireAfterWrite(60, TimeUnit.SECONDS) // Nonce tự động hết hạn sau 60 giây
    .build();
  // tag::start[]
  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);    
    setupRouter(router);
  
    JsonObject jsonServer=config().getJsonObject("server");
    JsonObject jsonPulsar=config().getJsonObject("pulsar");
    JsonObject jsonWebsocket=config().getJsonObject("websocket");

    vertx.setPeriodic(5000, id -> { // Kiểm tra kết nối mỗi 5 giây
      vertx.executeBlocking(promise -> {
        try {
          ensurePulsarConnections(jsonPulsar);
          promise.complete();
        } catch (Exception e) {
          logger.error("Lỗi khi ensurePulsarConnections: {}", e.getMessage());
          promise.fail(e);
        }
      });      
    });

    vertx.executeBlocking(promise -> {
      try {
        // lấy thông tin pod name
        gatewayId = InetAddress.getLocalHost().getHostName();
        // khởi tạo hashmap quản lý client socket
        if (clientSockets == null) {
          clientSockets = CacheBuilder.newBuilder()
          .maximumSize(jsonWebsocket.getInteger("max_sockets")) // Giới hạn 500 kết nối
          .expireAfterAccess(30, TimeUnit.MINUTES) // Đóng sau 30 phút không hoạt động
          .removalListener((RemovalNotification<String, SockJSSocket> notification) -> {
              SockJSSocket socket = notification.getValue();
              if (socket != null) {
                socket.close(); 
              }
          })
          .build();
        }
        ensurePulsarConnections(jsonPulsar);
        promise.complete();
      } catch (Exception e) {
        promise.fail(e);
      }
      }, res -> {
        if (res.succeeded()) {
            logger.info("Pulsar Init started successfully!");
            // Create and start the HTTP server
            createHttpServer(router, jsonServer.getInteger("port"),jsonServer.getString("host"))
            .onSuccess(server -> {
              logger.info("Gateway Server started and listening on port {}", server.actualPort());
              startPromise.complete(); // Complete the verticle start promise
            })
            .onFailure(cause -> {
              logger.error("Failed to start the Gateway Server: {}", cause.getMessage());
              startPromise.fail(cause); // Fail the verticle start promise
            }); 
        } else {
            logger.error("Failed to init Pulsar: {}", res.cause().getMessage());
            startPromise.fail(res.cause());
        }
      }
    );    
  }
  private void handleMessage(Consumer<byte[]> consumer, org.apache.pulsar.client.api.Message<byte[]> msg) {
    try {
        JsonObject json = new JsonObject(new String(msg.getData()));
        String socketId = json.getString("socketId");
        String response = json.getString("data");

        SockJSSocket ws = clientSockets.getIfPresent(socketId);
        if (ws != null && ws.writeHandlerID() != null) {
            byte[] compressedPayload = compressBrotli(response);
            ws.write(Buffer.buffer(compressedPayload));
            consumer.acknowledge(msg);
        }
        else {
          logger.warn("Kết nối WebSocket không hợp lệ hoặc đã đóng cho socketId: {}", socketId);
          clientSockets.invalidate(socketId);
          consumer.negativeAcknowledge(msg);
        } 
    } catch (Exception e) {
        logger.error("Lỗi khi xử lý tin nhắn: {}", e.getMessage());
        consumer.negativeAcknowledge(msg);
    }
  }  
  private byte[] compressBrotli(String data) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      BrotliOutputStream brotliOutputStream = new BrotliOutputStream(byteArrayOutputStream)) {

      brotliOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
      brotliOutputStream.close(); // Đóng stream để hoàn tất nén

      return byteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi nén Brotli", e);
    }
  } 
  private String decompressBrotli(byte[] compressedData) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        BrotliInputStream brotliInputStream = new BrotliInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = brotliInputStream.read(buffer)) != -1) {
          byteArrayOutputStream.write(buffer, 0, bytesRead);
      }

      return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    } catch (Exception e) {
        throw new RuntimeException("Lỗi khi giải nén Brotli", e);
    }
  }
  // end::start[]
  // tag::router[]
  private void setupRouter(Router router) { 
    // body handler
    router.route().handler(BodyHandler.create()
                            .setBodyLimit(config().getJsonObject("server")
                                                    .getInteger("max_payload_size"))); //5MB 
    // Store session information on the server side
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
    OAuth2Auth keycloakAuthProvider = KeycloakAuth.create(vertx,OAuth2FlowType.AUTH_CODE, jsonKeycloak, httpClientOptions);
    OAuth2AuthHandler keycloakOAuth2 = OAuth2AuthHandler
        .create(vertx, keycloakAuthProvider, callbackUrl)
        .setupCallback(router.route(callbackRoute));

    // protect "/api/*" by keycloakOAuth2
    router.route("/api/*").handler(keycloakOAuth2);  
    
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
    SockJSHandlerOptions optsSockJSHandler = new SockJSHandlerOptions()
      .setRegisterWriteHandler(true);
    SockJSHandler ebHandler = SockJSHandler.create(vertx, optsSockJSHandler);
    
    JsonObject jsonWebsocket=config().getJsonObject("websocket");

    router.route("/eventbus*").subRouter(ebHandler.bridge(optsSockJSBridge, event -> { 
      if (event.type() == BridgeEventType.SOCKET_CREATED) {
        // lấy clientIp
        String clientIp = event.socket().remoteAddress().host();
        // Giới hạn số lượng kết nối theo IP
        ipConnections.putIfAbsent(clientIp, new AtomicInteger(0));
        if (ipConnections.get(clientIp).incrementAndGet() > jsonWebsocket.getInteger("max_connections_per_ip")) {
            logger.warn("IP {} đã vượt quá giới hạn kết nối!", clientIp);
            event.socket().close();
            event.complete(false);
            return;
        }
        // Khi đóng kết nối, giảm số lượng
        event.socket().closeHandler(v -> {
          if (ipConnections.get(clientIp).decrementAndGet() == 0) {
            ipConnections.remove(clientIp);
          }
        });
        // lấy socketId
        String socketId = event.socket().writeHandlerID();
        // Nếu chưa quá giới hạn, lưu socket vào danh sách
        clientSockets.put(socketId, event.socket());
        logger.info("Socket {} được tạo, tổng số kết nối: {}", socketId, clientSockets.size());
        // This signals that it's ok to process the event
        event.complete(true);               
      } else if (event.type() == BridgeEventType.SOCKET_CLOSED) {
        String socketId = event.socket().writeHandlerID();
        if (clientSockets.asMap().containsKey(socketId)) {
          clientSockets.invalidate(socketId);
        }
        logger.info("Socket {} đã đóng và bị xóa khỏi cache", socketId);
        event.complete(true);              
      } else if (event.type() == BridgeEventType.RECEIVE) {
        try {
          String socketId = event.socket().writeHandlerID();
          // Giải nén dữ liệu
          JsonObject json = new JsonObject(decompressBrotli(event.getRawMessage().getBinary("body")));
          // Kiểm tra dữ liệu đầu vào
          if (json == null || !json.containsKey("data")) {
            logger.warn("Dữ liệu nhận được không hợp lệ từ socket {}", socketId);
            event.complete(false);
            return;
          }
          String payload = json.getString("data"); 
          if (payload.length() > jsonWebsocket.getInteger("max_payload_size")) {
            logger.warn("Payload từ socket {} quá lớn ({}/{} bytes)", socketId, payload.length(), jsonWebsocket.getInteger("max_payload_size"));
            event.complete(false);
            return;
          }   

          Long timestamp = json.getLong("ts",new Long(0));
          String nonce = json.getString("nonce", "");
          String receivedHmac = json.getString("hmac", "");
  
          // 1️⃣ Kiểm tra timestamp (ngăn tin nhắn cũ)
          long currentTimestamp = System.currentTimeMillis();
          if (Math.abs(currentTimestamp - timestamp) > 30_000) {  // Chỉ cho phép trong 30s
              logger.warn("Phát hiện Replay Attack: timestamp không hợp lệ từ socket {}", socketId);
              event.complete(false);
              return;
          }
  
          // 2️⃣ Kiểm tra nonce (tránh trùng lặp)
          if (nonce.isEmpty() || receivedNonces.asMap().containsKey(nonce)) {
              logger.warn("Phát hiện Replay Attack từ socket {} với nonce trùng lặp: {}", socketId, nonce);
              event.complete(false);
              return;
          }
          receivedNonces.put(nonce, true); // Lưu nonce (TTL 1 phút)
  
          // 3️⃣ Kiểm tra HMAC (bảo vệ toàn vẹn dữ liệu)
          String computedHmac = computeHmac(payload + timestamp + nonce, csrfSecret);
          if (!computedHmac.equals(receivedHmac)) {
              logger.warn("Phát hiện tin nhắn giả mạo hoặc Replay Attack từ socket {}!", socketId);
              event.complete(false);
              return;
          }

          // Kiểm tra SQL Injection
          if (SQL_INJECTION_PATTERN.matcher(payload).matches()) {
            logger.warn("Phát hiện SQL Injection từ socket {}: {}", socketId, payload);
            event.complete(false);
            return;
          }

          // Kiểm tra Command Injection
          if (CMD_INJECTION_PATTERN.matcher(payload).matches()) {
              logger.warn("Phát hiện Command Injection từ socket {}: {}", socketId, payload);
              event.complete(false);
              return;
          }          
          
          // Encode dữ liệu để tránh lỗi Injection
          String sanitizedPayload = StringEscapeUtils.escapeJson(payload);
          
          String message=new JsonObject()
            .put("socketId", socketId)
            .put("gatewayId", gatewayId) // Định tuyến ngược
            .put("data", sanitizedPayload)
            .encode();

          sendMessageWithRetry(socketId, message, 0, 3);
                              
          // This signals that it's ok to process the event
          event.complete(true);                
        } catch (Exception e) {
          logger.error("BridgeEventType.RECEIVE : {}", e.getMessage());
          event.complete(false); // Không cho phép tiếp tục xử lý event
        }
      }
    }));
    
    // protect "/login" and redirect to home page after successful authentication
    router.route("/login").handler(keycloakOAuth2).handler(ctx -> {
      ctx.redirect("/"); // redirect to your desired URL after successful authentication
    });
    
    // api
    router.get("/api/*").handler(this::dispatchRequests); 
    
    // logout
    router.post("/logout").handler(this::logoutHandler);    
    
    //uaa    
    router.get("/uaa").handler(this::authUaaHandler);
    
    // Đường dẫn để xuất khẩu các chỉ số Prometheus
    router.route("/sys/metrics").handler(PrometheusScrapingHandler.create());
    
    // Để kiểm tra thông tin cluster node từ HTTP request
    router.get("/sys/cluster-status").handler(ctx -> {
      ClusterManager clusterManager = ((VertxInternal) vertx).getClusterManager();
      List<String> nodeIds = clusterManager.getNodes();
      ctx.response()
        .putHeader("content-type", "application/json")
        .end(nodeIds.toString());
    });    
  }

  private String computeHmac(String data, String secretKey) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256"); // Chọn thuật toán HMAC-SHA256
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
    mac.init(secretKeySpec);
    byte[] hmacBytes = mac.doFinal(data.getBytes()); // Tạo HMAC từ dữ liệu
    return Base64.getEncoder().encodeToString(hmacBytes); // Mã hóa HMAC thành chuỗi
  }  
  
  private void sendMessageWithRetry(String socketId, String message, int retryCount, int retryMax) {
    if (retryCount > retryMax) { // Giới hạn số lần retry
        logger.error("Gửi tin nhắn thất bại sau {} lần retry: {}", retryMax, message);
        return;
    }
    producer.newMessage()
      .key(socketId)
      .value(message.getBytes())
      .sendAsync()
      .thenRun(() -> logger.info("Tin nhắn đã gửi thành công: {}", message))
      .exceptionally(ex -> {
        int nextDelay = (int) Math.pow(2, retryCount) * 100; // 100ms, 200ms, 400ms...
        logger.warn("Lỗi khi gửi tin nhắn: {}, thử lại sau {}ms", ex.getMessage(), nextDelay);
        vertx.setTimer(nextDelay, id -> sendMessageWithRetry(socketId, message, retryCount + 1, retryMax));
        return null;
    });
  }  
  // end::router[]
  private void ensurePulsarConnections(JsonObject jsonPulsar) throws Exception {
    if (client != null) {
      if (client.isClosed()){
        logger.warn("Pulsar client is closed, reinitializing...");
        restartPulsarClient(jsonPulsar);
        logger.info("Pulsar client initialized successfully.");
        logger.warn("Pulsar producer is disconnected, reinitializing...");
        restartProducer(jsonPulsar); 
        logger.info("Pulsar producer connected successfully.");
        logger.warn("Pulsar consumer is disconnected, reinitializing...");
        restartConsumer(jsonPulsar);  
        logger.info("Pulsar consumer connected successfully.");
      }
    } else {
      restartPulsarClient(jsonPulsar);
      logger.info("Pulsar client initialized successfully.");
    }
    
    if (producer != null) {
      if (producer.isConnected() == false){
        logger.warn("Pulsar producer is disconnected, reinitializing...");
        restartProducer(jsonPulsar);  
        logger.info("Pulsar producer connected successfully.");
      }
    } else {
      restartProducer(jsonPulsar);  
      logger.info("Pulsar producer initialized successfully.");
    }

    if (consumer != null ) {
      if (consumer.isConnected() == false) {
        logger.warn("Pulsar consumer is disconnected, reinitializing...");
        restartConsumer(jsonPulsar);  
        logger.info("Pulsar consumer connected successfully.");
      }
    } else {
      restartConsumer(jsonPulsar);  
      logger.info("Pulsar consumer initialized successfully.");
    }
  }

  private void restartPulsarClient(JsonObject jsonPulsar) throws Exception {
    if (client != null) {
      client.close();
    }
    client = PulsarClient.builder()
      .serviceUrl(jsonPulsar.getString("url"))
      .connectionsPerBroker(jsonPulsar.getInteger("connectionsPerBroker")) // Tăng số kết nối tới broker để giảm độ trễ
      .ioThreads(jsonPulsar.getInteger("numIoThreads")) // Tăng số luồng IO để xử lý nhanh hơn
      .listenerThreads(jsonPulsar.getInteger("numlistenerThreads")) // Tăng số luồng lắng nghe để cải thiện hiệu suất
      .enableTcpNoDelay(true) // Gửi tin ngay lập tức
      .operationTimeout(jsonPulsar.getInteger("operationTimeout"), TimeUnit.SECONDS) 
      .keepAliveInterval(jsonPulsar.getInteger("keepAliveInterval"), TimeUnit.SECONDS) // Gửi keep-alive mỗi 30s để giữ kết nối
      .build();  
  }

  private void restartProducer(JsonObject jsonPulsar) throws Exception {
    if (client == null) {
      logger.error("Cannot create producer, Pulsar client is null.");
      return;
    }
    if (producer != null) {
        producer.close();
    }
    producer = client.newProducer()
      .topic("gateway-requests")
      .sendTimeout(0, TimeUnit.SECONDS)
      .compressionType(CompressionType.LZ4)
      .batchingMaxMessages(jsonPulsar.getInteger("batchingMaxMessagesPerBatch")) // Batch tối đa 100 message
      .batchingMaxPublishDelay(jsonPulsar.getInteger("batchingMaxPublishDelay"), TimeUnit.MILLISECONDS) // Gửi batch sau 10ms nếu chưa đủ 100 tin
      .create(); 
    
  }

  private void restartConsumer(JsonObject jsonPulsar) throws Exception {
    if (client == null) {
      logger.error("Cannot create consumer, Pulsar client is null.");
      return;
    }
    if (consumer != null) {
        consumer.close();
    }
    DeadLetterPolicy dlPolicy = DeadLetterPolicy.builder()
      .maxRedeliverCount(3) // Thử lại tối đa 3 lần
      .deadLetterTopic("dead-message-topic")
      .build();    
    consumer = client.newConsumer()
        .topic("gateway-responses")
        .subscriptionName("ws-sub")
        .subscriptionType(SubscriptionType.Key_Shared)
        .receiverQueueSize(jsonPulsar.getInteger("receiverQueueSize"))
        .messageListener(this::handleMessage)
        .deadLetterPolicy(dlPolicy)
        .subscribe();
    
  }

  private void closeResources() throws Exception {
    if (consumer != null) {
      consumer.close();
      logger.info("Pulsar consumer closed.");
      consumer = null;
    }
    if (producer != null) {
      producer.close();
      logger.info("Pulsar producer closed.");
      producer = null;
    }
    if (client != null) {
      client.close();
      logger.info("Pulsar client closed.");
      client = null;
    }
    if (clientSockets != null){
      clientSockets.cleanUp();
      logger.info("ClientSockets cleared.");
      clientSockets = null;
    }
    ipConnections.clear();
  }
  
  @Override
  public void stop(Promise<Void> stopPromise) {
    vertx.executeBlocking(promise -> {
      try {
        closeResources();
        promise.complete();
      } catch (Exception e) {
          logger.error("Lỗi khi đóng kết nối Pulsar: {}", e.getMessage());
          promise.fail(e);
      }
    }, res -> {
        if (res.succeeded()) {
            logger.info("Shutdown hoàn tất.");
            stopPromise.complete();
        } else {
            logger.error("Lỗi khi shutdown: {}", res.cause().getMessage());
            stopPromise.fail(res.cause());
        }
    });
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
