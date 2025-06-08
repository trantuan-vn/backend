package com.smartconsultor.microservice.business.di.module;

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
public final class ApplicationModule_ProvideVertxFactory implements Factory<Vertx> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideVertxFactory(ApplicationModule module) {
    this.module = module;
  }

  @Override
  public Vertx get() {
    return provideVertx(module);
  }

  public static ApplicationModule_ProvideVertxFactory create(ApplicationModule module) {
    return new ApplicationModule_ProvideVertxFactory(module);
  }

  public static Vertx provideVertx(ApplicationModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideVertx());
  }
}
