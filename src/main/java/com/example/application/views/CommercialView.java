package com.example.application.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.application.service.PropertyService;
import com.example.application.model.Property;

@PageTitle("Commercial Properties")
@Route("commercial")
public class CommercialView extends VerticalLayout {
    private final PropertyService propertyService;
    private final Grid<Property> grid = new Grid<>(Property.class);

    @Autowired
    public CommercialView(PropertyService propertyService) {
        this.propertyService = propertyService;
        List<Property> properties = propertyService.getPropertiesByType("COMMERCIAL");
        grid.setItems(properties);

        grid.setColumns("propertyId", "title", "location", "price", "size", "status", "description");
        add(grid);
    }
}
