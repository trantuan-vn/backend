package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;

import dagger.Component;
import io.vertx.core.Vertx;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    CoreModule.class,
    WebSocketModule.class,
    PulsarModule.class,
    AuthModule.class,
    LocalDataSourceModule.class,
    WebClientModule.class,
    GatewayModule.class
})
public interface GatewayComponent {
    Vertx getVertx();
    GatewayVerticle getGatewayVerticle();
}