package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
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
    private GatewayModule gatewayModule;

    private Builder() {
    }

    public Builder gatewayModule(GatewayModule gatewayModule) {
      this.gatewayModule = Preconditions.checkNotNull(gatewayModule);
      return this;
    }

    public GatewayComponent build() {
      if (gatewayModule == null) {
        this.gatewayModule = new GatewayModule();
      }
      return new GatewayComponentImpl(gatewayModule);
    }
  }

  private static final class GatewayComponentImpl implements GatewayComponent {
    private final GatewayComponentImpl gatewayComponentImpl = this;

    Provider<Vertx> provideVertxProvider;

    Provider<WebClient> provideWebClientProvider;

    Provider<AppConfig> provideAppConfigProvider;

    Provider<WebSocketManager> provideWebSocketManagerProvider;

    Provider<SlotManager> provideSlotManagerProvider;

    Provider<PulsarService> providePulsarServiceProvider;

    Provider<AuthRemoteDataSource> provideAuthRemoteDataSourceProvider;

    Provider<AuthRepository> provideAuthRepositoryProvider;

    Provider<ExchangeCodeUseCase> provideExchangeCodeUseCaseProvider;

    Provider<RefreshUseCase> provideRefreshUseCaseProvider;

    Provider<LogoutUseCase> provideLogoutUseCaseProvider;

    Provider<AuthHandler> provideAuthHandlerProvider;

    Provider<AuthRouter> provideAuthRouterProvider;

    Provider<ValidateAccessTokenUseCase> provideValidateAccessTokenUseCaseProvider;

    Provider<WebSocketHandler> provideWebSocketHandlerProvider;

    Provider<ValidateAccessTokenHandler> provideValidateAccessTokenHandlerProvider;

    GatewayComponentImpl(GatewayModule gatewayModuleParam) {

      initialize(gatewayModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final GatewayModule gatewayModuleParam) {
      this.provideVertxProvider = DoubleCheck.provider(GatewayModule_ProvideVertxFactory.create(gatewayModuleParam));
      this.provideWebClientProvider = DoubleCheck.provider(GatewayModule_ProvideWebClientFactory.create(gatewayModuleParam, provideVertxProvider));
      this.provideAppConfigProvider = DoubleCheck.provider(GatewayModule_ProvideAppConfigFactory.create(gatewayModuleParam));
      this.provideWebSocketManagerProvider = DoubleCheck.provider(GatewayModule_ProvideWebSocketManagerFactory.create(gatewayModuleParam, provideVertxProvider));
      this.provideSlotManagerProvider = DoubleCheck.provider(GatewayModule_ProvideSlotManagerFactory.create(gatewayModuleParam, provideAppConfigProvider));
      this.providePulsarServiceProvider = DoubleCheck.provider(GatewayModule_ProvidePulsarServiceFactory.create(gatewayModuleParam, provideVertxProvider, provideAppConfigProvider, provideWebSocketManagerProvider, provideSlotManagerProvider));
      this.provideAuthRemoteDataSourceProvider = DoubleCheck.provider(GatewayModule_ProvideAuthRemoteDataSourceFactory.create(gatewayModuleParam, provideWebClientProvider, provideAppConfigProvider));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(GatewayModule_ProvideAuthRepositoryFactory.create(gatewayModuleParam, provideAuthRemoteDataSourceProvider));
      this.provideExchangeCodeUseCaseProvider = DoubleCheck.provider(GatewayModule_ProvideExchangeCodeUseCaseFactory.create(gatewayModuleParam, provideAuthRepositoryProvider));
      this.provideRefreshUseCaseProvider = DoubleCheck.provider(GatewayModule_ProvideRefreshUseCaseFactory.create(gatewayModuleParam, provideAuthRepositoryProvider));
      this.provideLogoutUseCaseProvider = DoubleCheck.provider(GatewayModule_ProvideLogoutUseCaseFactory.create(gatewayModuleParam, provideAuthRepositoryProvider));
      this.provideAuthHandlerProvider = DoubleCheck.provider(GatewayModule_ProvideAuthHandlerFactory.create(gatewayModuleParam, provideExchangeCodeUseCaseProvider, provideRefreshUseCaseProvider, provideLogoutUseCaseProvider));
      this.provideAuthRouterProvider = DoubleCheck.provider(GatewayModule_ProvideAuthRouterFactory.create(gatewayModuleParam, provideVertxProvider, provideAuthHandlerProvider));
      this.provideValidateAccessTokenUseCaseProvider = DoubleCheck.provider(GatewayModule_ProvideValidateAccessTokenUseCaseFactory.create(gatewayModuleParam, provideAuthRepositoryProvider));
      this.provideWebSocketHandlerProvider = DoubleCheck.provider(GatewayModule_ProvideWebSocketHandlerFactory.create(gatewayModuleParam, provideValidateAccessTokenUseCaseProvider, providePulsarServiceProvider, provideWebSocketManagerProvider));
      this.provideValidateAccessTokenHandlerProvider = DoubleCheck.provider(GatewayModule_ProvideValidateAccessTokenHandlerFactory.create(gatewayModuleParam, provideValidateAccessTokenUseCaseProvider));
    }

    @Override
    public void inject(Vertx vertx) {
    }

    @Override
    public void inject(GatewayVerticle gatewayVerticle) {
    }

    @Override
    public void inject(AppConfig appConfig) {
    }

    @Override
    public void inject(WebClient webClient) {
    }

    @Override
    public void inject(WebSocketManager webSocketManager) {
    }

    @Override
    public void inject(WebSocketHandler webSocketHandler) {
    }

    @Override
    public void inject(PulsarService pulsarService) {
    }

    @Override
    public void inject(AuthRouter authRouter) {
    }

    @Override
    public void inject(AuthHandler authHandler) {
    }

    @Override
    public void inject(ExchangeCodeUseCase exchangeCodeUseCase) {
    }

    @Override
    public void inject(RefreshUseCase refreshUseCase) {
    }

    @Override
    public void inject(LogoutUseCase logoutUseCase) {
    }

    @Override
    public void inject(ValidateAccessTokenHandler validateAccessTokenHandler) {
    }

    @Override
    public void inject(ValidateAccessTokenUseCase validateAccessTokenUseCase) {
    }

    @Override
    public void inject(AuthRemoteDataSource authRemoteDataSource) {
    }

    @Override
    public void inject(AuthRepository authRepository) {
    }

    @Override
    public Vertx vertx() {
      return provideVertxProvider.get();
    }

    @Override
    public WebClient webClient() {
      return provideWebClientProvider.get();
    }

    @Override
    public PulsarService pulsarService() {
      return providePulsarServiceProvider.get();
    }

    @Override
    public WebSocketManager webSocketManager() {
      return provideWebSocketManagerProvider.get();
    }

    @Override
    public AuthRouter authRouter() {
      return provideAuthRouterProvider.get();
    }

    @Override
    public AppConfig appConfig() {
      return provideAppConfigProvider.get();
    }

    @Override
    public WebSocketHandler webSocketHandler() {
      return provideWebSocketHandlerProvider.get();
    }

    @Override
    public AuthHandler authHandler() {
      return provideAuthHandlerProvider.get();
    }

    @Override
    public ExchangeCodeUseCase exchangeCodeUseCase() {
      return provideExchangeCodeUseCaseProvider.get();
    }

    @Override
    public RefreshUseCase refreshUseCase() {
      return provideRefreshUseCaseProvider.get();
    }

    @Override
    public LogoutUseCase logoutUseCase() {
      return provideLogoutUseCaseProvider.get();
    }

    @Override
    public ValidateAccessTokenHandler validateAccessTokenHandler() {
      return provideValidateAccessTokenHandlerProvider.get();
    }

    @Override
    public ValidateAccessTokenUseCase validateAccessTokenUseCase() {
      return provideValidateAccessTokenUseCaseProvider.get();
    }

    @Override
    public AuthRemoteDataSource authRemoteDataSource() {
      return provideAuthRemoteDataSourceProvider.get();
    }

    @Override
    public AuthRepository authRepository() {
      return provideAuthRepositoryProvider.get();
    }
  }
}
