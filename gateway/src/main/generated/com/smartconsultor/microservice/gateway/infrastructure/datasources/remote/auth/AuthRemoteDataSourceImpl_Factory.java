package com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
  private final Provider<WebClient> webClientProvider;

  private final Provider<AppConfig> appConfigProvider;

  public AuthRemoteDataSourceImpl_Factory(Provider<WebClient> webClientProvider,
      Provider<AppConfig> appConfigProvider) {
    this.webClientProvider = webClientProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public AuthRemoteDataSourceImpl get() {
    return newInstance(webClientProvider.get(), appConfigProvider.get());
  }

  public static AuthRemoteDataSourceImpl_Factory create(Provider<WebClient> webClientProvider,
      Provider<AppConfig> appConfigProvider) {
    return new AuthRemoteDataSourceImpl_Factory(webClientProvider, appConfigProvider);
  }

  public static AuthRemoteDataSourceImpl newInstance(WebClient webClient, AppConfig appConfig) {
    return new AuthRemoteDataSourceImpl(webClient, appConfig);
  }
}
