package com.example.application.service;

import com.example.application.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, Property property, LocalDateTime dateTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Confirmation");
        message.setText("Hello,\n\nYour appointment for the property titled \"" +
                property.getTitle() + "\" located at " + property.getLocation() +
                " has been confirmed for " + dateTime + ".\n\nThank you!");

        mailSender.send(message);
    }
}
