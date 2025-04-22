package com.example.application.model;

public class TransactionBuilder {
    private double amount;
    private String status;
    private String buyerId;
    private Long propertyId;

    public TransactionBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public TransactionBuilder setBuyerId(String buyerId) {
        this.buyerId = buyerId;
        return this;
    }

    public TransactionBuilder setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
        return this;
    }

    public Transactions build() {
        Transactions transaction = new Transactions();
        transaction.setAmount(amount);
        transaction.setStatus(status);
        transaction.setBuyerId(buyerId);
        transaction.setPropertyId(propertyId);
        return transaction;
    }
}