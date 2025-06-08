package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
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
public final class WebSocketModule_ProvideWebSocketManagerFactory implements Factory<WebSocketManager> {
  private final WebSocketModule module;

  private final Provider<Vertx> vertxProvider;

  public WebSocketModule_ProvideWebSocketManagerFactory(WebSocketModule module,
      Provider<Vertx> vertxProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
  }

  @Override
  public WebSocketManager get() {
    return provideWebSocketManager(module, vertxProvider.get());
  }

  public static WebSocketModule_ProvideWebSocketManagerFactory create(WebSocketModule module,
      Provider<Vertx> vertxProvider) {
    return new WebSocketModule_ProvideWebSocketManagerFactory(module, vertxProvider);
  }

  public static WebSocketManager provideWebSocketManager(WebSocketModule instance, Vertx vertx) {
    return Preconditions.checkNotNullFromProvides(instance.provideWebSocketManager(vertx));
  }
}
