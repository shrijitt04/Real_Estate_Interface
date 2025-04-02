package com.example.application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    private String description;
    private String location;
    private double price;
    private int size;
    private String status;
    private String title;
    private String type;  // COMMERCIAL, INDUSTRIAL, RESIDENTIAL

    // Constructors
    public Property() {}

    public Property(String description, String location, double price, int size, String status, String title, String type) {
        this.description = description;
        this.location = location;
        this.price = price;
        this.size = size;
        this.status = status;
        this.title = title;
        this.type = type;
    }

    // Getters and Setters
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
