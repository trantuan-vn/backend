package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.service.PulsarService;
import com.smartconsultor.microservice.gateway.adapter.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
public final class GatewayModule_ProvideWebSocketHandlerFactory implements Factory<WebSocketHandler> {
  private final GatewayModule module;

  private final Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider;

  private final Provider<PulsarService> pulsarServiceProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public GatewayModule_ProvideWebSocketHandlerFactory(GatewayModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    this.module = module;
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
    this.pulsarServiceProvider = pulsarServiceProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public WebSocketHandler get() {
    return provideWebSocketHandler(module, validateAccessTokenUseCaseProvider.get(), pulsarServiceProvider.get(), webSocketManagerProvider.get());
  }

  public static GatewayModule_ProvideWebSocketHandlerFactory create(GatewayModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    return new GatewayModule_ProvideWebSocketHandlerFactory(module, validateAccessTokenUseCaseProvider, pulsarServiceProvider, webSocketManagerProvider);
  }

  public static WebSocketHandler provideWebSocketHandler(GatewayModule instance,
      ValidateAccessTokenUseCase validateAccessTokenUseCase, PulsarService pulsarService,
      WebSocketManager webSocketManager) {
    return Preconditions.checkNotNullFromProvides(instance.provideWebSocketHandler(validateAccessTokenUseCase, pulsarService, webSocketManager));
  }
}
