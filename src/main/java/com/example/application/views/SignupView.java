package com.example.application.views;

import com.example.application.service.UserService;
import com.example.application.model.User;
import com.example.application.views.NotificationUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
// import org.springframework.web.client.RestTemplate;
import com.vaadin.flow.router.RouterLink;

@Route("signup")
@PageTitle("Sign Up")
// @PermitAll
public class SignupView extends VerticalLayout {    

    private final UserService userService;

    public SignupView(UserService userService) {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        this.userService = userService;
        
        TextField nameField = new TextField("Name");
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");
        TextField phoneField = new TextField("Phone Number");
        RadioButtonGroup<String> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Role");
        roleGroup.setItems("Buyer", "Admin");
        
       
        roleGroup.setItemEnabledProvider(role -> !"Admin".equals(role));
        
        
        roleGroup.setValue("Buyer");
        
        Button signupButton = new Button("Sign Up", event -> {
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

        Paragraph loginText = new Paragraph("Have an account? ");
        RouterLink loginLink = new RouterLink("Login Now!", LoginView.class);
        loginText.add(loginLink);

        add(nameField, emailField, passwordField, phoneField, roleGroup, signupButton, loginText);
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