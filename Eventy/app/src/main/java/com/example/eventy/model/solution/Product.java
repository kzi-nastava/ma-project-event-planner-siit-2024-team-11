package com.example.eventy.model.solution;

import com.example.eventy.model.event.EventType;

import java.util.ArrayList;

public class Product extends Solution {

    public Product() {

    }

    public Product(String name, Category category, String description, double price, Integer discount, ArrayList<String> imageUrls, Boolean isDeleted, Boolean isVisible, Boolean isAvailable, ArrayList<EventType> eventTypes) {
        super(name, category, description, price, discount, imageUrls, isDeleted, isVisible, isAvailable, eventTypes);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
