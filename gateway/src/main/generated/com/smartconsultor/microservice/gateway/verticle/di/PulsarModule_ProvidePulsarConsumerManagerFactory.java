package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
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
public final class PulsarModule_ProvidePulsarConsumerManagerFactory implements Factory<PulsarConsumerManager> {
  private final PulsarModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public PulsarModule_ProvidePulsarConsumerManagerFactory(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public PulsarConsumerManager get() {
    return providePulsarConsumerManager(module, vertxProvider.get(), appConfigProvider.get(), webSocketManagerProvider.get());
  }

  public static PulsarModule_ProvidePulsarConsumerManagerFactory create(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    return new PulsarModule_ProvidePulsarConsumerManagerFactory(module, vertxProvider, appConfigProvider, webSocketManagerProvider);
  }

  public static PulsarConsumerManager providePulsarConsumerManager(PulsarModule instance,
      Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarConsumerManager(vertx, appConfig, webSocketManager));
  }
}
