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

@PageTitle("Residential Properties")
@Route("industrial")
@CssImport("./styles/industrial-view.css")
public class IndustrialView extends VerticalLayout {
    private final PropertyService propertyService;

    @Autowired
    public IndustrialView(PropertyService propertyService) {
        this.propertyService = propertyService;
        List<Property> properties = propertyService.getPropertiesByType("INDUSTRIAL");
        
        addClassName("industrial-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        H2 header = new H2("Industrial Properties");
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
        
        Button buyButton = new Button("Buy Land");
        buyButton.addClassName("buy-button");
        
        Button bookButton = new Button("Book Appointment");
        bookButton.addClassName("book-button");
        
        if (!"AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            buyButton.setEnabled(false);
            buyButton.addClassName("disabled-button");
        }
        
        buttonLayout.add(buyButton, bookButton);
        
        card.add(title, infoDiv, description, price, status, buttonLayout);
        
        return card;
    }
}