package com.smartconsultor.microservice.gateway.infrastructure.service.redis.impl;

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
public final class RedisServiceImpl_Factory implements Factory<RedisServiceImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public RedisServiceImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public RedisServiceImpl get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get());
  }

  public static RedisServiceImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider) {
    return new RedisServiceImpl_Factory(vertxProvider, appConfigProvider);
  }

  public static RedisServiceImpl newInstance(Vertx vertx, AppConfig appConfig) {
    return new RedisServiceImpl(vertx, appConfig);
  }
}
