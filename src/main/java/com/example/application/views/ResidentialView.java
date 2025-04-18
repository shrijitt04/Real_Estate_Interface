package com.example.application.views;

import com.vaadin.flow.component.button.Button;
// import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import com.example.application.service.AppointmentService;
import com.example.application.service.EmailService;
import com.example.application.service.PropertyService;
import com.example.application.model.Appointment;
import com.example.application.model.Property;
import com.example.application.model.Transactions;
import com.example.application.service.TransactionService;



@PageTitle("Residential Properties")
@Route("residential")
@CssImport("./styles/residential-view.css")
public class ResidentialView extends VerticalLayout {

    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    @Autowired
    public ResidentialView(PropertyService propertyService, AppointmentService appointmentService, EmailService emailService, TransactionService transactionService) {
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        this.emailService = emailService;
        this.transactionService = transactionService;

        List<Property> properties = propertyService.getPropertiesByType("RESIDENTIAL");

        addClassName("residential-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 header = new H2("Residential Properties");
        header.addClassName("view-header");

        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.addClassName("property-card-container");

        properties.forEach(property -> {
            Div card = createPropertyCard(property);
            cardsLayout.add(card);
        });

        add(header, cardsLayout);
    }

    private Div createPropertyCard(Property property) {
        Div card = new Div();
        card.addClassName("property-card");

        H2 title = new H2(property.getTitle());
        title.addClassName("property-title");

        Div infoDiv = new Div();
        infoDiv.addClassName("property-info");
        Span location = new Span("Location: " + property.getLocation());
        Span size = new Span("Size: " + property.getSize() + " sq.ft");
        infoDiv.add(location, size);

        Paragraph description = new Paragraph(property.getDescription());
        description.addClassName("property-description");

        H4 price = new H4("$" + property.getPrice());
        price.addClassName("property-price");

        Span status = new Span(property.getStatus());
        status.addClassName("property-status");

        if ("AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-available");
        } else if ("SOLD".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-sold");
        } else if ("BOOKED".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-booked");
        }

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-container");

        Button bookButton = new Button("Book Appointment");
        bookButton.addClassName("book-button");

        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            bookButton.setEnabled(false);
            bookButton.addClassName("disabled-button");
        } else {
            bookButton.addClickListener(e -> openAppointmentDialog(property));
        }

        Button registerButton = new Button("Register Land");
        registerButton.addClassName("register-button");
        registerButton.getElement().getStyle().set("background-color", "#FF9800");  // Custom color for "Register Land"

        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            registerButton.setEnabled(false);
            registerButton.addClassName("disabled-button");
        } else {
            long propertyID = property.getPropertyId();
            if(transactionService.hasUserRegisteredForProperty(getLoggedInUserEmail(), propertyID)){
                registerButton.setEnabled(false);
                registerButton.addClassName("disabled-button");
                Notification.show("You have already registerd for this land",5000,Notification.Position.MIDDLE);            }
            else{
                registerButton.addClickListener(e -> openRegisterDialog(property));
            }
        }

        buttonLayout.add(bookButton, registerButton);

        card.add(title, infoDiv, description, price, status, buttonLayout);

        return card;
    }

    private void openAppointmentDialog(Property property) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select Appointment Date & Time");

        VerticalLayout dialogLayout = new VerticalLayout();
        DateTimePicker dateTimePicker = new DateTimePicker("Appointment Date & Time");
        dateTimePicker.setMin(LocalDateTime.now());
        dateTimePicker.setWidthFull();

        Button confirmButton = new Button("Confirm", event -> {
            LocalDateTime selectedDateTime = dateTimePicker.getValue();
            if (selectedDateTime != null) {
                sendIntialEmail(selectedDateTime, property);

                // saveAppointmentAndSendEmail(property, selectedDateTime);
                dialog.close();
                Notification.show("Appointment booked!", 3000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Please select a date and time.");
            }
        });

        dialogLayout.add(dateTimePicker, confirmButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openRegisterDialog(Property property) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Registration");

        VerticalLayout dialogLayout = new VerticalLayout();
        Paragraph confirmationText = new Paragraph("Are you sure you want to register this land?");
        Button confirmButton = new Button("Yes, Register", event -> {
            processRegistration(property);
            dialog.close();

            Notification.show("Land registered successfully!", 3000, Notification.Position.TOP_CENTER);
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        dialogLayout.add(confirmationText, confirmButton, cancelButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void sendIntialEmail(LocalDateTime dateTime, Property property){
        String loggedInUserEmail = getLoggedInUserEmail();
        if (loggedInUserEmail == null || loggedInUserEmail.isEmpty()) {
            Notification.show("Error: Could not determine user email.", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        Appointment appointment = new Appointment();
        appointment.setDateTime(dateTime);
        appointment.setNotes("Commercial appointment");
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setProperty(property);
        appointment.setUserId(loggedInUserEmail);
        appointmentService.saveAppointment(appointment);

        emailService.sendIntialEmail(loggedInUserEmail);

    }

    private void processRegistration(Property property) {
            try {
            Thread.sleep(3000);  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String loggedInUserEmail = getLoggedInUserEmail();
        if (loggedInUserEmail == null || loggedInUserEmail.isEmpty()) {
            Notification.show("Error: Could not determine user email.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        Transactions transaction = new Transactions();
        transaction.setAmount(50.00); 
        transaction.setBuyerId(loggedInUserEmail);
        transaction.setPropertyId((long)property.getPropertyId());
        transaction.setStatus("PENDING"); 

        transactionService.saveTransaction(transaction);

        // Simulate completing the transaction
        transaction.setStatus("COMPLETED");
        transactionService.updateTransaction(transaction);
        String token = transaction.getToken();
        emailService.sendTransactionEmail(loggedInUserEmail, property, token);

        System.out.println("Transaction completed for user: " + loggedInUserEmail);
    }

    private String getLoggedInUserEmail() {
        String loggedInUserEmail = null;
        String filePath = "email.txt";
        try {
            loggedInUserEmail = Files.readString(Paths.get(filePath)).trim();
            System.out.println("Read email: " + loggedInUserEmail);
        } catch (NoSuchFileException e) {
            System.err.println("Email file not found: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading email from file: " + e.getMessage());
        }
        return loggedInUserEmail;
    }

}