package com.smartconsultor.microservice.business.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import javax.inject.Inject;

import com.smartconsultor.microservice.business.adapter.annotation.HandlesEvent;
import com.smartconsultor.microservice.business.adapter.dto.gateway.business.common.EventType;
import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.application.usecase.common.BusinessUseCase;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import com.smartconsultor.microservice.business.domain.service.WalletService;

@HandlesEvent(EventType.DEPOSIT_REQUESTED)
public class DepositUseCase implements BusinessUseCase {

    @Inject
    WalletService walletService;

    /**
     * Xử lý nạp tiền vào ví người dùng.
     */
    @Override
    public TransactionResult execute(GatewayMessage message) {
        String userId = message.getUserId();

        walletService.credit(userId, "BTC", 
                                BigDecimal.valueOf(message.getBusiness().getDepositRequested().getAmount()));

        return TransactionResult.newBuilder()
                .setTransactionId(UUID.randomUUID().toString())
                .setStatus("SUCCESS")
                .setMessage("Deposit completed")
                .build();
    }
}
