package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.domain.service.WalletService;
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
public final class ApplicationModule_ProvideWalletServiceFactory implements Factory<WalletService> {
  private final ApplicationModule module;

  public ApplicationModule_ProvideWalletServiceFactory(ApplicationModule module) {
    this.module = module;
  }

  @Override
  public WalletService get() {
    return provideWalletService(module);
  }

  public static ApplicationModule_ProvideWalletServiceFactory create(ApplicationModule module) {
    return new ApplicationModule_ProvideWalletServiceFactory(module);
  }

  public static WalletService provideWalletService(ApplicationModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideWalletService());
  }
}
