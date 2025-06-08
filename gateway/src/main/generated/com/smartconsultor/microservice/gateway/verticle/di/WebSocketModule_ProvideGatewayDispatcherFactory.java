package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.websocket.GatewayDispatcher;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class WebSocketModule_ProvideGatewayDispatcherFactory implements Factory<GatewayDispatcher> {
  private final WebSocketModule module;

  public WebSocketModule_ProvideGatewayDispatcherFactory(WebSocketModule module) {
    this.module = module;
  }

  @Override
  public GatewayDispatcher get() {
    return provideGatewayDispatcher(module);
  }

  public static WebSocketModule_ProvideGatewayDispatcherFactory create(WebSocketModule module) {
    return new WebSocketModule_ProvideGatewayDispatcherFactory(module);
  }

  public static GatewayDispatcher provideGatewayDispatcher(WebSocketModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideGatewayDispatcher());
  }
}
