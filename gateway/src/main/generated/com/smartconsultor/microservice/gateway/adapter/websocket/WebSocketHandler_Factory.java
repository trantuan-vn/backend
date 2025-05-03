package com.smartconsultor.microservice.gateway.adapter.websocket;

import com.smartconsultor.microservice.gateway.adapter.service.PulsarService;
import com.smartconsultor.microservice.gateway.adapter.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
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

  private final Provider<PulsarService> pulsarServiceProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public WebSocketHandler_Factory(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
    this.pulsarServiceProvider = pulsarServiceProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public WebSocketHandler get() {
    return newInstance(validateAccessTokenUseCaseProvider.get(), pulsarServiceProvider.get(), webSocketManagerProvider.get());
  }

  public static WebSocketHandler_Factory create(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider,
      Provider<PulsarService> pulsarServiceProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    return new WebSocketHandler_Factory(validateAccessTokenUseCaseProvider, pulsarServiceProvider, webSocketManagerProvider);
  }

  public static WebSocketHandler newInstance(ValidateAccessTokenUseCase validateAccessTokenUseCase,
      PulsarService pulsarService, WebSocketManager webSocketManager) {
    return new WebSocketHandler(validateAccessTokenUseCase, pulsarService, webSocketManager);
  }
}
