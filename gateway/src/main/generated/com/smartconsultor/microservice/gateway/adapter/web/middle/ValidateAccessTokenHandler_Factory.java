package com.smartconsultor.microservice.gateway.adapter.web.middle;

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
public final class ValidateAccessTokenHandler_Factory implements Factory<ValidateAccessTokenHandler> {
  private final Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider;

  public ValidateAccessTokenHandler_Factory(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider) {
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
  }

  @Override
  public ValidateAccessTokenHandler get() {
    return newInstance(validateAccessTokenUseCaseProvider.get());
  }

  public static ValidateAccessTokenHandler_Factory create(
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider) {
    return new ValidateAccessTokenHandler_Factory(validateAccessTokenUseCaseProvider);
  }

  public static ValidateAccessTokenHandler newInstance(
      ValidateAccessTokenUseCase validateAccessTokenUseCase) {
    return new ValidateAccessTokenHandler(validateAccessTokenUseCase);
  }
}
