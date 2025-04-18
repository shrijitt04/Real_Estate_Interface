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

    public void sendTransactionEmail(String To, Property property, String token){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(To);
        message.setSubject("Registration Confirmation");
        message.setText("Hello, \n\n Congratulations!!\n\nYou have successfully registered for the land titled\" "+property.getTitle()+ "\n\nYour token number is \" "+token+"\n\nThis token number will be used for further land process.\n\n Thank you!");

        mailSender.send(message);
    }

    public void sendIntialEmail(String To){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(To);
        message.setSubject("Appointment Initiated");
        message.setText("Hello! \n\nYour appointment request has been received and the process has been initiated.\n\n An email confirmation will be sent to you regarding the same. \n\n Thank You! ");
        mailSender.send((message));
    }

    public void sendRejectionEmail(String To, Property property){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(To);
        message.setSubject("Appointment Confirmation");
        message.setText("Hello,\n\nYour appointment for the property titled \"" +
                property.getTitle() + "\" located at " + property.getLocation() +
                " has been rejected due to unavailable slots. \n\n Please try again after a few days. " + ".\n\nThank you!");
        mailSender.send(message);

    }
}
