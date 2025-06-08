package com.smartconsultor.microservice.business.infrastructure.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.pgclient.PgPool;
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
public final class WalletRepositoryImpl_Factory implements Factory<WalletRepositoryImpl> {
  private final Provider<PgPool> clientProvider;

  public WalletRepositoryImpl_Factory(Provider<PgPool> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public WalletRepositoryImpl get() {
    return newInstance(clientProvider.get());
  }

  public static WalletRepositoryImpl_Factory create(Provider<PgPool> clientProvider) {
    return new WalletRepositoryImpl_Factory(clientProvider);
  }

  public static WalletRepositoryImpl newInstance(PgPool client) {
    return new WalletRepositoryImpl(client);
  }
}
