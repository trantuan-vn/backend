package com.smartconsultor.microservice.gateway.verticle;

import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
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
public final class GatewayVerticle_Factory implements Factory<GatewayVerticle> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AuthRouter> authRouterProvider;

  private final Provider<AppConfig> authConfigProvider;

  private final Provider<WebSocketHandler> webSocketHandlerProvider;

  public GatewayVerticle_Factory(Provider<Vertx> vertxProvider,
      Provider<AuthRouter> authRouterProvider, Provider<AppConfig> authConfigProvider,
      Provider<WebSocketHandler> webSocketHandlerProvider) {
    this.vertxProvider = vertxProvider;
    this.authRouterProvider = authRouterProvider;
    this.authConfigProvider = authConfigProvider;
    this.webSocketHandlerProvider = webSocketHandlerProvider;
  }

  @Override
  public GatewayVerticle get() {
    return newInstance(vertxProvider.get(), authRouterProvider.get(), authConfigProvider.get(), webSocketHandlerProvider.get());
  }

  public static GatewayVerticle_Factory create(Provider<Vertx> vertxProvider,
      Provider<AuthRouter> authRouterProvider, Provider<AppConfig> authConfigProvider,
      Provider<WebSocketHandler> webSocketHandlerProvider) {
    return new GatewayVerticle_Factory(vertxProvider, authRouterProvider, authConfigProvider, webSocketHandlerProvider);
  }

  public static GatewayVerticle newInstance(Vertx vertx, AuthRouter authRouter,
      AppConfig authConfig, WebSocketHandler webSocketHandler) {
    return new GatewayVerticle(vertx, authRouter, authConfig, webSocketHandler);
  }
}
