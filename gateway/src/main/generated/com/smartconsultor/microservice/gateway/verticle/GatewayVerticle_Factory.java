package com.smartconsultor.microservice.gateway.verticle;

import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.ext.web.client.WebClient;
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
  private final Provider<AuthRouter> authRouterProvider;

  private final Provider<WebSocketHandler> webSocketHandlerProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebClient> webClientProvider;

  private final Provider<PulsarService> pulsarServiceProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<SlotManager> slotManagerProvider;

  private final Provider<SessionStore> sessionStoreProvider;

  private final Provider<GeoIPService> geoIPServiceProvider;

  public GatewayVerticle_Factory(Provider<AuthRouter> authRouterProvider,
      Provider<WebSocketHandler> webSocketHandlerProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebClient> webClientProvider, Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider, Provider<SessionStore> sessionStoreProvider,
      Provider<GeoIPService> geoIPServiceProvider) {
    this.authRouterProvider = authRouterProvider;
    this.webSocketHandlerProvider = webSocketHandlerProvider;
    this.appConfigProvider = appConfigProvider;
    this.webClientProvider = webClientProvider;
    this.pulsarServiceProvider = pulsarServiceProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
    this.slotManagerProvider = slotManagerProvider;
    this.sessionStoreProvider = sessionStoreProvider;
    this.geoIPServiceProvider = geoIPServiceProvider;
  }

  @Override
  public GatewayVerticle get() {
    return newInstance(authRouterProvider.get(), webSocketHandlerProvider.get(), appConfigProvider.get(), webClientProvider.get(), pulsarServiceProvider.get(), webSocketManagerProvider.get(), slotManagerProvider.get(), sessionStoreProvider.get(), geoIPServiceProvider.get());
  }

  public static GatewayVerticle_Factory create(Provider<AuthRouter> authRouterProvider,
      Provider<WebSocketHandler> webSocketHandlerProvider, Provider<AppConfig> appConfigProvider,
      Provider<WebClient> webClientProvider, Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider, Provider<SessionStore> sessionStoreProvider,
      Provider<GeoIPService> geoIPServiceProvider) {
    return new GatewayVerticle_Factory(authRouterProvider, webSocketHandlerProvider, appConfigProvider, webClientProvider, pulsarServiceProvider, webSocketManagerProvider, slotManagerProvider, sessionStoreProvider, geoIPServiceProvider);
  }

  public static GatewayVerticle newInstance(AuthRouter authRouter,
      WebSocketHandler webSocketHandler, AppConfig appConfig, WebClient webClient,
      PulsarService pulsarService, WebSocketManager webSocketManager, SlotManager slotManager,
      SessionStore sessionStore, GeoIPService geoIPService) {
    return new GatewayVerticle(authRouter, webSocketHandler, appConfig, webClient, pulsarService, webSocketManager, slotManager, sessionStore, geoIPService);
  }
}
