package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.application.usecase.DepositUseCase;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
public final class ApplicationModule_ProvideDepositUseCaseFactory implements Factory<DepositUseCase> {
  private final ApplicationModule module;

  private final Provider<WalletRepository> walletRepositoryProvider;

  public ApplicationModule_ProvideDepositUseCaseFactory(ApplicationModule module,
      Provider<WalletRepository> walletRepositoryProvider) {
    this.module = module;
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public DepositUseCase get() {
    return provideDepositUseCase(module, walletRepositoryProvider.get());
  }

  public static ApplicationModule_ProvideDepositUseCaseFactory create(ApplicationModule module,
      Provider<WalletRepository> walletRepositoryProvider) {
    return new ApplicationModule_ProvideDepositUseCaseFactory(module, walletRepositoryProvider);
  }

  public static DepositUseCase provideDepositUseCase(ApplicationModule instance,
      WalletRepository walletRepository) {
    return Preconditions.checkNotNullFromProvides(instance.provideDepositUseCase(walletRepository));
  }
}
