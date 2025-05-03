package com.smartconsultor.microservice.gateway.verticle.config;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AppConfig_Factory implements Factory<AppConfig> {
  @Override
  public AppConfig get() {
    return newInstance();
  }

  public static AppConfig_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppConfig newInstance() {
    return new AppConfig();
  }

  private static final class InstanceHolder {
    static final AppConfig_Factory INSTANCE = new AppConfig_Factory();
  }
}
