package com.example.application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int propertyId;

    private String description;
    private String location;
    private double price;
    private int size;
    private String status;
    private String title;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // Uses Enum instead of String 

    // Getters and Setters
    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }
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
    public PropertyType getType() { return type; }
    public void setType(PropertyType type) { this.type = type; }}
