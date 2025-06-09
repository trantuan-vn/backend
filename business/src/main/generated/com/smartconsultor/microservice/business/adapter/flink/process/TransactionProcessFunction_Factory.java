package com.smartconsultor.microservice.business.adapter.flink.process;

import com.smartconsultor.microservice.business.adapter.dispatcher.BusinessDispatcher;
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
  private final Provider<BusinessDispatcher> dispatcherProvider;

  public TransactionProcessFunction_Factory(Provider<BusinessDispatcher> dispatcherProvider) {
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public TransactionProcessFunction get() {
    return newInstance(dispatcherProvider.get());
  }

  public static TransactionProcessFunction_Factory create(
      Provider<BusinessDispatcher> dispatcherProvider) {
    return new TransactionProcessFunction_Factory(dispatcherProvider);
  }

  public static TransactionProcessFunction newInstance(BusinessDispatcher dispatcher) {
    return new TransactionProcessFunction(dispatcher);
  }
}
