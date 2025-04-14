package com.example.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.application.repository.PropertyRepository;
import com.example.application.model.Property;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getPropertiesByType(String type) {
        return propertyRepository.findByType(type.toUpperCase());
    }

    public Property saveProperty(Property property){
        return propertyRepository.save(property);
    }
}
