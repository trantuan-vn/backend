package com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
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
public final class SessionStoreImpl_Factory implements Factory<SessionStoreImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<SlotManager> slotManagerProvider;

  public SessionStoreImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<SlotManager> slotManagerProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.slotManagerProvider = slotManagerProvider;
  }

  @Override
  public SessionStoreImpl get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get(), slotManagerProvider.get());
  }

  public static SessionStoreImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<SlotManager> slotManagerProvider) {
    return new SessionStoreImpl_Factory(vertxProvider, appConfigProvider, slotManagerProvider);
  }

  public static SessionStoreImpl newInstance(Vertx vertx, AppConfig appConfig,
      SlotManager slotManager) {
    return new SessionStoreImpl(vertx, appConfig, slotManager);
  }
}
