package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
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
public final class PulsarModule_ProvidePulsarServiceFactory implements Factory<PulsarService> {
  private final PulsarModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  private final Provider<SlotManager> slotManagerProvider;

  private final Provider<PulsarClientFactory> pulsarClientFactoryProvider;

  private final Provider<PulsarProducerManager> pulsarProducerManagerProvider;

  private final Provider<PulsarConsumerManager> pulsarConsumerManagerProvider;

  private final Provider<BacklogManager> backlogManagerProvider;

  public PulsarModule_ProvidePulsarServiceFactory(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<SlotManager> slotManagerProvider,
      Provider<PulsarClientFactory> pulsarClientFactoryProvider,
      Provider<PulsarProducerManager> pulsarProducerManagerProvider,
      Provider<PulsarConsumerManager> pulsarConsumerManagerProvider,
      Provider<BacklogManager> backlogManagerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
    this.slotManagerProvider = slotManagerProvider;
    this.pulsarClientFactoryProvider = pulsarClientFactoryProvider;
    this.pulsarProducerManagerProvider = pulsarProducerManagerProvider;
    this.pulsarConsumerManagerProvider = pulsarConsumerManagerProvider;
    this.backlogManagerProvider = backlogManagerProvider;
  }

  @Override
  public PulsarService get() {
    return providePulsarService(module, vertxProvider.get(), appConfigProvider.get(), slotManagerProvider.get(), pulsarClientFactoryProvider.get(), pulsarProducerManagerProvider.get(), pulsarConsumerManagerProvider.get(), backlogManagerProvider.get());
  }

  public static PulsarModule_ProvidePulsarServiceFactory create(PulsarModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider,
      Provider<SlotManager> slotManagerProvider,
      Provider<PulsarClientFactory> pulsarClientFactoryProvider,
      Provider<PulsarProducerManager> pulsarProducerManagerProvider,
      Provider<PulsarConsumerManager> pulsarConsumerManagerProvider,
      Provider<BacklogManager> backlogManagerProvider) {
    return new PulsarModule_ProvidePulsarServiceFactory(module, vertxProvider, appConfigProvider, slotManagerProvider, pulsarClientFactoryProvider, pulsarProducerManagerProvider, pulsarConsumerManagerProvider, backlogManagerProvider);
  }

  public static PulsarService providePulsarService(PulsarModule instance, Vertx vertx,
      AppConfig appConfig, SlotManager slotManager, PulsarClientFactory pulsarClientFactory,
      PulsarProducerManager pulsarProducerManager, PulsarConsumerManager pulsarConsumerManager,
      BacklogManager backlogManager) {
    return Preconditions.checkNotNullFromProvides(instance.providePulsarService(vertx, appConfig, slotManager, pulsarClientFactory, pulsarProducerManager, pulsarConsumerManager, backlogManager));
  }
}
