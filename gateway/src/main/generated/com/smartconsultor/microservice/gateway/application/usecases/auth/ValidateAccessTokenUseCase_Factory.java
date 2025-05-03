package com.smartconsultor.microservice.gateway.application.usecases.auth;

import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
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
public final class ValidateAccessTokenUseCase_Factory implements Factory<ValidateAccessTokenUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public ValidateAccessTokenUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ValidateAccessTokenUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ValidateAccessTokenUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new ValidateAccessTokenUseCase_Factory(repositoryProvider);
  }

  public static ValidateAccessTokenUseCase newInstance(AuthRepository repository) {
    return new ValidateAccessTokenUseCase(repository);
  }
}
