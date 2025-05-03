package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
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
public final class GatewayModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final GatewayModule module;

  private final Provider<AuthRemoteDataSource> authRemoteDataSourceProvider;

  public GatewayModule_ProvideAuthRepositoryFactory(GatewayModule module,
      Provider<AuthRemoteDataSource> authRemoteDataSourceProvider) {
    this.module = module;
    this.authRemoteDataSourceProvider = authRemoteDataSourceProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(module, authRemoteDataSourceProvider.get());
  }

  public static GatewayModule_ProvideAuthRepositoryFactory create(GatewayModule module,
      Provider<AuthRemoteDataSource> authRemoteDataSourceProvider) {
    return new GatewayModule_ProvideAuthRepositoryFactory(module, authRemoteDataSourceProvider);
  }

  public static AuthRepository provideAuthRepository(GatewayModule instance,
      AuthRemoteDataSource authRemoteDataSource) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthRepository(authRemoteDataSource));
  }
}
