package com.smartconsultor.microservice.gateway.infrastructure.service.websocket.impl;

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
public final class WebSocketManagerImpl_Factory implements Factory<WebSocketManagerImpl> {
  private final Provider<Vertx> vertxProvider;

  public WebSocketManagerImpl_Factory(Provider<Vertx> vertxProvider) {
    this.vertxProvider = vertxProvider;
  }

  @Override
  public WebSocketManagerImpl get() {
    return newInstance(vertxProvider.get());
  }

  public static WebSocketManagerImpl_Factory create(Provider<Vertx> vertxProvider) {
    return new WebSocketManagerImpl_Factory(vertxProvider);
  }

  public static WebSocketManagerImpl newInstance(Vertx vertx) {
    return new WebSocketManagerImpl(vertx);
  }
}
