package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
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
public final class GatewayModule_ProvideAuthHandlerFactory implements Factory<AuthHandler> {
  private final GatewayModule module;

  private final Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider;

  private final Provider<RefreshUseCase> refreshUseCaseProvider;

  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  public GatewayModule_ProvideAuthHandlerFactory(GatewayModule module,
      Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider,
      Provider<RefreshUseCase> refreshUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    this.module = module;
    this.exchangeCodeUseCaseProvider = exchangeCodeUseCaseProvider;
    this.refreshUseCaseProvider = refreshUseCaseProvider;
    this.logoutUseCaseProvider = logoutUseCaseProvider;
  }

  @Override
  public AuthHandler get() {
    return provideAuthHandler(module, exchangeCodeUseCaseProvider.get(), refreshUseCaseProvider.get(), logoutUseCaseProvider.get());
  }

  public static GatewayModule_ProvideAuthHandlerFactory create(GatewayModule module,
      Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider,
      Provider<RefreshUseCase> refreshUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    return new GatewayModule_ProvideAuthHandlerFactory(module, exchangeCodeUseCaseProvider, refreshUseCaseProvider, logoutUseCaseProvider);
  }

  public static AuthHandler provideAuthHandler(GatewayModule instance,
      ExchangeCodeUseCase exchangeCodeUseCase, RefreshUseCase refreshUseCase,
      LogoutUseCase logoutUseCase) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthHandler(exchangeCodeUseCase, refreshUseCase, logoutUseCase));
  }
}
