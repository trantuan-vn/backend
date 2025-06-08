package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.application.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
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
public final class ApplicationModule_ProvidePgPoolFactory implements Factory<PgPool> {
  private final ApplicationModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AppConfig> appConfigProvider;

  public ApplicationModule_ProvidePgPoolFactory(ApplicationModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public PgPool get() {
    return providePgPool(module, vertxProvider.get(), appConfigProvider.get());
  }

  public static ApplicationModule_ProvidePgPoolFactory create(ApplicationModule module,
      Provider<Vertx> vertxProvider, Provider<AppConfig> appConfigProvider) {
    return new ApplicationModule_ProvidePgPoolFactory(module, vertxProvider, appConfigProvider);
  }

  public static PgPool providePgPool(ApplicationModule instance, Vertx vertx, AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(instance.providePgPool(vertx, appConfig));
  }
}
