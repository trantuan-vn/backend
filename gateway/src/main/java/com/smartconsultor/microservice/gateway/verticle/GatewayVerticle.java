package com.smartconsultor.microservice.gateway.verticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.di.DaggerGatewayComponent;
import com.smartconsultor.microservice.gateway.verticle.di.GatewayComponent;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

public class GatewayVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);

  private final GatewayComponent gatewayComponent;  

  public GatewayVerticle() {
    this.gatewayComponent = DaggerGatewayComponent.create();  
      // ✅ Inject dependencies trước khi dùng
    this.gatewayComponent.inject(this); 
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      // ✅ Dùng vertx đã có sẵn, không tạo mới
      Vertx vertx=gatewayComponent.vertx();
      AuthRouter authRouter=gatewayComponent.authRouter();
      WebSocketHandler webSocketHandler=gatewayComponent.webSocketHandler();
      AppConfig appConfig=gatewayComponent.appConfig();

      KeyCertOptions keyCertOptions = new PemKeyCertOptions()
          .addCertPath("/etc/tls/tls.crt")
          .addKeyPath("/etc/tls/tls.key");
          
      HttpServerOptions options = new HttpServerOptions()
          .setPort(appConfig.getServer().getPort())  // Cấu hình port cho server
          .setHost("0.0.0.0")  // Lắng nghe trên tất cả các IP
          .setIdleTimeout(60)  // Timeout nếu không có dữ liệu trong 60 giây = SSO Session Idle Timeout trong Keycloak
          .setMaxHeaderSize(8192)  // Tối đa kích thước header của request (8KB)
          .setMaxInitialLineLength(4096)  // Tối đa kích thước của dòng request đầu tiên
          .setTcpKeepAlive(true)  // Bật TCP keep-alive
          .setCompressionSupported(true)  // Bật nén cho các phản hồi HTTP/WebSocket
          .setHandle100ContinueAutomatically(true)  // Tự động xử lý mã phản hồi 100-continue
          .setSsl(true)
          .setKeyCertOptions(keyCertOptions);

      HttpServer server = vertx.createHttpServer(options);

      // ✅ Load RouterBuilder từ OpenAPI spec
      RouterBuilder.create(vertx, "openapi/gateway-auth-openapi.yaml", ar -> {
        if (ar.failed()) {
          logger.error("❌ Failed to load OpenAPI spec", ar.cause()); 
          startPromise.fail(ar.cause()); 
          return;
        }

        try {
          RouterBuilder routerBuilder = ar.result();
          Router router = routerBuilder.createRouter();
          // Cấu hình StaticHandler để phục vụ tệp tĩnh
          router.route("/static/*").handler(StaticHandler.create()
              .setCachingEnabled(true)  // Bật caching cho các tệp tĩnh
              .setCacheEntryTimeout(86400000)  // Thời gian lưu cache (24 giờ)
              .setMaxAgeSeconds(86400)  // Đặt Max-Age cho Cache-Control header (24 giờ)
          );
                    
          // ✅ Mount routers (từ Dagger) vào OpenAPI router
          authRouter.mount(router);

          // ✅ Start HTTP server
          server
          .webSocketHandler(webSocketHandler::handleWebSocket)          
          .requestHandler(router).listen(appConfig.getServer().getPort(), http -> {
            if (http.succeeded()) {
              logger.info("✅ Server started on port {}",appConfig.getServer().getPort());
              startPromise.complete();
            } else {
              logger.error("❌ Failed to start server", http.cause()); 
              startPromise.fail(http.cause());
            }
          });
        } catch (Exception e) {
          logger.error("❌ Unexpected error during router setup", e);
          startPromise.fail(e);
        }
      });

    } catch (Exception e) {
      logger.error("❌ Unexpected error during server initialization", e);
      startPromise.fail(e);
    }
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    logger.info("🛑 Shutting down GatewayVerticle...");

    Vertx vertx = gatewayComponent.vertx();
    WebClient webClient = gatewayComponent.webClient();
    PulsarService pulsarService = gatewayComponent.pulsarService();
    WebSocketManager webSocketManager = gatewayComponent.webSocketManager();
    AuthRouter authRouter = gatewayComponent.authRouter();
    AppConfig appConfig = gatewayComponent.appConfig();
    WebSocketHandler webSocketHandler = gatewayComponent.webSocketHandler();
    AuthHandler authHandler = gatewayComponent.authHandler();
    ExchangeCodeUseCase exchangeCodeUseCase = gatewayComponent.exchangeCodeUseCase();
    RefreshUseCase refreshUseCase = gatewayComponent.refreshUseCase();
    LogoutUseCase logoutUseCase = gatewayComponent.logoutUseCase();
    ValidateAccessTokenHandler validateAccessTokenHandler = gatewayComponent.validateAccessTokenHandler();
    ValidateAccessTokenUseCase validateAccessTokenUseCase = gatewayComponent.validateAccessTokenUseCase();
    AuthRemoteDataSource authRemoteDataSource = gatewayComponent.authRemoteDataSource();
    AuthRepository authRepository = gatewayComponent.authRepository();

    Future<Void> vertxShutdown=vertx.close();
    Future<Void> pulsarShutdown = pulsarService.shutdown();
    Future<Void> wsShutdown = webSocketManager.shutdown();
    webClient.close();


    CompositeFuture.all(vertxShutdown, pulsarShutdown, wsShutdown)
      .onSuccess(res -> {
        logger.info("✅ All services shutdown complete.");
        stopPromise.complete();
      })
      .onFailure(err -> {
        logger.error("❌ Failed to shutdown services", err);
        stopPromise.fail(err);
      });
  }  
}
