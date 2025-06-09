package com.smartconsultor.microservice.business.domain.service;

import com.smartconsultor.microservice.business.domain.model.UserWallet;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;

import java.math.BigDecimal;

import javax.inject.Inject;

public class WalletService {

    @Inject
    WalletRepository walletRepository;

    public UserWallet getWallet(String userId, String currency) {
        return walletRepository.findByUserId(userId, currency);
    }

    public void credit(String userId, String currency, BigDecimal amount) {
        UserWallet wallet = walletRepository.findByUserId(userId, currency);
        if (wallet == null) {
            wallet = new UserWallet(userId, currency, BigDecimal.ZERO);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    public void debit(String userId, String currency, BigDecimal amount) {
        UserWallet wallet = walletRepository.findByUserId(userId, currency);
        if (wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance or wallet not found.");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
    }

    public void transfer(String fromUserId, String toUserId, String currency, BigDecimal amount) {
        debit(fromUserId, currency, amount);
        credit(toUserId, currency, amount);
    }
}
