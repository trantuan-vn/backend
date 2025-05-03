package com.smartconsultor.microservice.gateway.verticle;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.di.DaggerGatewayComponent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;

public class GatewayVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);

  private final AppConfig authConfig;
  private final AuthRouter authRouter;
  private final Vertx vertx;
  private final WebSocketHandler webSocketHandler;

  @Inject
  public GatewayVerticle(Vertx vertx, AuthRouter authRouter, AppConfig authConfig, WebSocketHandler webSocketHandler) {
    this.vertx = vertx;   
    this.authRouter = authRouter; 
    this.authConfig = authConfig; 
    this.webSocketHandler = webSocketHandler; 
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      // ✅ Inject dependencies trước khi dùng
      DaggerGatewayComponent.create().inject(this); 

      // ✅ Dùng vertx đã có sẵn, không tạo mới
      HttpServer server = vertx.createHttpServer();

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
          // ✅ Mount routers (từ Dagger) vào OpenAPI router
          authRouter.mount(router);

          // ✅ Start HTTP server
          server
          .webSocketHandler(webSocketHandler::handleWebSocket)          
          .requestHandler(router).listen(authConfig.getServer().getPort(), http -> {
            if (http.succeeded()) {
              logger.info("✅ Server started on port {}",authConfig.getServer().getPort());
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
}
