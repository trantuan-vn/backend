package com.smartconsultor.microservice.business.application.usecase;

import com.smartconsultor.microservice.business.domain.service.WalletService;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class DepositUseCase_MembersInjector implements MembersInjector<DepositUseCase> {
  private final Provider<WalletService> walletServiceProvider;

  public DepositUseCase_MembersInjector(Provider<WalletService> walletServiceProvider) {
    this.walletServiceProvider = walletServiceProvider;
  }

  public static MembersInjector<DepositUseCase> create(
      Provider<WalletService> walletServiceProvider) {
    return new DepositUseCase_MembersInjector(walletServiceProvider);
  }

  @Override
  public void injectMembers(DepositUseCase instance) {
    injectWalletService(instance, walletServiceProvider.get());
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.application.usecase.DepositUseCase.walletService")
  public static void injectWalletService(DepositUseCase instance, WalletService walletService) {
    instance.walletService = walletService;
  }
}
