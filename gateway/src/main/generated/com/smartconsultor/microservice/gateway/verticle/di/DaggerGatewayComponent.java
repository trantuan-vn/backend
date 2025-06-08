package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.GatewayDispatcher;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import javax.annotation.processing.Generated;

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
public final class DaggerGatewayComponent {
  private DaggerGatewayComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static GatewayComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private CoreModule coreModule;

    private WebSocketModule webSocketModule;

    private PulsarModule pulsarModule;

    private AuthModule authModule;

    private LocalDataSourceModule localDataSourceModule;

    private WebClientModule webClientModule;

    private GatewayModule gatewayModule;

    private Builder() {
    }

    public Builder coreModule(CoreModule coreModule) {
      this.coreModule = Preconditions.checkNotNull(coreModule);
      return this;
    }

    public Builder webSocketModule(WebSocketModule webSocketModule) {
      this.webSocketModule = Preconditions.checkNotNull(webSocketModule);
      return this;
    }

    public Builder pulsarModule(PulsarModule pulsarModule) {
      this.pulsarModule = Preconditions.checkNotNull(pulsarModule);
      return this;
    }

    public Builder authModule(AuthModule authModule) {
      this.authModule = Preconditions.checkNotNull(authModule);
      return this;
    }

    public Builder localDataSourceModule(LocalDataSourceModule localDataSourceModule) {
      this.localDataSourceModule = Preconditions.checkNotNull(localDataSourceModule);
      return this;
    }

    public Builder webClientModule(WebClientModule webClientModule) {
      this.webClientModule = Preconditions.checkNotNull(webClientModule);
      return this;
    }

    public Builder gatewayModule(GatewayModule gatewayModule) {
      this.gatewayModule = Preconditions.checkNotNull(gatewayModule);
      return this;
    }

