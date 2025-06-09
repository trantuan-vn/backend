package com.smartconsultor.microservice.business.application.usecase.common;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;

public interface BusinessUseCase {
    TransactionResult execute(GatewayMessage message);
}
