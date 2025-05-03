package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class GatewayModule_ProvideAppConfigFactory implements Factory<AppConfig> {
  private final GatewayModule module;

  public GatewayModule_ProvideAppConfigFactory(GatewayModule module) {
    this.module = module;
  }

  @Override
  public AppConfig get() {
    return provideAppConfig(module);
  }

  public static GatewayModule_ProvideAppConfigFactory create(GatewayModule module) {
    return new GatewayModule_ProvideAppConfigFactory(module);
  }

  public static AppConfig provideAppConfig(GatewayModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideAppConfig());
  }
}
