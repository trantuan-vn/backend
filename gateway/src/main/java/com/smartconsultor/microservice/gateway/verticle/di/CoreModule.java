package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javax.inject.Singleton;

@Module
public class CoreModule {

    @Provides
    @Singleton
    public Vertx provideVertx() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        VertxOptions options = new VertxOptions()
            .setEventLoopPoolSize(cpuCores)
            .setWorkerPoolSize(cpuCores * 4)
            .setPreferNativeTransport(true);
        return Vertx.vertx(options);
    }

    @Provides
    @Singleton
    public AppConfig provideAppConfig() {
        return AppConfig.load("config/app-config.json");
    }
}