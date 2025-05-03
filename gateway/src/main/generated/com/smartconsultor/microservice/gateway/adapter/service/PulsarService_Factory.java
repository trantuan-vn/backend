package com.smartconsultor.microservice.gateway.adapter.service;

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
public final class PulsarService_Factory implements Factory<PulsarService> {
  private final Provider<AppConfig> appConfigProvider;

  private final Provider<WebSocketManager> webSocketManagerProvider;

  public PulsarService_Factory(Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    this.appConfigProvider = appConfigProvider;
    this.webSocketManagerProvider = webSocketManagerProvider;
  }

  @Override
  public PulsarService get() {
    return newInstance(appConfigProvider.get(), webSocketManagerProvider.get());
  }

  public static PulsarService_Factory create(Provider<AppConfig> appConfigProvider,
      Provider<WebSocketManager> webSocketManagerProvider) {
    return new PulsarService_Factory(appConfigProvider, webSocketManagerProvider);
  }

  public static PulsarService newInstance(AppConfig appConfig, WebSocketManager webSocketManager) {
    return new PulsarService(appConfig, webSocketManager);
  }
}
