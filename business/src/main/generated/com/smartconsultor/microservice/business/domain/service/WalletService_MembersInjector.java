package com.smartconsultor.microservice.business.domain.service;

import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
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
public final class WalletService_MembersInjector implements MembersInjector<WalletService> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  public WalletService_MembersInjector(Provider<WalletRepository> walletRepositoryProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  public static MembersInjector<WalletService> create(
      Provider<WalletRepository> walletRepositoryProvider) {
    return new WalletService_MembersInjector(walletRepositoryProvider);
  }

  @Override
  public void injectMembers(WalletService instance) {
    injectWalletRepository(instance, walletRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.domain.service.WalletService.walletRepository")
  public static void injectWalletRepository(WalletService instance,
      WalletRepository walletRepository) {
    instance.walletRepository = walletRepository;
  }
}
