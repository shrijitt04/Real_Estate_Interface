package com.example.application.repository;

// import com.example.application.model.Transaction;
import com.example.application.model.Transactions;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
    boolean existsByBuyerIdAndPropertyId(String buyerId, Long propertyId); // Updated to use Long
    Optional<Transactions> findByBuyerIdAndPropertyId(String buyerId, Long propertyId); // Updated to use Long
}
