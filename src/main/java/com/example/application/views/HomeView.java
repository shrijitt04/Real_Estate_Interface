package com.example.application.views;

import java.time.LocalDateTime;
import java.util.List;

import com.example.application.model.Appointment;
import com.example.application.model.Property;
import com.example.application.model.User;
import com.example.application.service.AuthService;
import com.example.application.service.PropertyService;
// import com.example.application.repository.AppointmentRepository;
import com.example.application.service.AppointmentService;
import com.example.application.service.UserService;
import com.example.application.service.EmailService;





import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;


@Route("admin")
@PageTitle("Home | Real Estate App")
@CssImport("./styles/admin-view.css")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;
    private final PropertyService propertyService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final EmailService emailService;
    private FlexLayout propertiesLayout;

    public HomeView(AuthService authService, PropertyService propertyService, AppointmentService appointmentService, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.propertyService = propertyService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.emailService = emailService;

        addClassName("admin-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Header with Add Property button
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.addClassName("header-layout");

        Button addButton = new Button("Add New Property");
        addButton.addClassName("add-property-button");
        addButton.addClickListener(e -> openPropertyDialog(null));

        Button appointmentButton = new Button("Manage Appointments");
        appointmentButton.addClassName("add-property-button");
        appointmentButton.addClickListener(e->showAllAppointments());

        Button userButton = new Button("Manage Users");
        userButton.addClassName("add-property-button");
        userButton.addClickListener(e->showAllUsers());

        H2 title = new H2("Property Management");
        title.addClassName("page-title");

        headerLayout.add(addButton, appointmentButton, userButton, title);
        headerLayout.setVerticalComponentAlignment(Alignment.CENTER, title);

        // Properties layout
        propertiesLayout = new FlexLayout();
        propertiesLayout.addClassName("properties-container");
        
        refreshProperties();

        add(headerLayout, propertiesLayout);
    }

    private void refreshProperties() {
        propertiesLayout.removeAll();
        List<Property> properties = propertyService.getAllProperties();
        
        properties.forEach(property -> {
            Div card = createPropertyCard(property);
            propertiesLayout.add(card);
        });
    }

    private Div createPropertyCard(Property property) {
        Div card = new Div();
        card.addClassName("property-card");
        
        // Title
        H2 title = new H2(property.getTitle());
        title.addClassName("property-title");
        
        // Location and size info
        Div infoDiv = new Div();
        infoDiv.addClassName("property-info");
        Span location = new Span("Location: " + property.getLocation());
        Span size = new Span("Size: " + (property.getSize()));
        infoDiv.add(location, size);
        
        // Description
        Paragraph description = new Paragraph(property.getDescription());
        description.addClassName("property-description");
        
        // Price
        H4 price = new H4("$" + property.getPrice());
        price.addClassName("property-price");
        
        // Type
        Span type = new Span("Type: " + property.getType());
        type.addClassName("property-type");
        
        // Status
        Span status = new Span(property.getStatus());
        status.addClassName("property-status");
        
        // Add status-specific class for coloring
        if ("AVAILABLE".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-available");
        } else if ("SOLD".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-sold");
        } else if ("BOOKED".equalsIgnoreCase(property.getStatus()) || 
                   "RENTED".equalsIgnoreCase(property.getStatus())) {
            status.addClassName("status-booked");
        }
        
        // Buttons
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.addClassName("button-container");
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setPadding(false);
        buttonLayout.setSpacing(true);
        
        Button editButton = new Button("Edit Details");
        editButton.addClassName("edit-button");
        editButton.addClickListener(e -> openPropertyDialog(property));
        
        Button deleteButton = new Button("Delete Property");
        deleteButton.addClassName("delete-button");

        deleteButton.addClickListener(e -> {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirmation");
        confirmDialog.setText("Are you sure you want to delete this property?");

        confirmDialog.setConfirmText("Delete");
        confirmDialog.setCancelText("Cancel");

        confirmDialog.addConfirmListener(event -> {
            propertyService.deleteProperty((long) property.getPropertyId());
            Notification.show("Property deleted successfully!");
            refreshProperties(); // Refresh your list/grid
        });

        confirmDialog.open();
    });
        
        buttonLayout.add(status, editButton, deleteButton);
        
        // Add all elements to the card
        card.add(title, infoDiv, description, price, type, buttonLayout);
        
        return card;
    }

    private void openPropertyDialog(Property property) {
        boolean isEdit = property != null;
        
        Dialog dialog = new Dialog();
        dialog.addClassName("property-dialog");
        dialog.setWidth("600px");
        dialog.setHeaderTitle(isEdit ? "Edit Property" : "Add a new Property!");

        // Form fields
        TextField titleField = new TextField("Title");
        titleField.addClassName("dialog-field");
        
        TextField descriptionField = new TextField("Description");
        descriptionField.addClassName("dialog-field");
        
        TextField locationField = new TextField("Location");
        locationField.addClassName("dialog-field");
        
        NumberField priceField = new NumberField("Price");
        priceField.addClassName("dialog-field");
        
        NumberField sizeField = new NumberField("Size");
        sizeField.addClassName("dialog-field");

        ComboBox<String> typeField = new ComboBox<>("Type");
        typeField.setItems("Residential", "Commercial", "Industrial");
        typeField.addClassName("dialog-field");

        ComboBox<String> statusField = new ComboBox<>("Status");
        statusField.setItems("AVAILABLE", "SOLD", "RENTED");
        statusField.addClassName("dialog-field");

        // Pre-fill fields if editing
        if (isEdit) {
            titleField.setValue(property.getTitle() != null ? property.getTitle() : "");
            descriptionField.setValue(property.getDescription() != null ? property.getDescription() : "");
            locationField.setValue(property.getLocation() != null ? property.getLocation() : "");
            priceField.setValue(property.getPrice());
            sizeField.setValue((double)property.getSize());
            typeField.setValue(property.getType());
            statusField.setValue(property.getStatus());
        }

        // Buttons
        Button saveButton = new Button(isEdit ? "Update Property" : "Save Property");
        saveButton.addClassName("save-button");
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        // Save logic
        saveButton.addClickListener(e -> {
            if (titleField.isEmpty() || locationField.isEmpty() ||
                priceField.isEmpty() || typeField.isEmpty() || statusField.isEmpty()) {

                Notification.show("Please fill all the required details", 3000, Notification.Position.MIDDLE);
                return;
            }

            Property propertyToSave = isEdit ? property : new Property();
            propertyToSave.setTitle(titleField.getValue());
            propertyToSave.setDescription(descriptionField.getValue());
            propertyToSave.setLocation(locationField.getValue());
            propertyToSave.setPrice(priceField.getValue() != null ? priceField.getValue() : 0.0);
            propertyToSave.setSize(sizeField.getValue() != null ? sizeField.getValue().intValue() : 0);
            propertyToSave.setType(typeField.getValue());
            propertyToSave.setStatus(statusField.getValue());

            propertyService.saveProperty(propertyToSave); 
            Notification.show(isEdit ? "Property updated successfully!" : "Property added successfully!");
            dialog.close();

            refreshProperties(); // Refresh the property cards
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout formLayout = new VerticalLayout(
            titleField,
            descriptionField,
            locationField,
            priceField,
            sizeField,
            typeField,
            statusField,
            buttonLayout
        );
        formLayout.setPadding(true);
        formLayout.setSpacing(true);
        formLayout.addClassName("dialog-form");

        dialog.add(formLayout);
        dialog.open();
    }
    private void showAllAppointments() {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");
        dialog.setHeight("600px");
    
        Grid<Appointment> appointmentGrid = new Grid<>(Appointment.class, false);
    
        // Define visible columns
        appointmentGrid.addColumn(Appointment::getAppointmentId)
            .setHeader("Appointment ID").setAutoWidth(true);
    
        appointmentGrid.addColumn(Appointment::getDateTime)
            .setHeader("Date & Time").setAutoWidth(true);
    
        appointmentGrid.addColumn(Appointment::getStatus)
            .setHeader("Status").setAutoWidth(true);
    
        appointmentGrid.addColumn(appointment -> 
            appointment.getProperty() != null ? appointment.getProperty().getTitle() : "N/A"
        ).setHeader("Property").setAutoWidth(true);
    
        appointmentGrid.addColumn(Appointment::getUserId)
            .setHeader("User Email").setAutoWidth(true);
    
        appointmentGrid.addColumn(Appointment::getNotes)
            .setHeader("Notes").setAutoWidth(true).setFlexGrow(1);
    
        // Accept / Reject buttons
        appointmentGrid.addComponentColumn(appointment -> {
            HorizontalLayout actions = new HorizontalLayout();
    
            Button acceptBtn = new Button("Accept", click -> {
                appointment.setStatus(Appointment.Status.CONFIRMED);
                appointmentService.saveAppointment(appointment); 
                String userID = appointment.getUserId();
                Property property = new Property();
                property = appointment.getProperty();
                LocalDateTime datetime = appointment.getDateTime();
                emailService.sendConfirmationEmail(userID, property, datetime);
                Notification.show("Appointment confirmed.");
                appointmentGrid.setItems(appointmentService
                    .displayAllAppointments()
                    .stream()
                    .filter(a -> a.getStatus() == Appointment.Status.PENDING)
                    .toList());
            });
    
            Button rejectBtn = new Button("Reject", click -> {
                appointment.setStatus(Appointment.Status.CANCELLED);
                appointmentService.saveAppointment(appointment); 
                Property property = new Property();
                property = appointment.getProperty();
                emailService.sendRejectionEmail(appointment.getUserId(),property);
                Notification.show("Appointment rejected.");
                appointmentGrid.setItems(appointmentService
                    .displayAllAppointments()
                    .stream()
                    .filter(a -> a.getStatus() == Appointment.Status.PENDING)
                    .toList());
            });
    
            acceptBtn.getElement().getThemeList().add("primary");
            rejectBtn.getElement().getThemeList().add("error");
    
            actions.add(acceptBtn, rejectBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    
        // Set only pending appointments
        appointmentGrid.setItems(appointmentService.displayAllAppointments()
            .stream()
            .filter(a -> a.getStatus() == Appointment.Status.PENDING)
            .toList());
    
        dialog.add(appointmentGrid);
        dialog.open();
    }
    

    private void showAllUsers(){
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");
        dialog.setHeight("600px");

        Grid<User> userGrid = new Grid<>(User.class, false); // false to avoid auto columns

        // Set columns with custom headers and value providers
        userGrid.addColumn(user -> user.getUserId())
            .setHeader("User ID").setAutoWidth(true);

        userGrid.addColumn(user -> user.getName())
            .setHeader("Name").setAutoWidth(true);

        userGrid.addColumn(user -> user.getEmail())
            .setHeader("Email ID").setAutoWidth(true);

        userGrid.addColumn(user->user.getPhone())
            .setHeader("Phone Number").setAutoWidth(true);

        userGrid.addColumn(user->user.getRole())
            .setHeader("Role").setAutoWidth(true);
        

        userGrid.addComponentColumn(user-> {
            Button removeButton = new Button("Remove", e -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Confirmation");
                confirmDialog.setText("Are you sure you want to remove this appointment?");

                confirmDialog.setConfirmText("Delete");
                confirmDialog.setCancelText("Cancel");
                confirmDialog.addConfirmListener(event->{
                    userService.deleteUser(user.getUserId());
                    Notification.show("User Removed.",5000,Notification.Position.MIDDLE);
                    userGrid.setItems(userService.displayAllUsers());
                });
                confirmDialog.open();

            });
            removeButton.getElement().getThemeList().add("error");
            return removeButton;
        }).setHeader("Actions").setAutoWidth(true);

        userGrid.setItems(userService.displayAllUsers());
        userGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        userGrid.setWidth("1000px");
        userGrid.setHeight("500px");

        dialog.add(userGrid);
        dialog.open();
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!authService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}