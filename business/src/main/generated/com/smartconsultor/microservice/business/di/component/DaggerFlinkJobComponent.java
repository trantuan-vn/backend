package com.smartconsultor.microservice.business.di.component;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.smartconsultor.microservice.business.FlinkJobRunner;
import com.smartconsultor.microservice.business.FlinkJobRunner_MembersInjector;
import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.adapter.flink.process.TransactionProcessFunction;
import com.smartconsultor.microservice.business.application.config.AppConfig;
import com.smartconsultor.microservice.business.application.usecase.DepositUseCase;
import com.smartconsultor.microservice.business.di.module.ApplicationModule;
import com.smartconsultor.microservice.business.di.module.ApplicationModule_ProvideAppConfigFactory;
import com.smartconsultor.microservice.business.di.module.ApplicationModule_ProvideDepositUseCaseFactory;
import com.smartconsultor.microservice.business.di.module.ApplicationModule_ProvidePgPoolFactory;
import com.smartconsultor.microservice.business.di.module.ApplicationModule_ProvideVertxFactory;
import com.smartconsultor.microservice.business.di.module.ApplicationModule_ProvideWalletRepositoryFactory;
import com.smartconsultor.microservice.business.di.module.FlinkModule;
import com.smartconsultor.microservice.business.di.module.FlinkModule_ProvideDeserializationSchemaFactory;
import com.smartconsultor.microservice.business.di.module.FlinkModule_ProvideSerializationSchemaFactory;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import javax.annotation.processing.Generated;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;

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
public final class DaggerFlinkJobComponent {
  private DaggerFlinkJobComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static FlinkJobComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private ApplicationModule applicationModule;

    private FlinkModule flinkModule;

    private Builder() {
    }

    public Builder applicationModule(ApplicationModule applicationModule) {
      this.applicationModule = Preconditions.checkNotNull(applicationModule);
      return this;
    }

    public Builder flinkModule(FlinkModule flinkModule) {
      this.flinkModule = Preconditions.checkNotNull(flinkModule);
      return this;
    }

    public FlinkJobComponent build() {
      if (applicationModule == null) {
        this.applicationModule = new ApplicationModule();
      }
      if (flinkModule == null) {
        this.flinkModule = new FlinkModule();
      }
      return new FlinkJobComponentImpl(applicationModule, flinkModule);
    }
  }

  private static final class FlinkJobComponentImpl implements FlinkJobComponent {
    private final FlinkJobComponentImpl flinkJobComponentImpl = this;

    Provider<Vertx> provideVertxProvider;

    Provider<AppConfig> provideAppConfigProvider;

    Provider<PgPool> providePgPoolProvider;

    Provider<WalletRepository> provideWalletRepositoryProvider;

    Provider<DepositUseCase> provideDepositUseCaseProvider;

    Provider<DeserializationSchema<GatewayMessage>> provideDeserializationSchemaProvider;

    Provider<SerializationSchema<TransactionResult>> provideSerializationSchemaProvider;

    FlinkJobComponentImpl(ApplicationModule applicationModuleParam, FlinkModule flinkModuleParam) {

      initialize(applicationModuleParam, flinkModuleParam);

    }

    TransactionProcessFunction transactionProcessFunction() {
      return new TransactionProcessFunction(provideDepositUseCaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationModule applicationModuleParam,
        final FlinkModule flinkModuleParam) {
      this.provideVertxProvider = DoubleCheck.provider(ApplicationModule_ProvideVertxFactory.create(applicationModuleParam));
      this.provideAppConfigProvider = DoubleCheck.provider(ApplicationModule_ProvideAppConfigFactory.create(applicationModuleParam));
      this.providePgPoolProvider = DoubleCheck.provider(ApplicationModule_ProvidePgPoolFactory.create(applicationModuleParam, provideVertxProvider, provideAppConfigProvider));
      this.provideWalletRepositoryProvider = DoubleCheck.provider(ApplicationModule_ProvideWalletRepositoryFactory.create(applicationModuleParam, providePgPoolProvider));
      this.provideDepositUseCaseProvider = DoubleCheck.provider(ApplicationModule_ProvideDepositUseCaseFactory.create(applicationModuleParam, provideWalletRepositoryProvider));
      this.provideDeserializationSchemaProvider = DoubleCheck.provider(FlinkModule_ProvideDeserializationSchemaFactory.create(flinkModuleParam));
      this.provideSerializationSchemaProvider = DoubleCheck.provider(FlinkModule_ProvideSerializationSchemaFactory.create(flinkModuleParam));
    }

    @Override
    public void inject(TransactionProcessFunction processFunction) {
    }

    @Override
    public void inject(FlinkJobRunner jobRunner) {
      injectFlinkJobRunner(jobRunner);
    }

    @Override
    public DepositUseCase getDepositUseCase() {
      return provideDepositUseCaseProvider.get();
    }

    @CanIgnoreReturnValue
    private FlinkJobRunner injectFlinkJobRunner(FlinkJobRunner instance) {
      FlinkJobRunner_MembersInjector.injectTransactionProcessFunction(instance, transactionProcessFunction());
      FlinkJobRunner_MembersInjector.injectGatewayMessageDeserialization(instance, provideDeserializationSchemaProvider.get());
      FlinkJobRunner_MembersInjector.injectResultMessageSerialization(instance, provideSerializationSchemaProvider.get());
      FlinkJobRunner_MembersInjector.injectAppConfig(instance, provideAppConfigProvider.get());
      return instance;
    }
  }
}
