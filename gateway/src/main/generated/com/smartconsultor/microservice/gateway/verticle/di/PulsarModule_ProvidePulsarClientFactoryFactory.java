package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
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
public final class PulsarModule_ProvidePulsarClientFactoryFactory implements Factory<PulsarClientFactory> {
  private final PulsarModule module;

  private final Provider<Vertx> vertxProvider;

  public PulsarModule_ProvidePulsarClientFactoryFactory(PulsarModule module,
      Provider<Vertx> vertxProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
  }

  @Override
  public PulsarClientFactory get() {
    return providePulsarClientFactory(module, vertxProvider.get());
  }

  public static PulsarModule_ProvidePulsarClientFactoryFactory create(PulsarModule module,
      Provider<Vertx> vertxProvider) {
    return new PulsarModule_ProvidePulsarClientFactoryFactory(module, vertxProvider);
  }

  public static PulsarClientFactory providePulsarClientFactory(PulsarModule instance, Vertx vertx) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarClientFactory(vertx));
  }
}
