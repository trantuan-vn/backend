package com.smartconsultor.microservice.gateway.verticle;

import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class GatewayVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticle.class);

    private final AuthRouter authRouter;
    private final WebSocketHandler webSocketHandler;
    private final AppConfig appConfig;
    private final WebClient webClient;
    private final PulsarService pulsarService;
    private final WebSocketManager webSocketManager;
    private final SlotManager slotManager;
    private final SessionStore sessionStore;
    private final GeoIPService geoIPService;

    @Inject
    public GatewayVerticle(AuthRouter authRouter, WebSocketHandler webSocketHandler,
                          AppConfig appConfig, WebClient webClient, PulsarService pulsarService,
                          WebSocketManager webSocketManager, SlotManager slotManager,
                          SessionStore sessionStore, GeoIPService geoIPService) {
        this.authRouter = authRouter;
        this.webSocketHandler = webSocketHandler;
        this.appConfig = appConfig;
        //webClient
        this.webClient = webClient;
        //pulsar
        this.pulsarService = pulsarService;
        //local
        this.webSocketManager = webSocketManager;
        this.slotManager = slotManager;
        //rocksdb
        this.sessionStore = sessionStore;
        //geo
        this.geoIPService = geoIPService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            HttpServerOptions options = new HttpServerOptions()
                .setPort(appConfig.getServer().getPort())
                .setHost("0.0.0.0")
                .setIdleTimeout(60)
                .setMaxHeaderSize(8192)
                .setMaxInitialLineLength(4096)
                .setTcpKeepAlive(true)
                .setCompressionSupported(true)
                .setHandle100ContinueAutomatically(true)
                .setUseAlpn(true);

            HttpServer server = vertx.createHttpServer(options);

            RouterBuilder.create(vertx, "openapi/gateway-auth-openapi.yaml", ar -> {
                if (ar.failed()) {
                    logger.error("❌ Failed to load OpenAPI spec", ar.cause());
                    startPromise.fail(ar.cause());
                    return;
                }

                try {
                    RouterBuilder routerBuilder = ar.result();
                    Router router = routerBuilder.createRouter();
                    router.route("/static/*").handler(
                        StaticHandler.create()
                            .setCachingEnabled(true)
                            .setMaxAgeSeconds(86400)
                            .setDirectoryListing(false)
                            .setIncludeHidden(false)
                            .setAlwaysAsyncFS(true)
                            .setEnableFSTuning(true)
                            .setCacheEntryTimeout(3600 * 1000)
                            .setFilesReadOnly(true)
                    );

                    authRouter.mount(router);

                    server
                        .webSocketHandler(webSocketHandler)
                        .requestHandler(router)
                        .listen(appConfig.getServer().getPort(), http -> {
                            if (http.succeeded()) {
                                logger.info("✅ Server started on port {}", appConfig.getServer().getPort());
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
        logger.info("🛑 Initiating GatewayVerticle shutdown...");

        if (webClient == null || pulsarService == null ||
            webSocketManager == null || slotManager == null ||
            sessionStore == null || geoIPService == null) {
            logger.error("❌ One or more components are null during shutdown");
            stopPromise.fail("Null component detected");
            return;
        }

        Future<Void> webClientClose = Future.future(promise -> {
            webClient.close();
            promise.complete();
        });
        Future<Void> slotManagerClose = Future.future(promise -> {
            slotManager.close();
            promise.complete();
        });
        Future<Void> sessionStoreClose = Future.future(promise -> {
            sessionStore.close();
            promise.complete();
        });
        Future<Void> geoIPServiceClose = Future.future(promise -> {
            geoIPService.close();
            promise.complete();
        });

        Future<Void> pulsarShutdown = pulsarService.shutdown();
        Future<Void> wsShutdown = webSocketManager.shutdown();
        Future<Void> vertxShutdown = vertx.close();

        List<Future> shutdownFutures = List.of(
            pulsarShutdown,
            wsShutdown,
            webClientClose,
            slotManagerClose,
            sessionStoreClose,
            geoIPServiceClose,
            vertxShutdown
        );

        CompositeFuture.all(shutdownFutures).onComplete(ar -> {
            if (ar.succeeded()) {
                logger.info("✅ All services shut down successfully.");
                stopPromise.complete();
            } else {
                logger.error("❌ Shutdown failed for one or more services: {}", ar.cause().getMessage(), ar.cause());
                stopPromise.fail(ar.cause());
            }
        });
    }
}