package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.apache.flink.api.common.serialization.DeserializationSchema;

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
public final class FlinkModule_ProvideDeserializationSchemaFactory implements Factory<DeserializationSchema<GatewayMessage>> {
  private final FlinkModule module;

  public FlinkModule_ProvideDeserializationSchemaFactory(FlinkModule module) {
    this.module = module;
  }

  @Override
  public DeserializationSchema<GatewayMessage> get() {
    return provideDeserializationSchema(module);
  }

  public static FlinkModule_ProvideDeserializationSchemaFactory create(FlinkModule module) {
    return new FlinkModule_ProvideDeserializationSchemaFactory(module);
  }

  public static DeserializationSchema<GatewayMessage> provideDeserializationSchema(
      FlinkModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideDeserializationSchema());
  }
}
