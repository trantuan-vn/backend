package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class ApplicationModule_ProvideWalletRepositoryFactory implements Factory<WalletRepository> {
  private final ApplicationModule module;

  private final Provider<PgPool> pgPoolProvider;

  public ApplicationModule_ProvideWalletRepositoryFactory(ApplicationModule module,
      Provider<PgPool> pgPoolProvider) {
    this.module = module;
    this.pgPoolProvider = pgPoolProvider;
  }

  @Override
  public WalletRepository get() {
    return provideWalletRepository(module, pgPoolProvider.get());
  }

  public static ApplicationModule_ProvideWalletRepositoryFactory create(ApplicationModule module,
      Provider<PgPool> pgPoolProvider) {
    return new ApplicationModule_ProvideWalletRepositoryFactory(module, pgPoolProvider);
  }

  public static WalletRepository provideWalletRepository(ApplicationModule instance,
      PgPool pgPool) {
    return Preconditions.checkNotNullFromProvides(instance.provideWalletRepository(pgPool));
  }
}
