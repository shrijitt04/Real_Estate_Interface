package com.example.application.views;

import java.util.List;

import com.example.application.model.Property;
import com.example.application.service.AuthService;
import com.example.application.service.PropertyService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        // Only show the fields you want (optional)
        propertyGrid.setColumns("title", "location", "price", "type", "status");

        // Optional: Set column headers
        propertyGrid.getColumnByKey("title").setHeader("Title");
        propertyGrid.getColumnByKey("location").setHeader("Location");
        propertyGrid.getColumnByKey("price").setHeader("Price");
        propertyGrid.getColumnByKey("type").setHeader("Type");
        propertyGrid.getColumnByKey("status").setHeader("Status");

        add(propertyGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!authService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}
