package com.example.application.service;

import com.example.application.model.User;
// import com.example.application.model.Role;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import javax.management.relation.Role;

import org.springframework.stereotype.Service;

@Service
@VaadinSessionScope
public class AuthService {

    private User currentUser;

    public boolean login(String email, String password, String role, UserService userService) {
        var userOptional = userService.getUserByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Simple password check (you should use proper password encoding in production)
            if (user.getPassword().equals(password) && user.getRole().equals(role)) {
                setCurrentUser(user);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        setCurrentUser(null);
        VaadinSession.getCurrent().getSession().invalidate();
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void setCurrentUser(User user) {
        this.currentUser = user;
    }
}