package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.ext.web.client.WebClient;
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
public final class GatewayModule_ProvideGatewayVerticleFactory implements Factory<GatewayVerticle> {
  private final GatewayModule module;

  private final Provider<AuthRouter> authRouterProvider;

  private final Provider<WebSocketHandler> webSocketHandlerProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebClient> webClientProvider;

  private final Provider<PulsarService> pulsarServiceProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<SlotManager> slotManagerProvider;

  private final Provider<SessionStore> sessionStoreProvider;

  private final Provider<GeoIPService> geoIPServiceProvider;

  public GatewayModule_ProvideGatewayVerticleFactory(GatewayModule module,
      Provider<AuthRouter> authRouterProvider, Provider<WebSocketHandler> webSocketHandlerProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebClient> webClientProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider, Provider<SessionStore> sessionStoreProvider,
      Provider<GeoIPService> geoIPServiceProvider) {
    this.module = module;
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
    return provideGatewayVerticle(module, authRouterProvider.get(), webSocketHandlerProvider.get(), appConfigProvider.get(), webClientProvider.get(), pulsarServiceProvider.get(), webSocketManagerProvider.get(), slotManagerProvider.get(), sessionStoreProvider.get(), geoIPServiceProvider.get());
  }

  public static GatewayModule_ProvideGatewayVerticleFactory create(GatewayModule module,
      Provider<AuthRouter> authRouterProvider, Provider<WebSocketHandler> webSocketHandlerProvider,
      Provider<AppConfig> appConfigProvider, Provider<WebClient> webClientProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<SlotManager> slotManagerProvider, Provider<SessionStore> sessionStoreProvider,
      Provider<GeoIPService> geoIPServiceProvider) {
    return new GatewayModule_ProvideGatewayVerticleFactory(module, authRouterProvider, webSocketHandlerProvider, appConfigProvider, webClientProvider, pulsarServiceProvider, webSocketManagerProvider, slotManagerProvider, sessionStoreProvider, geoIPServiceProvider);
  }

  public static GatewayVerticle provideGatewayVerticle(GatewayModule instance,
      AuthRouter authRouter, WebSocketHandler webSocketHandler, AppConfig appConfig,
      WebClient webClient, PulsarService pulsarService, WebSocketManager webSocketManager,
      SlotManager slotManager, SessionStore sessionStore, GeoIPService geoIPService) {
    return Preconditions.checkNotNullFromProvides(instance.provideGatewayVerticle(authRouter, webSocketHandler, appConfig, webClient, pulsarService, webSocketManager, slotManager, sessionStore, geoIPService));
  }
}
