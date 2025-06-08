package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.application.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class ApplicationModule_ProvideAppConfigFactory implements Factory<AppConfig> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideAppConfigFactory(ApplicationModule module) {
    this.module = module;
  }

  @Override
  public AppConfig get() {
    return provideAppConfig(module);
  }

  public static ApplicationModule_ProvideAppConfigFactory create(ApplicationModule module) {
    return new ApplicationModule_ProvideAppConfigFactory(module);
  }

  public static AppConfig provideAppConfig(ApplicationModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideAppConfig());
  }
}
