package com.smartconsultor.microservice.business;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.adapter.flink.process.TransactionProcessFunction;
import com.smartconsultor.microservice.business.application.config.AppConfig;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;

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
public final class FlinkJobRunner_MembersInjector implements MembersInjector<FlinkJobRunner> {
  private final Provider<TransactionProcessFunction> transactionProcessFunctionProvider;

  private final Provider<DeserializationSchema<GatewayMessage>> gatewayMessageDeserializationProvider;

  private final Provider<SerializationSchema<TransactionResult>> resultMessageSerializationProvider;

  private final Provider<AppConfig> appConfigProvider;

  public FlinkJobRunner_MembersInjector(
      Provider<TransactionProcessFunction> transactionProcessFunctionProvider,
      Provider<DeserializationSchema<GatewayMessage>> gatewayMessageDeserializationProvider,
      Provider<SerializationSchema<TransactionResult>> resultMessageSerializationProvider,
      Provider<AppConfig> appConfigProvider) {
    this.transactionProcessFunctionProvider = transactionProcessFunctionProvider;
    this.gatewayMessageDeserializationProvider = gatewayMessageDeserializationProvider;
    this.resultMessageSerializationProvider = resultMessageSerializationProvider;
    this.appConfigProvider = appConfigProvider;
  }

  public static MembersInjector<FlinkJobRunner> create(
      Provider<TransactionProcessFunction> transactionProcessFunctionProvider,
      Provider<DeserializationSchema<GatewayMessage>> gatewayMessageDeserializationProvider,
      Provider<SerializationSchema<TransactionResult>> resultMessageSerializationProvider,
      Provider<AppConfig> appConfigProvider) {
    return new FlinkJobRunner_MembersInjector(transactionProcessFunctionProvider, gatewayMessageDeserializationProvider, resultMessageSerializationProvider, appConfigProvider);
  }

  @Override
  public void injectMembers(FlinkJobRunner instance) {
    injectTransactionProcessFunction(instance, transactionProcessFunctionProvider.get());
    injectGatewayMessageDeserialization(instance, gatewayMessageDeserializationProvider.get());
    injectResultMessageSerialization(instance, resultMessageSerializationProvider.get());
    injectAppConfig(instance, appConfigProvider.get());
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.FlinkJobRunner.transactionProcessFunction")
  public static void injectTransactionProcessFunction(FlinkJobRunner instance,
      TransactionProcessFunction transactionProcessFunction) {
    instance.transactionProcessFunction = transactionProcessFunction;
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.FlinkJobRunner.gatewayMessageDeserialization")
  public static void injectGatewayMessageDeserialization(FlinkJobRunner instance,
      DeserializationSchema<GatewayMessage> gatewayMessageDeserialization) {
    instance.gatewayMessageDeserialization = gatewayMessageDeserialization;
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.FlinkJobRunner.resultMessageSerialization")
  public static void injectResultMessageSerialization(FlinkJobRunner instance,
      SerializationSchema<TransactionResult> resultMessageSerialization) {
    instance.resultMessageSerialization = resultMessageSerialization;
  }

  @InjectedFieldSignature("com.smartconsultor.microservice.business.FlinkJobRunner.appConfig")
  public static void injectAppConfig(FlinkJobRunner instance, AppConfig appConfig) {
    instance.appConfig = appConfig;
  }
}
