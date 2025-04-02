package com.example.application.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("User_Home")
@PageTitle("Real Estate Interface")
public class UserHome extends VerticalLayout {

    public UserHome() {
        // Set up the layout
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        addClassName("animated-background");

        // Create a container for the content with animation
        Div contentContainer = new Div();
        contentContainer.addClassName("content-container");
        contentContainer.getElement().getStyle().set("animation", "fadeIn 1.5s ease-in-out");

        // Get username from session (in a real app, this would come from authentication)
        String username = VaadinSession.getCurrent().getAttribute("username") != null ? 
                          VaadinSession.getCurrent().getAttribute("username").toString() : 
                          "Guest";

        // Create welcome header with animation
        H1 welcomeHeader = new H1("Welcome to the Real Estate Retail Interface!");
        welcomeHeader.getElement().getStyle().set("animation", "slideDown 1s ease-out");
        
        // Create personalized message
        Paragraph userMessage = new Paragraph(username + ", What are you looking for?");
        userMessage.getElement().getStyle().set("animation", "fadeIn 2s ease-in-out");
        userMessage.getElement().getStyle().set("margin-top", "1rem");
        userMessage.getElement().getStyle().set("margin-bottom", "2rem");

        // Create dropdown with property types
        ComboBox<String> propertyTypeComboBox = new ComboBox<>("Property Type");
        propertyTypeComboBox.setItems("COMMERCIAL","INDUSTRIAL", "RESIDENTIAL");
        propertyTypeComboBox.setPlaceholder("Select property type");
        propertyTypeComboBox.setClearButtonVisible(true);
        propertyTypeComboBox.setWidth("300px");
        
        // Add animation to dropdown
        propertyTypeComboBox.getElement().getStyle().set("animation", "bounceIn 1.5s ease-in-out");
        
        // Add selection listener
        propertyTypeComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if(event.getValue().equals("COMMERCIAL")){
                    getUI().ifPresent(ui -> ui.navigate("commercial"));
                }
                if(event.getValue().equals("INDUSTRIAL")){
                    getUI().ifPresent(ui -> ui.navigate("industrial"));
                }
                if(event.getValue().equals("RESIDENTIAL")){
                    getUI().ifPresent(ui -> ui.navigate("residential"));
                }
                // In a real application, you would navigate to the selected property type page
                Paragraph selectionInfo = new Paragraph("You selected: " + event.getValue());
                selectionInfo.getElement().getStyle().set("animation", "fadeIn 1s ease-in-out");
                selectionInfo.getElement().getStyle().set("margin-top", "1rem");
                
                // Remove previous selection info if exists
                getChildren().forEach(component -> {
                    if (component instanceof Paragraph && 
                        ((Paragraph) component).getText().startsWith("You selected:, Now fuck off")) {
                        remove(component);
                    }
                });
                
                add(selectionInfo);
            }
        });

        // Add components to the container
        contentContainer.add(welcomeHeader, userMessage, propertyTypeComboBox);
        
        // Add container to the layout
        add(contentContainer);
        
        // Add client-side animation for background
        UI.getCurrent().getPage().executeJs(
            "document.documentElement.style.setProperty('--animate-duration', '3s');"
        );
    }
}

