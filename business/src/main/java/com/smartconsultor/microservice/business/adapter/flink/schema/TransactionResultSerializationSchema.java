package com.smartconsultor.microservice.business.adapter.flink.schema;

import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class TransactionResultSerializationSchema implements SerializationSchema<TransactionResult> {

    @Override
    public byte[] serialize(TransactionResult transactionResult) {
        return transactionResult.toByteArray(); // auto-generated Protobuf method
    }
}
