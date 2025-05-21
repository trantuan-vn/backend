package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class GatewayModule_ProvidePulsarServiceFactory implements Factory<PulsarService> {
  private final GatewayModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<SlotManager> slotManagerProvider;

  public GatewayModule_ProvidePulsarServiceFactory(GatewayModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
    this.slotManagerProvider = slotManagerProvider;
  }

  @Override
  public PulsarService get() {
    return providePulsarService(module, vertxProvider.get(), appConfigProvider.get(), webSocketManagerProvider.get(), slotManagerProvider.get());
  }

  public static GatewayModule_ProvidePulsarServiceFactory create(GatewayModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider) {
    return new GatewayModule_ProvidePulsarServiceFactory(module, vertxProvider, appConfigProvider, webSocketManagerProvider, slotManagerProvider);
  }

  public static PulsarService providePulsarService(GatewayModule instance, Vertx vertx,
      AppConfig appConfig, WebSocketManager webSocketManager, SlotManager slotManager) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarService(vertx, appConfig, webSocketManager, slotManager));
  }
}
