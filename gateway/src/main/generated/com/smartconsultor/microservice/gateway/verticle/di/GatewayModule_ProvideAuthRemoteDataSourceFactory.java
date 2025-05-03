package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.ext.web.client.WebClient;
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
public final class GatewayModule_ProvideAuthRemoteDataSourceFactory implements Factory<AuthRemoteDataSource> {
  private final GatewayModule module;

  private final Provider<WebClient> webClientProvider;

  private final Provider<AppConfig> appConfigProvider;

  public GatewayModule_ProvideAuthRemoteDataSourceFactory(GatewayModule module,
      Provider<WebClient> webClientProvider, Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.webClientProvider = webClientProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public AuthRemoteDataSource get() {
    return provideAuthRemoteDataSource(module, webClientProvider.get(), appConfigProvider.get());
  }

  public static GatewayModule_ProvideAuthRemoteDataSourceFactory create(GatewayModule module,
      Provider<WebClient> webClientProvider, Provider<AppConfig> appConfigProvider) {
    return new GatewayModule_ProvideAuthRemoteDataSourceFactory(module, webClientProvider, appConfigProvider);
  }

  public static AuthRemoteDataSource provideAuthRemoteDataSource(GatewayModule instance,
      WebClient webClient, AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthRemoteDataSource(webClient, appConfig));
  }
}
