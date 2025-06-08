package com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl;

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
public final class SlotManagerImpl_Factory implements Factory<SlotManagerImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public SlotManagerImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public SlotManagerImpl get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get());
  }

  public static SlotManagerImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    return new SlotManagerImpl_Factory(vertxProvider, appConfigProvider);
  }

  public static SlotManagerImpl newInstance(Vertx vertx, AppConfig appConfig) {
    return new SlotManagerImpl(vertx, appConfig);
  }
}
