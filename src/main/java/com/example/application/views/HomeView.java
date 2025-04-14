package com.example.application.views;

import java.util.List;

import com.example.application.model.Property;
import com.example.application.service.AuthService;
import com.example.application.service.PropertyService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("admin")
@PageTitle("Home | Real Estate App")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;
    private final PropertyService propertyService;

    public HomeView(AuthService authService, PropertyService propertyService) {
        this.authService = authService;
        this.propertyService = propertyService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        List<Property> properties = propertyService.getAllProperties();

        Grid<Property> propertyGrid = new Grid<>(Property.class);
        propertyGrid.setItems(properties);

        propertyGrid.setColumns("title", "location", "price", "type", "status");
        propertyGrid.getColumnByKey("title").setHeader("Title");
        propertyGrid.getColumnByKey("location").setHeader("Location");
        propertyGrid.getColumnByKey("price").setHeader("Price");
        propertyGrid.getColumnByKey("type").setHeader("Type");
        propertyGrid.getColumnByKey("status").setHeader("Status");

        propertyGrid.setHeight("500px");

        Button addButton = new Button("Add Property");
        addButton.addClickListener(e -> openPropertyDialog(propertyGrid));

        add(propertyGrid, addButton);
    }

    private void openPropertyDialog(Grid<Property> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add a new Property!");

        // Form fields
        TextField titleField = new TextField("Title");
        TextField descriptionField = new TextField("Description");
        TextField locationField = new TextField("Location");
        NumberField priceField = new NumberField("Price");
        NumberField sizeField = new NumberField("Size");

        ComboBox<String> typeField = new ComboBox<>("Type");
        typeField.setItems("Residential", "Commercial", "Industrial");

        ComboBox<String> statusField = new ComboBox<>("Status");
        statusField.setItems("AVAILABLE", "SOLD", "RENTED");

        // Buttons
        Button saveButton = new Button("Save Property");
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        // Save logic
        saveButton.addClickListener(e -> {
            if (titleField.isEmpty() || descriptionField.isEmpty() || locationField.isEmpty() ||
                priceField.isEmpty() || sizeField.isEmpty() || typeField.isEmpty() || statusField.isEmpty()) {

                Notification.show("Please fill all the details", 3000, Notification.Position.MIDDLE);
                return;
            }

            Property newProperty = new Property();
            newProperty.setTitle(titleField.getValue());
            newProperty.setDescription(descriptionField.getValue());
            newProperty.setLocation(locationField.getValue());
            newProperty.setPrice(priceField.getValue() != null ? priceField.getValue() : 0.0);
            newProperty.setSize(sizeField.getValue() != null ? sizeField.getValue().intValue() : 0);
            newProperty.setType(typeField.getValue());
            newProperty.setStatus(statusField.getValue());

            propertyService.saveProperty(newProperty); 
            Notification.show("Property added successfully!");
            dialog.close();

            grid.setItems(propertyService.getAllProperties()); // Refresh the grid
        });

        VerticalLayout formLayout = new VerticalLayout(
            titleField,
            descriptionField,
            locationField,
            priceField,
            sizeField,
            typeField,
            statusField,
            new HorizontalLayout(saveButton, cancelButton)
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        dialog.add(formLayout);
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!authService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}
