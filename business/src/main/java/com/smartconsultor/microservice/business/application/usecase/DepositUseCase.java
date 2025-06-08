package com.smartconsultor.microservice.business.application.usecase;


import java.math.BigDecimal;
import java.time.Instant;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import com.smartconsultor.microservice.business.domain.model.UserWallet;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;

public class DepositUseCase {

    private final WalletRepository walletRepository;

    public DepositUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Xử lý nạp tiền vào ví người dùng.
     */
    public TransactionResult execute(GatewayMessage message) {
        String userId = message.getUserId();

        // Lấy ví hiện tại
        UserWallet wallet = walletRepository.findByUserId(userId, "BTC");

        if (wallet == null) {
            // Nếu ví chưa tồn tại, tạo mới
            wallet = new UserWallet(userId, "BTC", BigDecimal.ZERO);
        }

        // Cộng số dư
        BigDecimal newBalance = wallet.getBalance().add(BigDecimal.valueOf(0));
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(Instant.now());

        // Lưu lại ví
        walletRepository.save(wallet);

        // Trả về kết quả
        return TransactionResult.newBuilder()
                    .setTransactionId("1")
                    .build();
    }
}
