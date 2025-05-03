package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
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
public final class GatewayModule_ProvideValidateAccessTokenHandlerFactory implements Factory<ValidateAccessTokenHandler> {
  private final GatewayModule module;

  private final Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider;

  public GatewayModule_ProvideValidateAccessTokenHandlerFactory(GatewayModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider) {
    this.module = module;
    this.validateAccessTokenUseCaseProvider = validateAccessTokenUseCaseProvider;
  }

  @Override
  public ValidateAccessTokenHandler get() {
    return provideValidateAccessTokenHandler(module, validateAccessTokenUseCaseProvider.get());
  }

  public static GatewayModule_ProvideValidateAccessTokenHandlerFactory create(GatewayModule module,
      Provider<ValidateAccessTokenUseCase> validateAccessTokenUseCaseProvider) {
    return new GatewayModule_ProvideValidateAccessTokenHandlerFactory(module, validateAccessTokenUseCaseProvider);
  }

  public static ValidateAccessTokenHandler provideValidateAccessTokenHandler(GatewayModule instance,
      ValidateAccessTokenUseCase validateAccessTokenUseCase) {
    return Preconditions.checkNotNullFromProvides(instance.provideValidateAccessTokenHandler(validateAccessTokenUseCase));
  }
}
