package com.example.application.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    private double amount;
    private String status; // 'CANCELLED', 'COMPLETED', or 'PENDING'
    private String buyerId;
    private long propertyId;

    @Column(unique = true, nullable = false)
    private String token;

    // Constructor to generate token
    public Transactions() {
        this.token = generateToken();
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(); // 10-character token
    }

    // Getters and setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", propertyId=" + propertyId +
                ", token='" + token + '\'' +
                '}';
    }
}
