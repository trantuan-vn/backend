package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.apache.flink.api.common.serialization.SerializationSchema;

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
public final class FlinkModule_ProvideSerializationSchemaFactory implements Factory<SerializationSchema<TransactionResult>> {
  private final FlinkModule module;

  public FlinkModule_ProvideSerializationSchemaFactory(FlinkModule module) {
    this.module = module;
  }

  @Override
  public SerializationSchema<TransactionResult> get() {
    return provideSerializationSchema(module);
  }

  public static FlinkModule_ProvideSerializationSchemaFactory create(FlinkModule module) {
    return new FlinkModule_ProvideSerializationSchemaFactory(module);
  }

  public static SerializationSchema<TransactionResult> provideSerializationSchema(
      FlinkModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideSerializationSchema());
  }
}
