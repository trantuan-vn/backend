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
public final class ExchangeCodeUseCase_Factory implements Factory<ExchangeCodeUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public ExchangeCodeUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ExchangeCodeUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ExchangeCodeUseCase_Factory create(Provider<AuthRepository> repositoryProvider) {
    return new ExchangeCodeUseCase_Factory(repositoryProvider);
  }

  public static ExchangeCodeUseCase newInstance(AuthRepository repository) {
    return new ExchangeCodeUseCase(repository);
  }
}
