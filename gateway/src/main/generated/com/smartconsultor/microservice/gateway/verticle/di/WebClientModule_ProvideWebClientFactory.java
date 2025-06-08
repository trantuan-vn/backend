package com.smartconsultor.microservice.gateway.verticle.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
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
public final class WebClientModule_ProvideWebClientFactory implements Factory<WebClient> {
  private final WebClientModule module;

  private final Provider<Vertx> vertxProvider;

  public WebClientModule_ProvideWebClientFactory(WebClientModule module,
      Provider<Vertx> vertxProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
  }

  @Override
  public WebClient get() {
    return provideWebClient(module, vertxProvider.get());
  }

  public static WebClientModule_ProvideWebClientFactory create(WebClientModule module,
      Provider<Vertx> vertxProvider) {
    return new WebClientModule_ProvideWebClientFactory(module, vertxProvider);
  }

  public static WebClient provideWebClient(WebClientModule instance, Vertx vertx) {
    return Preconditions.checkNotNullFromProvides(instance.provideWebClient(vertx));
  }
}
