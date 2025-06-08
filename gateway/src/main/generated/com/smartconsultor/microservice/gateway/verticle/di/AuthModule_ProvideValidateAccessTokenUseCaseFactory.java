package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
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
public final class AuthModule_ProvideValidateAccessTokenUseCaseFactory implements Factory<ValidateAccessTokenUseCase> {
  private final AuthModule module;

  private final Provider<AuthRepository> authRepositoryProvider;

  public AuthModule_ProvideValidateAccessTokenUseCaseFactory(AuthModule module,
      Provider<AuthRepository> authRepositoryProvider) {
    this.module = module;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public ValidateAccessTokenUseCase get() {
    return provideValidateAccessTokenUseCase(module, authRepositoryProvider.get());
  }

  public static AuthModule_ProvideValidateAccessTokenUseCaseFactory create(AuthModule module,
      Provider<AuthRepository> authRepositoryProvider) {
    return new AuthModule_ProvideValidateAccessTokenUseCaseFactory(module, authRepositoryProvider);
  }

  public static ValidateAccessTokenUseCase provideValidateAccessTokenUseCase(AuthModule instance,
      AuthRepository authRepository) {
    return Preconditions.checkNotNullFromProvides(instance.provideValidateAccessTokenUseCase(authRepository));
  }
}
