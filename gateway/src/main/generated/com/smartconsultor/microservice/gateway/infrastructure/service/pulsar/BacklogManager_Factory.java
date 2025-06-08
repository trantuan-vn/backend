package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
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
public final class BacklogManager_Factory implements Factory<BacklogManager> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public BacklogManager_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public BacklogManager get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get());
  }

  public static BacklogManager_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    return new BacklogManager_Factory(vertxProvider, appConfigProvider);
  }

  public static BacklogManager newInstance(Vertx vertx, AppConfig appConfig) {
    return new BacklogManager(vertx, appConfig);
  }
}
