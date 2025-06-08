package com.smartconsultor.microservice.gateway.verticle;

import com.smartconsultor.microservice.gateway.verticle.di.DaggerGatewayComponent;
import com.smartconsultor.microservice.gateway.verticle.di.GatewayComponent;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayLauncher extends Launcher {
    private static final Logger logger = LoggerFactory.getLogger(GatewayLauncher.class);

    public static void main(String[] args) {
        logger.info("🚀 Starting GatewayLauncher...");

        try {
            logger.debug("Creating GatewayComponent...");
            GatewayComponent component = DaggerGatewayComponent.create();
            logger.debug("GatewayComponent created successfully");

            logger.debug("Retrieving Vertx instance from GatewayComponent...");
            Vertx vertx = component.getVertx();
            logger.debug("Vertx instance retrieved");

            logger.debug("Creating GatewayVerticle with dependencies...");
            GatewayVerticle verticle = component.getGatewayVerticle();
            logger.debug("GatewayVerticle created successfully");

            // ✅ Đăng ký shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🛑 Shutdown signal received. Closing Vertx...");
                vertx.close(ar -> {
                    if (ar.succeeded()) {
                        logger.info("✅ Vertx closed successfully");
                    } else {
                        logger.error("❌ Failed to close Vertx", ar.cause());
                    }
                });
            }));
                        
            logger.info("Deploying GatewayVerticle...");
            vertx.deployVerticle(verticle, res -> {
                if (res.succeeded()) {
                    logger.info("✅ GatewayVerticle deployed successfully with deployment ID: {}", res.result());
                } else {
                    logger.error("❌ Failed to deploy GatewayVerticle", res.cause());
                }
            });
        } catch (Exception e) {
            logger.error("❌ Unexpected error during GatewayLauncher startup", e);
        }
    }
}