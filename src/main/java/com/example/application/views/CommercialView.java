package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;


@Route("commercial")
@PageTitle("Commercial Properties")

public class CommercialView extends VerticalLayout{
    public CommercialView(){
        H2 title  = new H2("Welcome to Commercial Page");
        add(title);
    }
    
}

