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

import com.example.application.service.AppointmentService;
import com.example.application.service.EmailService;
import com.example.application.service.PropertyService;
import com.example.application.model.Appointment;
import com.example.application.model.Property;

@PageTitle("Commercial Properties")
@Route("commercial")
@CssImport("./styles/commercial-view.css")
public class CommercialView extends VerticalLayout {

    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;

    @Autowired
    public CommercialView(PropertyService propertyService, AppointmentService appointmentService, EmailService emailService) {
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        this.emailService = emailService;

        List<Property> properties = propertyService.getPropertiesByType("COMMERCIAL");

        addClassName("commercial-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 header = new H2("Commercial Properties");
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

        buttonLayout.add(bookButton);

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
                saveAppointmentAndSendEmail(property, selectedDateTime);
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

    private void saveAppointmentAndSendEmail(Property property, LocalDateTime dateTime) {
        // Replace with logged-in user's ID/email in real use case
        String userId = "pes2ug22cs543@pesu.pes.edu";

        Appointment appointment = new Appointment();
        appointment.setDateTime(dateTime);
        appointment.setNotes("commercial appointment");
        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointment.setProperty(property);

        appointment.setUserId(userId);

        appointmentService.saveAppointment(appointment);

    
        emailService.sendConfirmationEmail(userId, property, dateTime);
    }
}
