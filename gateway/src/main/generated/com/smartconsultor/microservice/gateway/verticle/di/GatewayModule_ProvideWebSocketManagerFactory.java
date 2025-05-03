package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.service.WebSocketManager;
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
public final class GatewayModule_ProvideWebSocketManagerFactory implements Factory<WebSocketManager> {
  private final GatewayModule module;

  public GatewayModule_ProvideWebSocketManagerFactory(GatewayModule module) {
    this.module = module;
  }

  @Override
  public WebSocketManager get() {
    return provideWebSocketManager(module);
  }

  public static GatewayModule_ProvideWebSocketManagerFactory create(GatewayModule module) {
    return new GatewayModule_ProvideWebSocketManagerFactory(module);
  }

  public static WebSocketManager provideWebSocketManager(GatewayModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideWebSocketManager());
  }
}
