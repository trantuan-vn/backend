package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.websocket.GatewayDispatcher;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
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
public final class WebSocketModule_ProvideWebSocketHandlerFactory implements Factory<WebSocketHandler> {
  private final WebSocketModule module;

  private final Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<GatewayDispatcher> gatewayDispatcherProvider;

  public WebSocketModule_ProvideWebSocketHandlerFactory(WebSocketModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<GatewayDispatcher> gatewayDispatcherProvider) {
    this.module = module;
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
    this.gatewayDispatcherProvider = gatewayDispatcherProvider;
  }

  @Override
  public WebSocketHandler get() {
    return provideWebSocketHandler(module, validateAccessTokenUseCaseProvider.get(), webSocketManagerProvider.get(), gatewayDispatcherProvider.get());
  }

  public static WebSocketModule_ProvideWebSocketHandlerFactory create(WebSocketModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<GatewayDispatcher> gatewayDispatcherProvider) {
    return new WebSocketModule_ProvideWebSocketHandlerFactory(module, validateAccessTokenUseCaseProvider, webSocketManagerProvider, gatewayDispatcherProvider);
  }

  public static WebSocketHandler provideWebSocketHandler(WebSocketModule instance,
      ValidateAccessTokenUseCase validateAccessTokenUseCase, WebSocketManager webSocketManager,
      GatewayDispatcher gatewayDispatcher) {
    return Preconditions.checkNotNullFromProvides(instance.provideWebSocketHandler(validateAccessTokenUseCase, webSocketManager, gatewayDispatcher));
  }
}
