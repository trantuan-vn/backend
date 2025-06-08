package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
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
public final class LocalDataSourceModule_ProvideGeoIPServiceFactory implements Factory<GeoIPService> {
  private final LocalDataSourceModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public LocalDataSourceModule_ProvideGeoIPServiceFactory(LocalDataSourceModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public GeoIPService get() {
    return provideGeoIPService(module, vertxProvider.get(), appConfigProvider.get());
  }

  public static LocalDataSourceModule_ProvideGeoIPServiceFactory create(
      LocalDataSourceModule module, Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    return new LocalDataSourceModule_ProvideGeoIPServiceFactory(module, vertxProvider, appConfigProvider);
  }

  public static GeoIPService provideGeoIPService(LocalDataSourceModule instance, Vertx vertx,
      AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.provideGeoIPService(vertx, appConfig));
  }
}
