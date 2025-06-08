package com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.impl;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
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
public final class AuthRemoteDataSourceImpl_Factory implements Factory<AuthRemoteDataSourceImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<WebClient> webClientProvider;

  private final Provider<AppConfig> appConfigProvider;

  public AuthRemoteDataSourceImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<WebClient> webClientProvider, Provider<AppConfig> appConfigProvider) {
    this.vertxProvider = vertxProvider;
    this.webClientProvider = webClientProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public AuthRemoteDataSourceImpl get() {
    return newInstance(vertxProvider.get(), webClientProvider.get(), appConfigProvider.get());
  }

  public static AuthRemoteDataSourceImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<WebClient> webClientProvider, Provider<AppConfig> appConfigProvider) {
    return new AuthRemoteDataSourceImpl_Factory(vertxProvider, webClientProvider, appConfigProvider);
  }

  public static AuthRemoteDataSourceImpl newInstance(Vertx vertx, WebClient webClient,
      AppConfig appConfig) {
    return new AuthRemoteDataSourceImpl(vertx, webClient, appConfig);
  }
}
