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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

            if (email == null || email.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty()) {
                Notification.show("Please enter email, password, and select an appropriate role");
                return;
            }

            if (authService.login(email, password, role, userService)) {
                Notification.show("Login successful!");

                // --- Correct String Comparison ---
                if ("Admin".equals(role)) { // Use .equals() for strings!
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                }
                // Optional: Be explicit about the Buyer role if needed
                // else if ("Buyer".equals(role)) {
                else { // Handles "Buyer" and potentially any other non-Admin role
                    String filepath = "email.txt"; // Path relative to app working directory
                    try {
                        Files.writeString(Paths.get(filepath),
                                email,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.TRUNCATE_EXISTING); // Overwrites file!

                        // --- Correct Placement of Success Message ---
                        System.out.println("!!!WARNING!!! Written successfully to potentially insecure file: " + filepath);

                    } catch (IOException e) {
                        System.err.println("!!!ERROR!!! Failed to write email to temporary file: " + e.getMessage());
                        // Prevent navigation if file write fails crucial step
                        Notification.show("Internal error saving session info. Please try again.");
                        return; // Stop processing here
                    }
                    // Navigate only after successful file write (if that's the intent)
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