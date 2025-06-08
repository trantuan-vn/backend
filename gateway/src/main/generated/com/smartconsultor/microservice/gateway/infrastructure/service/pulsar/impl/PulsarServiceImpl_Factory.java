package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.impl;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
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
public final class PulsarServiceImpl_Factory implements Factory<PulsarServiceImpl> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<SlotManager> slotManagerProvider;

  private final Provider<PulsarClientFactory> clientFactoryProvider;

  private final Provider<PulsarProducerManager> producerManagerProvider;

  private final Provider<PulsarConsumerManager> consumerManagerProvider;

  private final Provider<BacklogManager> backlogManagerProvider;

  public PulsarServiceImpl_Factory(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<SlotManager> slotManagerProvider,
      Provider<PulsarClientFactory> clientFactoryProvider,
      Provider<PulsarProducerManager> producerManagerProvider,
      Provider<PulsarConsumerManager> consumerManagerProvider,
      Provider<BacklogManager> backlogManagerProvider) {
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.slotManagerProvider = slotManagerProvider;
    this.clientFactoryProvider = clientFactoryProvider;
    this.producerManagerProvider = producerManagerProvider;
    this.consumerManagerProvider = consumerManagerProvider;
    this.backlogManagerProvider = backlogManagerProvider;
  }

  @Override
  public PulsarServiceImpl get() {
    return newInstance(vertxProvider.get(), appConfigProvider.get(), slotManagerProvider.get(), clientFactoryProvider.get(), producerManagerProvider.get(), consumerManagerProvider.get(), backlogManagerProvider.get());
  }

  public static PulsarServiceImpl_Factory create(Provider<Vertx> vertxProvider,
      Provider<AppConfig> appConfigProvider, Provider<SlotManager> slotManagerProvider,
      Provider<PulsarClientFactory> clientFactoryProvider,
      Provider<PulsarProducerManager> producerManagerProvider,
      Provider<PulsarConsumerManager> consumerManagerProvider,
      Provider<BacklogManager> backlogManagerProvider) {
    return new PulsarServiceImpl_Factory(vertxProvider, appConfigProvider, slotManagerProvider, clientFactoryProvider, producerManagerProvider, consumerManagerProvider, backlogManagerProvider);
  }

  public static PulsarServiceImpl newInstance(Vertx vertx, AppConfig appConfig,
      SlotManager slotManager, PulsarClientFactory clientFactory,
      PulsarProducerManager producerManager, PulsarConsumerManager consumerManager,
      BacklogManager backlogManager) {
    return new PulsarServiceImpl(vertx, appConfig, slotManager, clientFactory, producerManager, consumerManager, backlogManager);
  }
}
