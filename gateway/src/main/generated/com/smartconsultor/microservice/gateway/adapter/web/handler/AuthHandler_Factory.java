package com.smartconsultor.microservice.gateway.adapter.web.handler;

import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
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
public final class AuthHandler_Factory implements Factory<AuthHandler> {
  private final Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider;

  private final Provider<RefreshUseCase> refreshUseCaseProvider;

  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  public AuthHandler_Factory(Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider,
      Provider<RefreshUseCase> refreshUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    this.exchangeCodeUseCaseProvider = exchangeCodeUseCaseProvider;
    this.refreshUseCaseProvider = refreshUseCaseProvider;
    this.logoutUseCaseProvider = logoutUseCaseProvider;
  }

  @Override
  public AuthHandler get() {
    return newInstance(exchangeCodeUseCaseProvider.get(), refreshUseCaseProvider.get(), logoutUseCaseProvider.get());
  }

  public static AuthHandler_Factory create(
      Provider<ExchangeCodeUseCase> exchangeCodeUseCaseProvider,
      Provider<RefreshUseCase> refreshUseCaseProvider,
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    return new AuthHandler_Factory(exchangeCodeUseCaseProvider, refreshUseCaseProvider, logoutUseCaseProvider);
  }

  public static AuthHandler newInstance(ExchangeCodeUseCase exchangeCodeUseCase,
      RefreshUseCase refreshUseCase, LogoutUseCase logoutUseCase) {
    return new AuthHandler(exchangeCodeUseCase, refreshUseCase, logoutUseCase);
  }
}
