package com.example.application.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
// import com.vaadin.flow.component.animation.Animation;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
// import com.vaadin.flow.server.PWA;
// import com.vaadin.flow.theme.lumo.Lumo;

// import com.vaadin.flow.theme.Theme;

@Route("temp")
@PageTitle("Real Estate Dashboard")
@CssImport("./styles/shared-styles.css")
@CssImport("./styles/animations.css")
// @Theme(value = "my-theme", variant = Lumo.DARK)
public class MainView extends AppLayout {

    private String userName = "John Doe"; // This would typically come from your authentication system

    public MainView() {
        createHeader();
        createDrawer();
        setContent(createContent());
        
        // Apply animation class to the entire view
        getElement().getClassList().add("fade-in");
    }

    private void createHeader() {
        H3 logo = new H3("Real Estate Pro");
        logo.addClassName("logo");

        Span welcome = new Span("Welcome, " + userName);
        welcome.addClassName("welcome-text");
        
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, welcome);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassName("header");
        
        addToNavbar(header);
    }

    private void createDrawer() {
        // Create navigation buttons with icons and animations
        addToDrawer(createNavigationButton("Dashboard", VaadinIcon.HOME));
        addToDrawer(createNavigationButton("View Properties", VaadinIcon.SEARCH));
        addToDrawer(createNavigationButton("My Properties", VaadinIcon.BUILDING));
        addToDrawer(createNavigationButton("Favorites", VaadinIcon.HEART));
        addToDrawer(createNavigationButton("Messages", VaadinIcon.ENVELOPE));
        addToDrawer(createNavigationButton("My Profile", VaadinIcon.USER));
        addToDrawer(createNavigationButton("Settings", VaadinIcon.COG));
        addToDrawer(createNavigationButton("Logout", VaadinIcon.SIGN_OUT));
    }

    private Component createNavigationButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.addClassName("menu-button");
        
        // Add click listener
        button.addClickListener(e -> {
            Notification notification = new Notification(
                    "Navigating to " + text, 
                    3000, 
                    Notification.Position.BOTTOM_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.open();
        });
        
        return button;
    }

    private Component createContent() {
        // Main content area
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassName("content");

        // Dashboard header
        H1 header = new H1("Your Real Estate Dashboard");
        header.addClassName("slide-in-from-left");
        
        // Stats section
        HorizontalLayout stats = createStatsLayout();
        stats.addClassName("slide-in-from-right");
        
        // Featured properties section
        VerticalLayout featuredProperties = new VerticalLayout();
        featuredProperties.add(new H3("Featured Properties"));
        featuredProperties.add(createPropertyGrid());
        featuredProperties.addClassName("slide-in-from-bottom");
        
        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.add(new H3("Recent Activity"));
        recentActivity.add(createActivityList());
        recentActivity.addClassName("slide-in-from-bottom");
        recentActivity.getStyle().set("animation-delay", "200ms");
        
        // Add all components to the main content
        content.add(header, stats, featuredProperties, recentActivity);
        
        return content;
    }

    private HorizontalLayout createStatsLayout() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        stats.setSpacing(true);
        
        stats.add(createStatCard("Properties Viewed", "42", VaadinIcon.EYE));
        stats.add(createStatCard("Saved Properties", "15", VaadinIcon.BOOKMARK));
        stats.add(createStatCard("New Messages", "7", VaadinIcon.ENVELOPE));
        stats.add(createStatCard("Appointments", "3", VaadinIcon.CALENDAR));
        
        return stats;
    }
    
    private Component createStatCard(String title, String value, VaadinIcon icon) {
        Div card = new Div();
        card.addClassName("stat-card");
        card.addClassName("pulse-on-hover");
        
        Icon cardIcon = icon.create();
        cardIcon.addClassName("stat-icon");
        
        H1 valueText = new H1(value);
        valueText.addClassName("stat-value");
        
        Paragraph titleText = new Paragraph(title);
        titleText.addClassName("stat-title");
        
        card.add(cardIcon, valueText, titleText);
        
        return card;
    }
    
    private Component createPropertyGrid() {
        HorizontalLayout grid = new HorizontalLayout();
        grid.setWidthFull();
        grid.setSpacing(true);
        
        grid.add(createPropertyCard("Modern Apartment", "$250,000", "123 Main St"));
        grid.add(createPropertyCard("Luxury Villa", "$1,200,000", "456 Ocean Ave"));
        grid.add(createPropertyCard("Family Home", "$450,000", "789 Park Rd"));
        
        return grid;
    }
    
    private Component createPropertyCard(String title, String price, String address) {
        Div card = new Div();
        card.addClassName("property-card");
        card.addClassName("scale-on-hover");
        
        Image image = new Image("https://via.placeholder.com/300x200?text=Property", "Property Image");
        image.setWidth("100%");
        
        H3 propertyTitle = new H3(title);
        Span propertyPrice = new Span(price);
        propertyPrice.addClassName("property-price");
        
        Paragraph propertyAddress = new Paragraph(address);
        
        Button viewButton = new Button("View Details", VaadinIcon.ARROW_RIGHT.create());
        viewButton.addClassName("view-button");
        
        card.add(image, propertyTitle, propertyPrice, propertyAddress, viewButton);
        
        return card;
    }
    
    private Component createActivityList() {
        VerticalLayout list = new VerticalLayout();
        list.setSpacing(false);
        list.setPadding(false);
        
        list.add(createActivityItem("You viewed Modern Apartment", "2 hours ago"));
        list.add(createActivityItem("Agent John Smith replied to your message", "Yesterday"));
        list.add(createActivityItem("Price dropped on Luxury Villa", "2 days ago"));
        
        return list;
    }
    
    private Component createActivityItem(String text, String time) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        item.addClassName("activity-item");
        
        Span activityText = new Span(text);
        Span timeText = new Span(time);
        timeText.addClassName("time-text");
        
        item.add(activityText);
        item.add(timeText);
        item.expand(activityText);
        
        return item;
    }
}