package com.example.application.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport("./styles/commercial-view.css")
@CssImport("./styles/residential-view.css")
@CssImport("./styles/industrial-view.css")
@CssImport("./styles/admin-view.css")
@CssImport("./styles/shared-styles.css")

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        H1 logo = new H1("Commercial Properties");
        logo.addClassName("logo");

        HorizontalLayout header = new HorizontalLayout(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");

        addToNavbar(header);
    }
}