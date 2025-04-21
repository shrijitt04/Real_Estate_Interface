package com.example.application.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.example.application.model.Property;

@CssImport("./styles/commercial-view.css")
public class PropertyCardFactory {
    public static Div createPropertyCard(Property property, Runnable onBookAppointment, Runnable onRegisterLand) {
        Div card = new Div();
        card.addClassName("property-card");

        // Title
        H2 title = new H2(property.getTitle());
        title.addClassName("property-title");

        // Location and Size
        Div infoDiv = new Div();
        infoDiv.addClassName("property-info");
        Span location = new Span("Location: " + property.getLocation());
        Span size = new Span("Size: " + property.getSize() + " sq.ft");
        infoDiv.add(location, size);

        // Description
        Paragraph description = new Paragraph(property.getDescription());
        description.addClassName("property-description");

        // Price
        H4 price = new H4("$" + property.getPrice());
        price.addClassName("property-price");

        // Status
        Span status = new Span(property.getStatus());
        status.addClassName("property-status");
        if ("AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-available");
        } else if ("SOLD".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-sold");
        } else if ("BOOKED".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-booked");
        }

        // Buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-container");

        Button bookButton = new Button("Book Appointment");
        bookButton.addClassName("book-button");
        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            bookButton.setEnabled(false);
            bookButton.addClassName("disabled-button");
        } else {
            bookButton.addClickListener(e -> onBookAppointment.run());
        }

        Button registerButton = new Button("Register Land");
        registerButton.addClassName("register-button");
        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            registerButton.setEnabled(false);
            registerButton.addClassName("disabled-button");
        } else {
            registerButton.addClickListener(e -> onRegisterLand.run());
        }

        buttonLayout.add(bookButton, registerButton);

        // Add all elements to the card
        card.add(title, infoDiv, description, price, status, buttonLayout);

        return card;
    }
}