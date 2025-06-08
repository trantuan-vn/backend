package com.smartconsultor.microservice.gateway.verticle.di;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import javax.inject.Singleton;

@Module
public class WebClientModule {

    @Provides
    @Singleton
    public WebClient provideWebClient(Vertx vertx) {
        return WebClient.create(vertx, new WebClientOptions()
            .setUserAgent("SmartConsultor/1.0")
            .setKeepAlive(true)
            .setConnectTimeout(3000)
            .setMaxPoolSize(100));
    }
}