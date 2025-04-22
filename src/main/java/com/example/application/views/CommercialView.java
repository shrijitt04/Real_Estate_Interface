package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.NoSuchElementException;

import com.example.application.service.AppointmentService;
import com.example.application.service.EmailService;
import com.example.application.service.PropertyService;
import com.example.application.service.TransactionService;
import com.example.application.model.Appointment;
import com.example.application.model.Property;
import com.example.application.model.Transactions;
import com.example.application.model.TransactionBuilder;
import com.example.application.views.PropertyCardFactory;

import com.example.application.views.NotificationUtils;

class CommercialPropertyIterator implements Iterator<Property> {
    private final List<Property> properties;
    private int position = 0;

    public CommercialPropertyIterator(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public boolean hasNext() {
        return position < properties.size();
    }

    @Override
    public Property next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more commercial properties in iterator");
        }
        return properties.get(position++);
    }
}

class CommercialPropertyCollection {
    private final List<Property> properties = new ArrayList<>();

    public void addProperty(Property property) {
        if (property != null) {
            properties.add(property);
        }
    }

    public CommercialPropertyIterator createIterator() {
        return new CommercialPropertyIterator(new ArrayList<>(properties));
    }

    public int size() {
        return properties.size();
    }
}

@PageTitle("Commercial Properties")
@Route("commercial")
@CssImport("./styles/commercial-view.css")
public class CommercialView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(CommercialView.class);

    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    @Autowired
    public CommercialView(PropertyService propertyService, AppointmentService appointmentService, JavaMailSender mailSender, TransactionService transactionService) {
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        this.emailService = EmailService.getInstance(mailSender);
        this.transactionService = transactionService;

        log.info("Initializing CommercialView...");

        List<Property> fetchedProperties;
        try {
            fetchedProperties = propertyService.getPropertiesByType("COMMERCIAL");
            log.debug("Fetched {} commercial properties from service.", fetchedProperties.size());
        } catch (Exception e) {
            log.error("Failed to fetch commercial properties", e);
            fetchedProperties = new ArrayList<>();
            Notification.show("Error loading properties. Please try refreshing.", 5000, Notification.Position.MIDDLE);
        }

        CommercialPropertyCollection propertyCollection = new CommercialPropertyCollection();
        fetchedProperties.forEach(propertyCollection::addProperty);

        addClassName("commercial-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 header = new H2("Commercial Properties");
        header.addClassName("view-header");

        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        cardsLayout.addClassName("property-card-container");

        CommercialPropertyIterator iterator = propertyCollection.createIterator();
        if (!iterator.hasNext()) {
            add(header, new Paragraph("No commercial properties available at the moment."));
        } else {
            while (iterator.hasNext()) {
                Property originalProperty = iterator.next();

                if (originalProperty == null || originalProperty.getPropertyId() == null) {
                    log.warn("Skipping card creation for null property or property with null ID.");
                    continue;
                }

                try {
                    Div card = PropertyCardFactory.createPropertyCard(
                        originalProperty,
                        () -> openAppointmentDialog(originalProperty),
                        () -> openRegisterDialog(originalProperty)
                    );
                    cardsLayout.add(card);
                } catch (Exception e) {
                    log.error("Error creating property card for property ID: {}", originalProperty.getPropertyId(), e);
                }
            }
            add(header, cardsLayout);
        }

        log.info("CommercialView initialized successfully with {} cards.", cardsLayout.getComponentCount());
    }

    private void openAppointmentDialog(Property property) {
        if (property == null || property.getPropertyId() == null) {
            log.error("Attempted to open appointment dialog for invalid property: {}", property);
            NotificationUtils.showStyledNotification("Cannot book appointment for this property (invalid data).", 3000);
            return;
        }
        log.debug("Opening appointment dialog for property ID: {}", property.getPropertyId());

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select Appointment Date & Time for '" + property.getTitle() + "'");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        DateTimePicker dateTimePicker = new DateTimePicker("Appointment Date & Time");
        dateTimePicker.setMin(LocalDateTime.now().plusMinutes(5));
        dateTimePicker.setStep(java.time.Duration.ofMinutes(30));
        dateTimePicker.setWidthFull();

        Button confirmButton = new Button("Confirm Appointment", event -> {
            LocalDateTime selectedDateTime = dateTimePicker.getValue();
            if (selectedDateTime != null) {
                sendIntialEmail(selectedDateTime, property);
                dialog.close();
            } else {
                NotificationUtils.showStyledNotification("Please select a valid date and time.", 3000);
            }
        });
        confirmButton.getElement().getThemeList().add("primary");

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();

        dialogLayout.add(dateTimePicker, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openRegisterDialog(Property property) {
        if (property == null || property.getPropertyId() == null) {
            log.error("Attempted to open registration dialog for invalid property: {}", property);
            NotificationUtils.showStyledNotification("Cannot register for this property (invalid data).", 3000);
            return;
        }
        log.debug("Opening registration dialog for property ID: {}", property.getPropertyId());

        String loggedInUserEmail = getLoggedInUserEmail();
        if (loggedInUserEmail == null) {
            NotificationUtils.showStyledNotification("Could not identify user. Please log in.", 3000);
            return;
        }

        try {
            if (transactionService.hasUserRegisteredForProperty(loggedInUserEmail, property.getPropertyId())) {
                NotificationUtils.showStyledNotification("You have already registered for this property.", 3000);
                return;
            }
        } catch (Exception e) {
            log.error("Error checking existing registration for user {} and property {}", loggedInUserEmail, property.getPropertyId(), e);
            NotificationUtils.showStyledNotification("Could not verify registration status. Please try again.", 3000);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Registration");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        Paragraph confirmationText = new Paragraph("Register your interest for commercial property: '" + property.getTitle() + "'?");
        confirmationText.getStyle().set("text-align", "center");

        Button confirmButton = new Button("Yes, Register", event -> {
            processRegistration(property, loggedInUserEmail);
            dialog.close();
        });
        confirmButton.getElement().getThemeList().add("primary");

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);

        dialogLayout.add(confirmationText, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void sendIntialEmail(LocalDateTime dateTime, Property property) {
        String loggedInUserEmail = getLoggedInUserEmail();
        if (loggedInUserEmail == null) {
            Notification.show("Error: Could not determine user email. Please log in.", 3000, Notification.Position.TOP_CENTER);
            log.error("Failed to send initial email: User email not found.");
            return;
        }

        if (property == null || property.getPropertyId() == null) {
            Notification.show("Error: Cannot book appointment due to invalid property data.", 3000, Notification.Position.TOP_CENTER);
            log.error("CRITICAL: sendIntialEmail called with invalid property: {}", property);
            return;
        }

        log.info("Creating appointment for user '{}' for COMMERCIAL property ID '{}' at {}", loggedInUserEmail, property.getPropertyId(), dateTime);

        Appointment appointment = new Appointment();
        appointment.setDateTime(dateTime);
        appointment.setNotes("Commercial property appointment: " + property.getTitle());
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setProperty(property);
        appointment.setUserId(loggedInUserEmail);

        try {
            appointmentService.saveAppointment(appointment);
            log.info("Appointment saved successfully with ID: {}", appointment.getAppointmentId());
            emailService.sendIntialEmail(loggedInUserEmail);
            log.info("Initial confirmation email sent to {}", loggedInUserEmail);
            NotificationUtils.showStyledNotification("Appointment requested. Check your email for confirmation.", 3000);
        } catch (Exception e) {
            log.error("Error saving appointment or sending initial email for user {}", loggedInUserEmail, e);
            Notification.show("Error booking appointment. Please try again later.", 5000, Notification.Position.MIDDLE);
        }
    }

    private void processRegistration(Property property, String loggedInUserEmail) {
        if (property == null || property.getPropertyId() == null || loggedInUserEmail == null) {
            log.error("CRITICAL: processRegistration called with invalid arguments. Property: {}, Email: {}", property, loggedInUserEmail);
            Notification.show("Internal Error: Cannot process registration.", 3000, Notification.Position.MIDDLE);
            return;
        }

        log.info("Processing registration for user '{}' and COMMERCIAL property ID '{}'", loggedInUserEmail, property.getPropertyId());
        NotificationUtils.showStyledNotification("Registration in progress...", 2000);

        if ("SOLD".equalsIgnoreCase(property.getStatus()) || "PENDING".equalsIgnoreCase(property.getStatus())) {
            log.warn("Attempt to register for property ID {} which is already {}", property.getPropertyId(), property.getStatus());
            NotificationUtils.showStyledNotification("This property is no longer available for registration.", 3000);
            return;
        }

        Transactions transaction = new TransactionBuilder()
            .setAmount(100.00)
            .setBuyerId(loggedInUserEmail)
            .setPropertyId(property.getPropertyId())
            .setStatus("PENDING")
            .build();

        try {
            transactionService.saveTransaction(transaction);
            log.info("Initial transaction record saved with ID: {} (Status: PENDING)", transaction.getTransactionId());

            Thread.sleep(500);

            transaction.setStatus("COMPLETED");
            transactionService.updateTransaction(transaction);
            log.info("Transaction ID {} status updated to COMPLETED.", transaction.getTransactionId());

            property.setStatus("SOLD");
            propertyService.saveProperty(property);
            log.info("Property ID {} status updated to {}.", property.getPropertyId(), property.getStatus());

            String token = transaction.getToken();
            if (token == null) {
                log.warn("Transaction token was null after saving/updating transaction ID: {}", transaction.getTransactionId());
            }

            emailService.sendTransactionEmail(loggedInUserEmail, property, token);
            log.info("Transaction completion email sent to {} for property '{}'", loggedInUserEmail, property.getTitle());

            NotificationUtils.showStyledNotification("Registration successful! Check your email.", 3000);

        } catch (InterruptedException e) {
            log.warn("Thread sleep interrupted during registration simulation", e);
            Thread.currentThread().interrupt();
            Notification.show("Registration process was interrupted.", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            log.error("Error processing registration transaction, updating property status, or sending email for user {}", loggedInUserEmail, e);
            Notification.show("Registration failed. Please try again later or contact support.", 5000, Notification.Position.MIDDLE);
        }
    }

    private String getLoggedInUserEmail() {
        String loggedInUserEmail = null;
        String filePath = "email.txt";
        try {
            loggedInUserEmail = Files.readString(Paths.get(filePath)).trim();
            log.debug("Read email from file {}: '{}'", filePath, loggedInUserEmail);
            if (loggedInUserEmail.isEmpty()) {
                log.warn("Email read from file {} is empty.", filePath);
                return null;
            }
            if (!loggedInUserEmail.contains("@")) {
                log.warn("Invalid email format read from file {}: {}", filePath, loggedInUserEmail);
                return null;
            }
        } catch (NoSuchFileException e) {
            log.error("Email file not found: {}", filePath);
        } catch (IOException e) {
            log.error("IOException reading email from file: {}", filePath, e);
        } catch (Exception e) {
            log.error("Unexpected error reading email from file: {}", filePath, e);
        }
        return loggedInUserEmail;
    }
}