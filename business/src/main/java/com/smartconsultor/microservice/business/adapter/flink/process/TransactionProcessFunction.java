package com.smartconsultor.microservice.business.adapter.flink.process;

import com.smartconsultor.microservice.business.adapter.dispatcher.BusinessDispatcher;
import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import javax.inject.Inject;

public class TransactionProcessFunction extends KeyedProcessFunction<String, GatewayMessage, TransactionResult> {

    private final BusinessDispatcher dispatcher;

    @Inject
    public TransactionProcessFunction(BusinessDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void processElement(GatewayMessage message, Context ctx, Collector<TransactionResult> out) throws Exception {
        try {
            TransactionResult result = dispatcher.dispatch(message);
            if (result != null) {
                out.collect(result);
            }
        } catch (Exception e) {
            // Log lỗi hoặc tạo TransactionResult lỗi
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }
}
