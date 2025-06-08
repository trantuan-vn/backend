package com.smartconsultor.microservice.business.di.module;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import com.smartconsultor.microservice.business.adapter.flink.schema.GatewayMessageDeserializationSchema;
import com.smartconsultor.microservice.business.adapter.flink.schema.TransactionResultSerializationSchema;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class FlinkModule {

    @Singleton
    @Provides
    public DeserializationSchema<GatewayMessage> provideDeserializationSchema() {
        return new GatewayMessageDeserializationSchema(); // bạn cần đảm bảo class này tồn tại
    }

    @Singleton
    @Provides
    public SerializationSchema<TransactionResult> provideSerializationSchema() {
        return new TransactionResultSerializationSchema(); // bạn cần đảm bảo class này tồn tại
    }
}
