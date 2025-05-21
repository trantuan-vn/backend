package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
  private final Provider<AppConfig> appConfigProvider;

  public SlotManagerImpl_Factory(Provider<AppConfig> appConfigProvider) {
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public SlotManagerImpl get() {
    return newInstance(appConfigProvider.get());
  }

  public static SlotManagerImpl_Factory create(Provider<AppConfig> appConfigProvider) {
    return new SlotManagerImpl_Factory(appConfigProvider);
  }

  public static SlotManagerImpl newInstance(AppConfig appConfig) {
    return new SlotManagerImpl(appConfig);
  }
}
