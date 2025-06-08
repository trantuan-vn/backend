package com.smartconsultor.microservice.business.adapter.flink.process;

import com.smartconsultor.microservice.business.application.usecase.DepositUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class TransactionProcessFunction_Factory implements Factory<TransactionProcessFunction> {
  private final Provider<DepositUseCase> depositUseCaseProvider;

  public TransactionProcessFunction_Factory(Provider<DepositUseCase> depositUseCaseProvider) {
    this.depositUseCaseProvider = depositUseCaseProvider;
  }

  @Override
  public TransactionProcessFunction get() {
    return newInstance(depositUseCaseProvider.get());
  }

  public static TransactionProcessFunction_Factory create(
      Provider<DepositUseCase> depositUseCaseProvider) {
    return new TransactionProcessFunction_Factory(depositUseCaseProvider);
  }

  public static TransactionProcessFunction newInstance(DepositUseCase depositUseCase) {
    return new TransactionProcessFunction(depositUseCase);
  }
}
