package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
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
public final class PulsarModule_ProvidePulsarProducerManagerFactory implements Factory<PulsarProducerManager> {
  private final PulsarModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public PulsarModule_ProvidePulsarProducerManagerFactory(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public PulsarProducerManager get() {
    return providePulsarProducerManager(module, vertxProvider.get(), appConfigProvider.get());
  }

  public static PulsarModule_ProvidePulsarProducerManagerFactory create(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    return new PulsarModule_ProvidePulsarProducerManagerFactory(module, vertxProvider, appConfigProvider);
  }

  public static PulsarProducerManager providePulsarProducerManager(PulsarModule instance,
      Vertx vertx, AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarProducerManager(vertx, appConfig));
  }
}