    public GatewayComponent build() {
      if (coreModule == null) {
        this.coreModule = new CoreModule();
      }
      if (webSocketModule == null) {
        this.webSocketModule = new WebSocketModule();
      }
      if (pulsarModule == null) {
        this.pulsarModule = new PulsarModule();
      }
      if (authModule == null) {
        this.authModule = new AuthModule();
      }
      if (localDataSourceModule == null) {
        this.localDataSourceModule = new LocalDataSourceModule();
      }
      if (webClientModule == null) {
        this.webClientModule = new WebClientModule();
      }
      if (gatewayModule == null) {
        this.gatewayModule = new GatewayModule();
      }
      return new GatewayComponentImpl(coreModule, webSocketModule, pulsarModule, authModule, localDataSourceModule, webClientModule, gatewayModule);
    }
  }

  private static final class GatewayComponentImpl implements GatewayComponent {
    private final GatewayComponentImpl gatewayComponentImpl = this;

    Provider<Vertx> provideVertxProvider;

    Provider<WebClient> provideWebClientProvider;

    Provider<AppConfig> provideAppConfigProvider;

    Provider<AuthRemoteDataSource> provideAuthRemoteDataSourceProvider;

    Provider<AuthRepository> provideAuthRepositoryProvider;

    Provider<ExchangeCodeUseCase> provideExchangeCodeUseCaseProvider;

    Provider<RefreshUseCase> provideRefreshUseCaseProvider;

    Provider<LogoutUseCase> provideLogoutUseCaseProvider;

    Provider<AuthHandler> provideAuthHandlerProvider;

    Provider<AuthRouter> provideAuthRouterProvider;

    Provider<ValidateAccessTokenUseCase> provideValidateAccessTokenUseCaseProvider;

    Provider<WebSocketManager> provideWebSocketManagerProvider;

    Provider<GatewayDispatcher> provideGatewayDispatcherProvider;

    Provider<WebSocketHandler> provideWebSocketHandlerProvider;

    Provider<SlotManager> provideSlotManagerProvider;

    Provider<PulsarClientFactory> providePulsarClientFactoryProvider;

    Provider<PulsarProducerManager> providePulsarProducerManagerProvider;

    Provider<PulsarConsumerManager> providePulsarConsumerManagerProvider;

    Provider<BacklogManager> provideBacklogManagerProvider;

    Provider<PulsarService> providePulsarServiceProvider;

    Provider<SessionStore> provideSessionStoreProvider;

    Provider<GeoIPService> provideGeoIPServiceProvider;

    Provider<GatewayVerticle> provideGatewayVerticleProvider;

    GatewayComponentImpl(CoreModule coreModuleParam, WebSocketModule webSocketModuleParam,
        PulsarModule pulsarModuleParam, AuthModule authModuleParam,
        LocalDataSourceModule localDataSourceModuleParam, WebClientModule webClientModuleParam,
        GatewayModule gatewayModuleParam) {

      initialize(coreModuleParam, webSocketModuleParam, pulsarModuleParam, authModuleParam, localDataSourceModuleParam, webClientModuleParam, gatewayModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final CoreModule coreModuleParam,
        final WebSocketModule webSocketModuleParam, final PulsarModule pulsarModuleParam,
        final AuthModule authModuleParam, final LocalDataSourceModule localDataSourceModuleParam,
        final WebClientModule webClientModuleParam, final GatewayModule gatewayModuleParam) {
      this.provideVertxProvider = DoubleCheck.provider(CoreModule_ProvideVertxFactory.create(coreModuleParam));
      this.provideWebClientProvider = DoubleCheck.provider(WebClientModule_ProvideWebClientFactory.create(webClientModuleParam, provideVertxProvider));
      this.provideAppConfigProvider = DoubleCheck.provider(CoreModule_ProvideAppConfigFactory.create(coreModuleParam));
      this.provideAuthRemoteDataSourceProvider = DoubleCheck.provider(AuthModule_ProvideAuthRemoteDataSourceFactory.create(authModuleParam, provideVertxProvider, provideWebClientProvider, provideAppConfigProvider));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(AuthModule_ProvideAuthRepositoryFactory.create(authModuleParam, provideAuthRemoteDataSourceProvider));
      this.provideExchangeCodeUseCaseProvider = DoubleCheck.provider(AuthModule_ProvideExchangeCodeUseCaseFactory.create(authModuleParam, provideAuthRepositoryProvider));
      this.provideRefreshUseCaseProvider = DoubleCheck.provider(AuthModule_ProvideRefreshUseCaseFactory.create(authModuleParam, provideAuthRepositoryProvider));
      this.provideLogoutUseCaseProvider = DoubleCheck.provider(AuthModule_ProvideLogoutUseCaseFactory.create(authModuleParam, provideAuthRepositoryProvider));
      this.provideAuthHandlerProvider = DoubleCheck.provider(AuthModule_ProvideAuthHandlerFactory.create(authModuleParam, provideExchangeCodeUseCaseProvider, provideRefreshUseCaseProvider, provideLogoutUseCaseProvider));
      this.provideAuthRouterProvider = DoubleCheck.provider(AuthModule_ProvideAuthRouterFactory.create(authModuleParam, provideVertxProvider, provideAuthHandlerProvider));
      this.provideValidateAccessTokenUseCaseProvider = DoubleCheck.provider(AuthModule_ProvideValidateAccessTokenUseCaseFactory.create(authModuleParam, provideAuthRepositoryProvider));
      this.provideWebSocketManagerProvider = DoubleCheck.provider(WebSocketModule_ProvideWebSocketManagerFactory.create(webSocketModuleParam, provideVertxProvider));
      this.provideGatewayDispatcherProvider = DoubleCheck.provider(WebSocketModule_ProvideGatewayDispatcherFactory.create(webSocketModuleParam));
      this.provideWebSocketHandlerProvider = DoubleCheck.provider(WebSocketModule_ProvideWebSocketHandlerFactory.create(webSocketModuleParam, provideValidateAccessTokenUseCaseProvider, provideWebSocketManagerProvider, provideGatewayDispatcherProvider));
      this.provideSlotManagerProvider = DoubleCheck.provider(LocalDataSourceModule_ProvideSlotManagerFactory.create(localDataSourceModuleParam, provideVertxProvider, provideAppConfigProvider));
      this.providePulsarClientFactoryProvider = DoubleCheck.provider(PulsarModule_ProvidePulsarClientFactoryFactory.create(pulsarModuleParam, provideVertxProvider));
      this.providePulsarProducerManagerProvider = DoubleCheck.provider(PulsarModule_ProvidePulsarProducerManagerFactory.create(pulsarModuleParam, provideVertxProvider, provideAppConfigProvider));
      this.providePulsarConsumerManagerProvider = DoubleCheck.provider(PulsarModule_ProvidePulsarConsumerManagerFactory.create(pulsarModuleParam, provideVertxProvider, provideAppConfigProvider, provideWebSocketManagerProvider));
      this.provideBacklogManagerProvider = DoubleCheck.provider(PulsarModule_ProvideBacklogManagerFactory.create(pulsarModuleParam, provideVertxProvider, provideAppConfigProvider, provideWebSocketManagerProvider));
      this.providePulsarServiceProvider = DoubleCheck.provider(PulsarModule_ProvidePulsarServiceFactory.create(pulsarModuleParam, provideVertxProvider, provideAppConfigProvider, provideSlotManagerProvider, providePulsarClientFactoryProvider, providePulsarProducerManagerProvider, providePulsarConsumerManagerProvider, provideBacklogManagerProvider));
      this.provideSessionStoreProvider = DoubleCheck.provider(LocalDataSourceModule_ProvideSessionStoreFactory.create(localDataSourceModuleParam, provideVertxProvider, provideAppConfigProvider, provideSlotManagerProvider));
      this.provideGeoIPServiceProvider = DoubleCheck.provider(LocalDataSourceModule_ProvideGeoIPServiceFactory.create(localDataSourceModuleParam, provideVertxProvider, provideAppConfigProvider));
      this.provideGatewayVerticleProvider = DoubleCheck.provider(GatewayModule_ProvideGatewayVerticleFactory.create(gatewayModuleParam, provideAuthRouterProvider, provideWebSocketHandlerProvider, provideAppConfigProvider, provideWebClientProvider, providePulsarServiceProvider, provideWebSocketManagerProvider, provideSlotManagerProvider, provideSessionStoreProvider, provideGeoIPServiceProvider));
    }

    @Override
    public Vertx getVertx() {
      return provideVertxProvider.get();
    }

    @Override
    public GatewayVerticle getGatewayVerticle() {
      return provideGatewayVerticleProvider.get();
    }
  }
}
