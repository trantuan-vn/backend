package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.service.PulsarService;
import com.smartconsultor.microservice.gateway.adapter.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public GatewayModule_ProvidePulsarServiceFactory(GatewayModule module,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider) {
    this.module = module;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public PulsarService get() {
    return providePulsarService(module, appConfigProvider.get(), webSocketManagerProvider.get());
  }

  public static GatewayModule_ProvidePulsarServiceFactory create(GatewayModule module,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider) {
    return new GatewayModule_ProvidePulsarServiceFactory(module, appConfigProvider, webSocketManagerProvider);
  }

  public static PulsarService providePulsarService(GatewayModule instance, AppConfig appConfig,
      WebSocketManager webSocketManager) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarService(appConfig, webSocketManager));
  }
}
