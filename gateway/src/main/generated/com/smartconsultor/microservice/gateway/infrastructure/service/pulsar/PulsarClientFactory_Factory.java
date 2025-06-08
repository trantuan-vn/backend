package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

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
public final class PulsarClientFactory_Factory implements Factory<PulsarClientFactory> {
  private final Provider<Vertx> vertxProvider;

  public PulsarClientFactory_Factory(Provider<Vertx> vertxProvider) {
    this.vertxProvider = vertxProvider;
  }

  @Override
  public PulsarClientFactory get() {
    return newInstance(vertxProvider.get());
  }

  public static PulsarClientFactory_Factory create(Provider<Vertx> vertxProvider) {
    return new PulsarClientFactory_Factory(vertxProvider);
  }

  public static PulsarClientFactory newInstance(Vertx vertx) {
    return new PulsarClientFactory(vertx);
  }
}
