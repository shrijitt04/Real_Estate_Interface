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
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.application.service.AppointmentService;
import com.example.application.service.EmailService;
import com.example.application.service.PropertyService;
import com.example.application.model.Appointment;
import com.example.application.model.Property;
import com.example.application.model.Transactions;
import com.example.application.service.TransactionService;
import com.example.application.views.PropertyCardFactory;
import com.example.application.model.PropertyBuilder;

@PageTitle("Industrial Properties")
@Route("industrial")
@CssImport("./styles/industrial-view.css")
public class IndustrialView extends VerticalLayout {

    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    // Iterator for Property
    class PropertyIterator implements Iterator<Property> {
        private final List<Property> properties;
        private int position = 0;

        public PropertyIterator(List<Property> properties) {
            this.properties = properties;
        }

        @Override
        public boolean hasNext() {
            return position < properties.size();
        }

        @Override
        public Property next() {
            return properties.get(position++);
        }
    }

    // Collection for Property
    class PropertyCollection {
        private final List<Property> properties = new ArrayList<>();

        public void addProperty(Property property) {
            properties.add(property);
        }

        public PropertyIterator createIterator() {
            return new PropertyIterator(properties);
        }
    }

    @Autowired
    public IndustrialView(PropertyService propertyService, AppointmentService appointmentService, JavaMailSender mailSender, TransactionService transactionService) {
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        this.emailService = EmailService.getInstance(mailSender);
        this.transactionService = transactionService;

        List<Property> properties = propertyService.getPropertiesByType("INDUSTRIAL");
        PropertyCollection propertyCollection = new PropertyCollection();
        properties.forEach(propertyCollection::addProperty);

        addClassName("industrial-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 header = new H2("Industrial Properties");
        header.addClassName("view-header");

        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.addClassName("property-card-container");

        PropertyIterator iterator = propertyCollection.createIterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            Property builtProperty = new PropertyBuilder()
                .setTitle(property.getTitle())
                .setDescription(property.getDescription())
                .setLocation(property.getLocation())
                .setPrice(property.getPrice())
                .setSize(property.getSize())
                .setStatus(property.getStatus())
                .setType(property.getType())
                .build();

            Div card = PropertyCardFactory.createPropertyCard(
                builtProperty,
                () -> openAppointmentDialog(builtProperty),
                () -> openRegisterDialog(builtProperty)
            );
            cardsLayout.add(card);
        }

        add(header, cardsLayout);
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
        transaction.setPropertyId(property.getPropertyId()); // Ensure type consistency with Long
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
}