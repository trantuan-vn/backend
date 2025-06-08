package com.smartconsultor.microservice.business.adapter.flink.schema;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.io.IOException;

public class GatewayMessageDeserializationSchema implements DeserializationSchema<GatewayMessage> {

    @Override
    public GatewayMessage deserialize(byte[] message) throws IOException {
        return GatewayMessage.parseFrom(message); // auto-generated Protobuf method
    }

    @Override
    public boolean isEndOfStream(GatewayMessage nextElement) {
        return false;
    }

    @Override
    public TypeInformation<GatewayMessage> getProducedType() {
        return Types.POJO(GatewayMessage.class);
    }
}
