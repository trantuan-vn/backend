package com.smartconsultor.microservice.business.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class UserWallet {
    private String userId;
    private String currency;
    private BigDecimal balance;
    private Instant lastUpdated;

    public UserWallet(String userId, String currency, BigDecimal balance) {
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.lastUpdated = Instant.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
