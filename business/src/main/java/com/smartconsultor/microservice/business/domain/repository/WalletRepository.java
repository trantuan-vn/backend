package com.smartconsultor.microservice.business.domain.repository;

import com.smartconsultor.microservice.business.domain.model.UserWallet;

public interface WalletRepository {
    UserWallet findByUserId(String userId, String currency);
    void save(UserWallet wallet);
}