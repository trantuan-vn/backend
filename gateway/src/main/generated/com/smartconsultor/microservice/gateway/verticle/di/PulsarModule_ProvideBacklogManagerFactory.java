package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
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
public final class PulsarModule_ProvideBacklogManagerFactory implements Factory<BacklogManager> {
  private final PulsarModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public PulsarModule_ProvideBacklogManagerFactory(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public BacklogManager get() {
    return provideBacklogManager(module, vertxProvider.get(), appConfigProvider.get(), webSocketManagerProvider.get());
  }

  public static PulsarModule_ProvideBacklogManagerFactory create(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    return new PulsarModule_ProvideBacklogManagerFactory(module, vertxProvider, appConfigProvider, webSocketManagerProvider);
  }

  public static BacklogManager provideBacklogManager(PulsarModule instance, Vertx vertx,
      AppConfig appConfig, WebSocketManager webSocketManager) {
    return Preconditions.checkNotNullFromProvides(instance.provideBacklogManager(vertx, appConfig, webSocketManager));
  }
}
