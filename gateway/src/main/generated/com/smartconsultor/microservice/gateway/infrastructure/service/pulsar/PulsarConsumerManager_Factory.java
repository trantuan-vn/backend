package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
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
public final class PulsarConsumerManager_Factory implements Factory<PulsarConsumerManager> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public PulsarConsumerManager_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public PulsarConsumerManager get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get(), webSocketManagerProvider.get());
  }

  public static PulsarConsumerManager_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebSocketManager> webSocketManagerProvider) {
    return new PulsarConsumerManager_Factory(vertxProvider, appConfigProvider, webSocketManagerProvider);
  }

  public static PulsarConsumerManager newInstance(Vertx vertx, AppConfig appConfig,
      WebSocketManager webSocketManager) {
    return new PulsarConsumerManager(vertx, appConfig, webSocketManager);
  }
}
