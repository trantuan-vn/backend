package com.smartconsultor.microservice.business.adapter.flink.process;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.adapter.dto.gateway.business.common.EventType;
import com.smartconsultor.microservice.business.application.usecase.*;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;

import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;


import javax.inject.Inject;

public class TransactionProcessFunction extends KeyedProcessFunction<String, GatewayMessage, TransactionResult> {

    private final DepositUseCase depositUseCase;

    @Inject
    public TransactionProcessFunction(DepositUseCase depositUseCase) {
        this.depositUseCase = depositUseCase;
    }

    @Override
    public void processElement(GatewayMessage message, Context ctx, Collector<TransactionResult> out) throws Exception {
        TransactionResult result;

        switch (message.getBusiness().getItems(0).getEventTypeValue()) {
            case EventType.DEPOSIT_REQUESTED_VALUE:
                result = depositUseCase.execute(message);
                break;
            // Các event khác như WITHDRAWAL, SWAP, BRIDGE, PLACEORDER ...
            default:
                // Xử lý mặc định hoặc log lỗi
                result = TransactionResult.newBuilder()
                    .setTransactionId("1")
                    .build();
        }

        out.collect(result);
    }
}

