package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PulsarServiceImpl_Factory implements Factory<PulsarServiceImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<SlotManager> slotManagerProvider;

  public PulsarServiceImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
    this.slotManagerProvider = slotManagerProvider;
  }

  @Override
  public PulsarServiceImpl get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get(), webSocketManagerProvider.get(), slotManagerProvider.get());
  }

  public static PulsarServiceImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider) {
    return new PulsarServiceImpl_Factory(vertxProvider, appConfigProvider, webSocketManagerProvider, slotManagerProvider);
  }

  public static PulsarServiceImpl newInstance(Vertx vertx, AppConfig appConfig,
      WebSocketManager webSocketManager, SlotManager slotManager) {
    return new PulsarServiceImpl(vertx, appConfig, webSocketManager, slotManager);
  }
}
