package com.smartconsultor.microservice.gateway.infrastructure.datasources.local;

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
public final class GeoIPService_Factory implements Factory<GeoIPService> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public GeoIPService_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public GeoIPService get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get());
  }

  public static GeoIPService_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    return new GeoIPService_Factory(vertxProvider, appConfigProvider);
  }

  public static GeoIPService newInstance(Vertx vertx, AppConfig appConfig) {
    return new GeoIPService(vertx, appConfig);
  }
}
