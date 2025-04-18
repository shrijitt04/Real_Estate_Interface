package com.example.application.service;

import com.example.application.model.Appointment;
import com.example.application.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public List<Appointment> displayAllAppointments(){
        return appointmentRepository.findAll();
    }

    public void deleteAppointment(BigInteger id){
        appointmentRepository.deleteById(id);
    }
}
