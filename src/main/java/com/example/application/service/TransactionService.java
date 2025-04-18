package com.example.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.repository.TransactionRepository;
import com.example.application.model.Transactions;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void saveTransaction(Transactions transaction) {
        transactionRepository.save(transaction);
    }

    public void updateTransaction(Transactions transaction) {
        transactionRepository.save(transaction);
    }

    public boolean hasUserRegisteredForProperty(String buyerId, long propertyId) {
        return transactionRepository.existsByBuyerIdAndPropertyId(buyerId, propertyId);
    }
    
}
