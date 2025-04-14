package com.example.application.views;

import com.example.application.service.AuthService;
import com.example.application.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@PageTitle("Login | Real Estate App")
public class LoginView extends VerticalLayout {

    private final UserService userService;
    private final AuthService authService;

    public LoginView(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        H2 title = new H2("Login to Real Estate App");
        
        EmailField emailField = new EmailField("Email");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidth("300px");
        
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setWidth("300px");

        RadioButtonGroup<String> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Role");
        roleGroup.setItems("Buyer", "Admin");
        
        Button loginButton = new Button("Login", event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String role = roleGroup.getValue();
            
            if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Notification.show("Please enter both email and password and select an approproate role");
                return;
            }
            
            if (authService.login(email, password, role, userService)) {
                Notification.show("Login successful!");
                if (role=="Admin"){
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                }
                else{
                    getUI().ifPresent(ui -> ui.navigate("User_Home"));
                }
            } else {
                Notification.show("Bad Credentials");
            }
        });
        
        Paragraph signupText = new Paragraph("Don't have an account? ");
        RouterLink signupLink = new RouterLink("Sign up", SignupView.class);
        signupText.add(signupLink);
        
        add(title, emailField, passwordField, roleGroup, loginButton, signupText);
    }
}