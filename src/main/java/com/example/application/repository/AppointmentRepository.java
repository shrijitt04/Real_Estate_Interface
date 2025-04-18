package com.example.application.repository;

import com.example.application.model.Appointment;
import com.example.application.model.Transactions;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, BigInteger> {
    

}
