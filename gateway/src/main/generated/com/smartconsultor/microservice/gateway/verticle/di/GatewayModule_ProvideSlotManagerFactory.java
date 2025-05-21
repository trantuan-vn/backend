package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class GatewayModule_ProvideSlotManagerFactory implements Factory<SlotManager> {
  private final GatewayModule module;

  private final Provider<AppConfig> appConfigProvider;

  public GatewayModule_ProvideSlotManagerFactory(GatewayModule module,
      Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public SlotManager get() {
    return provideSlotManager(module, appConfigProvider.get());
  }

  public static GatewayModule_ProvideSlotManagerFactory create(GatewayModule module,
      Provider<AppConfig> appConfigProvider) {
    return new GatewayModule_ProvideSlotManagerFactory(module, appConfigProvider);
  }

  public static SlotManager provideSlotManager(GatewayModule instance, AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.provideSlotManager(appConfig));
  }
}
