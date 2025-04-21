package com.example.application.model;

public class PropertyBuilder {
    private String title;
    private String description;
    private String location;
    private double price;
    private int size;
    private String status;
    private String type;

    public PropertyBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public PropertyBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public PropertyBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public PropertyBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    public PropertyBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public PropertyBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public PropertyBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public Property build() {
        Property property = new Property();
        property.setTitle(this.title);
        property.setDescription(this.description);
        property.setLocation(this.location);
        property.setPrice(this.price);
        property.setSize(this.size);
        property.setStatus(this.status);
        property.setType(this.type);
        return property;
    }
}