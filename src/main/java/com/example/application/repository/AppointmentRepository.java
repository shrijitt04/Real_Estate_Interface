package com.example.application.repository;

import com.example.application.model.Appointment;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, BigInteger> {
}
