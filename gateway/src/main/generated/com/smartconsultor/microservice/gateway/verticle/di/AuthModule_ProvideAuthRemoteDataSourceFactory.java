package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
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
public final class AuthModule_ProvideAuthRemoteDataSourceFactory implements Factory<AuthRemoteDataSource> {
  private final AuthModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<WebClient> webClientProvider;

  private final Provider<AppConfig> appConfigProvider;

  public AuthModule_ProvideAuthRemoteDataSourceFactory(AuthModule module,
      Provider<Vertx> vertxProvider, Provider<WebClient> webClientProvider,
      Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.webClientProvider = webClientProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public AuthRemoteDataSource get() {
    return provideAuthRemoteDataSource(module, vertxProvider.get(), webClientProvider.get(), appConfigProvider.get());
  }

  public static AuthModule_ProvideAuthRemoteDataSourceFactory create(AuthModule module,
      Provider<Vertx> vertxProvider, Provider<WebClient> webClientProvider,
      Provider<AppConfig> appConfigProvider) {
    return new AuthModule_ProvideAuthRemoteDataSourceFactory(module, vertxProvider, webClientProvider, appConfigProvider);
  }

  public static AuthRemoteDataSource provideAuthRemoteDataSource(AuthModule instance, Vertx vertx,
      WebClient webClient, AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthRemoteDataSource(vertx, webClient, appConfig));
  }
}
