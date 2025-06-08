package com.smartconsultor.microservice.gateway.adapter.websocket;

import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class WebSocketHandler_Factory implements Factory<WebSocketHandler> {
  private final Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  private final Provider<GatewayDispatcher> dispatcherProvider;

  public WebSocketHandler_Factory(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<GatewayDispatcher> dispatcherProvider) {
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public WebSocketHandler get() {
    return newInstance(validateAccessTokenUseCaseProvider.get(), webSocketManagerProvider.get(), dispatcherProvider.get());
  }

  public static WebSocketHandler_Factory create(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<WebSocketManager> webSocketManagerProvider,
      Provider<GatewayDispatcher> dispatcherProvider) {
    return new WebSocketHandler_Factory(validateAccessTokenUseCaseProvider, webSocketManagerProvider, dispatcherProvider);
  }

  public static WebSocketHandler newInstance(ValidateAccessTokenUseCase validateAccessTokenUseCase,
      WebSocketManager webSocketManager, GatewayDispatcher dispatcher) {
    return new WebSocketHandler(validateAccessTokenUseCase, webSocketManager, dispatcher);
  }
}
