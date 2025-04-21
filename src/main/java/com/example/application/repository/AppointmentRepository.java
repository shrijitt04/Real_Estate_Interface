package com.example.application.repository;

import com.example.application.model.Appointment;
// import com.example.application.model.Transactions;

import java.math.BigInteger;
// import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAll();

}
