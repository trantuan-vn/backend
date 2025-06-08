package com.smartconsultor.microservice.gateway.verticle.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class CoreModule_ProvideVertxFactory implements Factory<Vertx> {
  private final CoreModule module;

  public CoreModule_ProvideVertxFactory(CoreModule module) {
    this.module = module;
  }

  @Override
  public Vertx get() {
    return provideVertx(module);
  }

  public static CoreModule_ProvideVertxFactory create(CoreModule module) {
    return new CoreModule_ProvideVertxFactory(module);
  }

  public static Vertx provideVertx(CoreModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideVertx());
  }
}
