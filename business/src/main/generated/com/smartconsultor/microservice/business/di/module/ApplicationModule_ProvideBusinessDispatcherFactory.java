package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.adapter.dispatcher.BusinessDispatcher;
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
public final class ApplicationModule_ProvideBusinessDispatcherFactory implements Factory<BusinessDispatcher> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideBusinessDispatcherFactory(ApplicationModule module) {
    this.module = module;
  }

  @Override
  public BusinessDispatcher get() {
    return provideBusinessDispatcher(module);
  }

  public static ApplicationModule_ProvideBusinessDispatcherFactory create(
      ApplicationModule module) {
    return new ApplicationModule_ProvideBusinessDispatcherFactory(module);
  }

  public static BusinessDispatcher provideBusinessDispatcher(ApplicationModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideBusinessDispatcher());
  }
}
