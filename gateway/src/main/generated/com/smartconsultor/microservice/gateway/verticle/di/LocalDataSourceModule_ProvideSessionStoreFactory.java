package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
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
public final class LocalDataSourceModule_ProvideSessionStoreFactory implements Factory<SessionStore> {
  private final LocalDataSourceModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<SlotManager> slotManagerProvider;

  public LocalDataSourceModule_ProvideSessionStoreFactory(LocalDataSourceModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<SlotManager> slotManagerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.slotManagerProvider = slotManagerProvider;
  }

  @Override
  public SessionStore get() {
    return provideSessionStore(module, vertxProvider.get(), appConfigProvider.get(), slotManagerProvider.get());
  }

  public static LocalDataSourceModule_ProvideSessionStoreFactory create(
      LocalDataSourceModule module, Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<SlotManager> slotManagerProvider) {
    return new LocalDataSourceModule_ProvideSessionStoreFactory(module, vertxProvider, appConfigProvider, slotManagerProvider);
  }

  public static SessionStore provideSessionStore(LocalDataSourceModule instance, Vertx vertx,
      AppConfig appConfig, SlotManager slotManager) {
    return Preconditions.checkNotNullFromProvides(instance.provideSessionStore(vertx, appConfig, slotManager));
  }
}
