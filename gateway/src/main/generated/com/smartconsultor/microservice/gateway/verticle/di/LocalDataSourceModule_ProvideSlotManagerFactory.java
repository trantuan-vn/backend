package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
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
public final class LocalDataSourceModule_ProvideSlotManagerFactory implements Factory<SlotManager> {
  private final LocalDataSourceModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public LocalDataSourceModule_ProvideSlotManagerFactory(LocalDataSourceModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public SlotManager get() {
    return provideSlotManager(module, vertxProvider.get(), appConfigProvider.get());
  }

  public static LocalDataSourceModule_ProvideSlotManagerFactory create(LocalDataSourceModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    return new LocalDataSourceModule_ProvideSlotManagerFactory(module, vertxProvider, appConfigProvider);
  }

  public static SlotManager provideSlotManager(LocalDataSourceModule instance, Vertx vertx,
      AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.provideSlotManager(vertx, appConfig));
  }
}
