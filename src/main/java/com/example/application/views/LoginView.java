package com.example.application.views;

import com.example.application.service.AuthService;
import com.example.application.service.UserService;
import com.example.application.views.NotificationUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Route("")
@PageTitle("Login | Real Estate App")
@CssImport("./styles/login-view.css")
public class LoginView extends VerticalLayout {

    private final UserService userService;
    private final AuthService authService;

    public LoginView(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;

        // Main layout setup
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Create card container
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.addClassName("login-card");
        loginCard.setAlignItems(Alignment.CENTER);
        loginCard.setPadding(true);
        loginCard.setSpacing(true);

        // Header with logo and title
        Div headerDiv = new Div();
        headerDiv.addClassName("login-header");
        
        Image logo = new Image("themes/realestate/images/real-estate-logo.png", "Real Estate App Logo");
        logo.addClassName("login-logo");
        
        H2 title = new H2("Welcome Back");
        title.addClassName("login-title");
        
        Paragraph subtitle = new Paragraph("Sign in to access your account");
        subtitle.addClassName("login-subtitle");
        
        headerDiv.add(logo, title, subtitle);

        // Form fields
        EmailField emailField = new EmailField();
        emailField.setLabel("Email");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setRequiredIndicatorVisible(true);
        emailField.addClassName("login-form-field");
        emailField.setPlaceholder("your.email@example.com");

        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.addClassName("login-form-field");
        passwordField.setPlaceholder("Enter your password");

        RadioButtonGroup<String> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Login as");
        roleGroup.setItems("Buyer", "Admin");
        roleGroup.setValue("Buyer"); // Set default value
        roleGroup.addClassName("login-form-field");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClassName("login-button");
        loginButton.setIcon(new Icon(VaadinIcon.SIGN_IN));
        
        loginButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String role = roleGroup.getValue(); 

            if (email == null || email.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty()) {
                NotificationUtils.showStyledNotification("Please fill in all fields", 3000);
                return;
            }

            if (authService.login(email, password, role, userService)) {
                NotificationUtils.showStyledNotification("Login successful", 3000);

                if ("Admin".equals(role)) { 
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                }
                
                else { 
                    String filepath = "email.txt"; 
                    try {
                        Files.writeString(Paths.get(filepath),
                                email,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.TRUNCATE_EXISTING);

                        System.out.println("!!!WARNING!!! Written successfully " + filepath);

                    } catch (IOException e) {
                        System.err.println("!!!ERROR!!! Failed to write email to temporary file: " + e.getMessage());
                        Notification.show("Internal error saving session info. Please try again.");
                        return; // Stop processing here
                    }
        
                    getUI().ifPresent(ui -> ui.navigate("User_Home"));
                }
            } else {
                Notification.show("Invalid email or password. Please try again.");
            }
        });

        // Footer with signup link
        Div footerDiv = new Div();
        footerDiv.addClassName("login-footer");
        
        Span signupText = new Span("Don't have an account? ");
        RouterLink signupLink = new RouterLink("Sign up", SignupView.class);
        
        footerDiv.add(signupText, signupLink);

        // Add all components to the card
        loginCard.add(headerDiv, emailField, passwordField, roleGroup, loginButton, footerDiv);

        // Add the card to the main layout
        add(loginCard);
    }
}