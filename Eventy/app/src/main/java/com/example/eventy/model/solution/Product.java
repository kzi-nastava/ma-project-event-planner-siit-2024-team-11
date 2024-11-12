package com.example.eventy.model.solution;

import java.util.ArrayList;

public class Product extends Solution {

    public Product() {

    }

    public Product(String name, String description, double price, Integer discount, ArrayList<String> imageUrls, Boolean isDeleted, Boolean isVisible, Boolean isAvailable) {
        super(name, description, price, discount, imageUrls, isDeleted, isVisible, isAvailable);
    }
}
