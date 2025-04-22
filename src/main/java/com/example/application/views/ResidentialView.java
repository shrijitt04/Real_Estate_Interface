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

import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ArrayList;

import com.example.application.service.AppointmentService;
import com.example.application.service.EmailService;
import com.example.application.service.PropertyService;
import com.example.application.model.Appointment;
import com.example.application.model.Property;
import com.example.application.model.Transactions;
import com.example.application.service.TransactionService;
import com.example.application.model.TransactionBuilder; // Import the new TransactionBuilder class
// Make sure these imports point to your actual classes
import com.example.application.views.PropertyCardFactory;
import com.example.application.views.NotificationUtils;

@PageTitle("Residential Properties")
@Route("residential")
@CssImport("./styles/residential-view.css") 
public class ResidentialView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(ResidentialView.class);

    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    // --- Iterator Pattern Implementation ---
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
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return properties.get(position++);
        }
    }

    // Collection for Property
    class PropertyCollection {
        private final List<Property> properties = new ArrayList<>();

        public void addProperty(Property property) {
            if (property != null) {
                properties.add(property);
            }
        }

        public PropertyIterator createIterator() {
            return new PropertyIterator(new ArrayList<>(properties)); // Return iterator over a copy
        }

        public int size() {
            return properties.size();
        }
    }
    // --- End Iterator Pattern Implementation ---


    @Autowired
    public ResidentialView(PropertyService propertyService, AppointmentService appointmentService, JavaMailSender mailSender, TransactionService transactionService) {
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        // Consider making EmailService a proper Spring bean (@Service) and injecting it
        this.emailService = EmailService.getInstance(mailSender);
        this.transactionService = transactionService;

        log.info("Initializing ResidentialView...");

        // --- Fetch and prepare properties ---
        List<Property> fetchedProperties;
        try {
            fetchedProperties = propertyService.getPropertiesByType("RESIDENTIAL");
            log.debug("Fetched {} residential properties from service.", fetchedProperties.size());
        } catch (Exception e) {
            log.error("Failed to fetch residential properties", e);
            fetchedProperties = new ArrayList<>(); // Avoid NullPointerException later
            Notification.show("Error loading properties. Please try refreshing.", 5000, Notification.Position.MIDDLE);
        }

        PropertyCollection propertyCollection = new PropertyCollection();
        fetchedProperties.forEach(propertyCollection::addProperty);
        // --- End Fetch ---

        // --- View Setup ---
        addClassName("residential-view");
        setSizeFull(); // Use sparingly if content might exceed viewport height
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER); // Center content horizontally

        H2 header = new H2("Residential Properties");
        header.addClassName("view-header"); // Style in CSS

        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP); // Allow cards to wrap
        cardsLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center cards
        cardsLayout.addClassName("property-card-container"); // Style in CSS

        // --- Create Property Cards ---
        PropertyIterator iterator = propertyCollection.createIterator();
        if (!iterator.hasNext()) {
            add(header, new Paragraph("No residential properties available at the moment."));
        } else {
            while (iterator.hasNext()) {
                // Get the ORIGINAL property object fetched from the DB (includes the ID)
                Property originalProperty = iterator.next();

                // Check if property or essential data is null before creating card
                if (originalProperty == null || originalProperty.getPropertyId() == null) {
                    log.warn("Skipping card creation for null property or property with null ID.");
                    continue;
                }

                try {
                    // Pass the ORIGINAL property (with ID) to the factory and the button actions
                    Div card = PropertyCardFactory.createPropertyCard(
                        originalProperty, // Use the original property for the card display
                        () -> openAppointmentDialog(originalProperty), // Pass the ORIGINAL property
                        () -> openRegisterDialog(originalProperty)    // Pass the ORIGINAL property
                    );
                    cardsLayout.add(card);
                } catch (Exception e) {
                    log.error("Error creating property card for property ID: {}", originalProperty.getPropertyId(), e);
                    // Optionally add a placeholder or error message card
                }
            }
             add(header, cardsLayout);
        }
        // --- End Create Property Cards ---

        log.info("ResidentialView initialized successfully with {} cards.", cardsLayout.getComponentCount());
    }

    private void openAppointmentDialog(Property property) {
        // Defensive check
        if (property == null || property.getPropertyId() == null) {
            log.error("Attempted to open appointment dialog for invalid property: {}", property);
            NotificationUtils.showStyledNotification("Cannot book appointment for this property (invalid data).", 3000);
            return;
        }
        log.debug("Opening appointment dialog for property ID: {}", property.getPropertyId());

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select Appointment Date & Time for '" + property.getTitle() + "'"); // More specific title

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        DateTimePicker dateTimePicker = new DateTimePicker("Appointment Date & Time");
        dateTimePicker.setMin(LocalDateTime.now().plusMinutes(5)); // Add a small buffer
        dateTimePicker.setStep(java.time.Duration.ofMinutes(30)); // Set time steps
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
        confirmButton.getElement().getThemeList().add("primary"); // Style button

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
        // Defensive check
        if (property == null || property.getPropertyId() == null) {
            log.error("Attempted to open registration dialog for invalid property: {}", property);
            NotificationUtils.showStyledNotification("Cannot register for this property (invalid data).", 3000);
            return;
        }
        log.debug("Opening registration dialog for property ID: {}", property.getPropertyId());

        String loggedInUserEmail = getLoggedInUserEmail(); // Fetch user email
        if (loggedInUserEmail == null) {
             NotificationUtils.showStyledNotification("Could not identify user. Please log in.", 3000);
             return;
        }

        // Check if already registered before showing dialog
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
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER); // Center content

        Paragraph confirmationText = new Paragraph("Register your interest for property: '" + property.getTitle() + "'? A nominal fee might apply.");
        confirmationText.getStyle().set("text-align", "center");

        Button confirmButton = new Button("Yes, Register", event -> {
            // Pass the ORIGINAL property (with ID)
            processRegistration(property, loggedInUserEmail); // Pass email to avoid fetching again
            dialog.close();
            // Notifications handled within processRegistration
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
        String loggedInUserEmail = getLoggedInUserEmail(); // Fetch user email
        if (loggedInUserEmail == null) {
            Notification.show("Error: Could not determine user email. Please log in.", 3000, Notification.Position.TOP_CENTER);
            log.error("Failed to send initial email: User email not found.");
            return;
        }

        // Property object should be valid and have an ID as it comes from the card action
        if (property == null || property.getPropertyId() == null) {
             Notification.show("Error: Cannot book appointment due to invalid property data.", 3000, Notification.Position.TOP_CENTER);
             log.error("CRITICAL: sendIntialEmail called with invalid property: {}", property);
             return;
        }

        log.info("Creating appointment for user '{}' for property ID '{}' at {}", loggedInUserEmail, property.getPropertyId(), dateTime);

        Appointment appointment = new Appointment();
        appointment.setDateTime(dateTime);
        appointment.setNotes("Appointment request for: " + property.getTitle()); // More specific note
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setProperty(property); // Link the EXISTING property
        appointment.setUserId(loggedInUserEmail);

        try {
            appointmentService.saveAppointment(appointment);
            log.info("Appointment saved successfully with ID: {}", appointment.getAppointmentId());
            // Send email only AFTER successful save
            emailService.sendIntialEmail(loggedInUserEmail);
            log.info("Initial confirmation email sent to {}", loggedInUserEmail);
            NotificationUtils.showStyledNotification("Appointment requested. Check your email for confirmation.", 3000);
        } catch (Exception e) {
            log.error("Error saving appointment or sending initial email for user {}", loggedInUserEmail, e);
            Notification.show("Error booking appointment. Please try again later.", 5000, Notification.Position.MIDDLE);
        }
    }

    private void processRegistration(Property property, String loggedInUserEmail) {
        // Property and email should be valid based on checks in openRegisterDialog
        if (property == null || property.getPropertyId() == null || loggedInUserEmail == null) {
            log.error("CRITICAL: processRegistration called with invalid arguments. Property: {}, Email: {}", property, loggedInUserEmail);
            Notification.show("Internal Error: Cannot process registration.", 3000, Notification.Position.MIDDLE);
            return;
        }

        log.info("Processing registration for user '{}' and property ID '{}'", loggedInUserEmail, property.getPropertyId());
        NotificationUtils.showStyledNotification("Registration in progress...", 2000); // Brief "in progress" message

        Transactions transaction = new TransactionBuilder()
            .setAmount(50.00) // Example residential registration fee
            .setBuyerId(loggedInUserEmail)
            .setPropertyId(property.getPropertyId())
            .setStatus("PENDING")
            .build();

        try {
            // Save the initial transaction record
            transactionService.saveTransaction(transaction);
            log.info("Initial transaction record saved with ID: {} (Status: PENDING)", transaction.getTransactionId());

            // --- Simulate completion (Replace with actual logic) ---
            // In a real app, this status update would likely happen after
            // a payment gateway callback or manual admin approval.
            Thread.sleep(500); // Short delay purely for simulation/UX if needed
            transaction.setStatus("COMPLETED");
            transactionService.updateTransaction(transaction);
            // --- End Simulation ---

            log.info("Transaction ID {} status updated to COMPLETED.", transaction.getTransactionId());

            String token = transaction.getToken(); // Get token AFTER saving/updating
            if (token == null) {
                log.warn("Transaction token was null after saving/updating transaction ID: {}", transaction.getTransactionId());
                // Decide if email should still be sent or if this is an error state
            }

            // Send confirmation email
            emailService.sendTransactionEmail(loggedInUserEmail, property, token);
            log.info("Transaction completion email sent to {} for property '{}'", loggedInUserEmail, property.getTitle());

            NotificationUtils.showStyledNotification("Registration successful! Check your email.", 3000); // Notify user on success

        } catch (InterruptedException e) {
            log.warn("Thread sleep interrupted during registration simulation", e);
            Thread.currentThread().interrupt(); // Restore interrupt status
             Notification.show("Registration process was interrupted.", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
             log.error("Error processing registration transaction or sending email for user {}", loggedInUserEmail, e);
             // Provide a more specific error if possible (e.g., "Database error", "Email sending failed")
             Notification.show("Registration failed. Please try again later or contact support.", 5000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Retrieves the logged-in user's email.
     * NOTE: Reading from a file is highly insecure and not suitable for production.
     * Replace with proper authentication (e.g., Spring Security, Vaadin Session).
     *
     * @return The email address as a String, or null if not found or error occurs.
     */
    private String getLoggedInUserEmail() {
        String loggedInUserEmail = null;
        String filePath = "email.txt"; // VERY FRAGILE - REPLACE WITH AUTHENTICATION
        try {
            loggedInUserEmail = Files.readString(Paths.get(filePath)).trim();
            log.debug("Read email from file {}: '{}'", filePath, loggedInUserEmail);
            if (loggedInUserEmail.isEmpty()) {
                 log.warn("Email read from file {} is empty.", filePath);
                 return null; // Treat empty string as null/not found
            }
            // Basic email format check (optional but recommended)
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