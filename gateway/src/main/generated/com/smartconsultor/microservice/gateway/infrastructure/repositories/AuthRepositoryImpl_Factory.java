package com.smartconsultor.microservice.gateway.infrastructure.repositories;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<AuthRemoteDataSource> remoteDataSourceProvider;

  public AuthRepositoryImpl_Factory(Provider<AuthRemoteDataSource> remoteDataSourceProvider) {
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(remoteDataSourceProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(
      Provider<AuthRemoteDataSource> remoteDataSourceProvider) {
    return new AuthRepositoryImpl_Factory(remoteDataSourceProvider);
  }

  public static AuthRepositoryImpl newInstance(AuthRemoteDataSource remoteDataSource) {
    return new AuthRepositoryImpl(remoteDataSource);
  }
}
