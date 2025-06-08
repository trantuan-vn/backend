package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl.GeoIPServiceImpl;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl.SessionStoreImpl;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl.SlotManagerImpl;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import javax.inject.Singleton;

@Module
public class LocalDataSourceModule {

    @Provides
    @Singleton
    public GeoIPService provideGeoIPService(Vertx vertx, AppConfig appConfig) {
        return new GeoIPServiceImpl(vertx, appConfig);
    }

    @Provides
    @Singleton
    public SlotManager provideSlotManager(Vertx vertx, AppConfig appConfig) {
        return new SlotManagerImpl(vertx, appConfig);
    }

    @Provides
    @Singleton
    public SessionStore provideSessionStore(Vertx vertx, AppConfig appConfig, SlotManager slotManager) {
        return new SessionStoreImpl(vertx, appConfig, slotManager);
    }
}