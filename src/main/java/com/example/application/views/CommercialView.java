package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.application.service.PropertyService;
import com.example.application.model.Property;

@PageTitle("Commercial Properties")
@Route("commercial")
@CssImport("./styles/commercial-view.css")
public class CommercialView extends VerticalLayout {
    private final PropertyService propertyService;

    @Autowired
    public CommercialView(PropertyService propertyService) {
        this.propertyService = propertyService;
        List<Property> properties = propertyService.getPropertiesByType("COMMERCIAL");
        
        // Set up the main layout
        addClassName("commercial-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        // Create header
        H2 header = new H2("Commercial Properties");
        header.addClassName("view-header");
        
        // Create a flex layout for the property cards
        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.addClassName("property-card-container");
        
        // Create property cards
        properties.forEach(property -> {
            Div card = createPropertyCard(property);
            cardsLayout.add(card);
        });
        
        add(header, cardsLayout);
    }
    
    private Div createPropertyCard(Property property) {
        Div card = new Div();
        card.addClassName("property-card");
        
        // Title
        H2 title = new H2(property.getTitle());
        title.addClassName("property-title");
        
        // Location and size info
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
        
        // Add status-specific class for coloring
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
        
        Button buyButton = new Button("Buy Land");
        buyButton.addClassName("buy-button");
        
        Button bookButton = new Button("Book Appointment");
        bookButton.addClassName("book-button");
        
        // Disable buy button if property is booked or sold
        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            buyButton.setEnabled(false);
            buyButton.addClassName("disabled-button");
        }
        
        buttonLayout.add(buyButton, bookButton);
        
        // Add all elements to the card
        card.add(title, infoDiv, description, price, status, buttonLayout);
        
        return card;
    }
}