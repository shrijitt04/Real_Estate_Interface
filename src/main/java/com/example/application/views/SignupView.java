package com.example.application.views;

import com.example.application.service.UserService;
import com.example.application.model.User;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("signup")
@PageTitle("Sign Up | Real Estate App")
@CssImport("./styles/signup-view.css")
public class SignupView extends VerticalLayout {    

    private final UserService userService;

    public SignupView(UserService userService) {
        this.userService = userService;
        
        // Main layout setup
        addClassName("signup-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Create card container
        VerticalLayout signupCard = new VerticalLayout();
        signupCard.addClassName("signup-card");
        signupCard.setAlignItems(Alignment.CENTER);
        signupCard.setPadding(true);
        signupCard.setSpacing(true);

        // Header with logo and title
        Div headerDiv = new Div();
        headerDiv.addClassName("signup-header");
        
        Image logo = new Image("themes/realestate/images/real-estate-logo.png", "Real Estate App Logo");
        logo.addClassName("signup-logo");
        
        H2 title = new H2("Create Account");
        title.addClassName("signup-title");
        
        Paragraph subtitle = new Paragraph("Sign up to start your real estate journey");
        subtitle.addClassName("signup-subtitle");
        
        headerDiv.add(logo, title, subtitle);

        // Form fields
        TextField nameField = new TextField("Name");
        nameField.setPrefixComponent(VaadinIcon.USER.create());
        nameField.setRequiredIndicatorVisible(true);
        nameField.addClassName("signup-form-field");
        nameField.setPlaceholder("Enter your full name");

        EmailField emailField = new EmailField("Email");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setRequiredIndicatorVisible(true);
        emailField.addClassName("signup-form-field");
        emailField.setPlaceholder("your.email@example.com");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.addClassName("signup-form-field");
        passwordField.setPlaceholder("Create a strong password");

        TextField phoneField = new TextField("Phone Number");
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setRequiredIndicatorVisible(true);
        phoneField.addClassName("signup-form-field");
        phoneField.setPlaceholder("Enter your phone number");

        RadioButtonGroup<String> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Role");
        roleGroup.setItems("Buyer", "Admin");
        roleGroup.setItemEnabledProvider(role -> !"Admin".equals(role));
        roleGroup.setValue("Buyer");
        roleGroup.addClassName("signup-form-field");

        // Signup button
        Button signupButton = new Button("Sign Up");
        signupButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signupButton.addClassName("signup-button");
        signupButton.setIcon(new Icon(VaadinIcon.USER_CHECK));
        
        signupButton.addClickListener(event -> {
            try {
                if (nameField.isEmpty() || emailField.isEmpty() || passwordField.isEmpty() || 
                    phoneField.isEmpty() || roleGroup.isEmpty()) {
                        NotificationUtils.showStyledNotification("Please fill all fields", 3000);
                        return;
                }

                User user = new User();
                user.setName(nameField.getValue());
                user.setEmail(emailField.getValue());
                user.setPassword(passwordField.getValue());
                user.setPhone(phoneField.getValue());
                user.setRole(roleGroup.getValue());

                userService.saveUser(user);
                NotificationUtils.showStyledNotification("User registered successfully!", 3000);
                clearForm(nameField, emailField, passwordField, phoneField, roleGroup);
            } catch (Exception e) {
                NotificationUtils.showStyledNotification("Registration failed: " + e.getMessage(), 3000);   
                e.printStackTrace();
            }
        });

        // Footer with login link
        Div footerDiv = new Div();
        footerDiv.addClassName("signup-footer");
        
        Span loginText = new Span("Already have an account? ");
        RouterLink loginLink = new RouterLink("Login Now!", LoginView.class);
        
        footerDiv.add(loginText, loginLink);

        // Add all components to the card
        signupCard.add(headerDiv, nameField, emailField, passwordField, phoneField, roleGroup, signupButton, footerDiv);

        // Add the card to the main layout
        add(signupCard);
    }
    
    private void clearForm(TextField nameField, EmailField emailField, PasswordField passwordField, 
                           TextField phoneField, RadioButtonGroup<String> roleGroup) {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        phoneField.clear();
        roleGroup.clear();
    }
}