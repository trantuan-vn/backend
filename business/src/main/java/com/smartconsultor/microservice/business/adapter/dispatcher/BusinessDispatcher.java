package com.smartconsultor.microservice.business.adapter.dispatcher;


import java.util.Map;
import java.util.UUID;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.adapter.dto.gateway.business.common.EventType;
import com.smartconsultor.microservice.business.application.usecase.common.BusinessUseCase;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;

public class BusinessDispatcher {

    private final Map<EventType, BusinessUseCase> handlers;

    public BusinessDispatcher(Map<EventType, BusinessUseCase> handlers) {
        this.handlers = handlers;
    }

    public TransactionResult dispatch(GatewayMessage original) {
        TransactionResult result;
        EventType eventType = EventType.forNumber(original.getBusiness().getEventTypeValue());
        if (eventType == null) 
            return TransactionResult.newBuilder()
                .setTransactionId(UUID.randomUUID().toString())
                .setStatus("Failed")
                .setMessage("Deposit Failed")
                .build();

        BusinessUseCase useCase = handlers.get(eventType);
        if (useCase == null) 
            return TransactionResult.newBuilder()
                .setTransactionId(UUID.randomUUID().toString())
                .setStatus("Failed")
                .setMessage("Deposit Failed")
                .build();

        try {
            // Xử lý generics một cách an toàn
            result= ((BusinessUseCase) useCase).execute(original);
        } catch (Exception e) {
            // Logging và xử lý exception nếu cần
            System.err.println("Dispatch failed for eventType " + eventType + ": " + e.getMessage());
            result= TransactionResult.newBuilder()
                .setTransactionId(UUID.randomUUID().toString())
                .setStatus("Failed")
                .setMessage("Deposit Failed")
                .build();
        }

        return result;
    }
}
