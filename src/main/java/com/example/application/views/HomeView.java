package com.example.application.views;

import com.example.application.model.User;
import com.example.application.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("admin_home")
@PageTitle("Home | Real Estate App")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;

    public HomeView(AuthService authService) {
        this.authService = authService;
        
        if (authService.isLoggedIn()) {
            User currentUser = authService.getCurrentUser();
            
            H2 title = new H2("Welcome to Real Estate App");
            H3 welcomeMessage = new H3("Hello, " + currentUser.getName() + "!");
            Paragraph userInfo = new Paragraph("Email: " + currentUser.getEmail());
            Paragraph roleInfo = new Paragraph("Role: " + currentUser.getRole());
            
            Button logoutButton = new Button("Logout", event -> {
                authService.logout();
                getUI().ifPresent(ui -> ui.navigate("login"));
            });
            
            add(title, welcomeMessage, userInfo, roleInfo, logoutButton);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!authService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}