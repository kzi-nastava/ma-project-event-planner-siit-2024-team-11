package com.example.eventy.model.solution;

import java.util.ArrayList;

public class Product extends Solution {

    public Product() {

    }

    public Product(String name, Category category, String description, double price, Integer discount, ArrayList<String> imageUrls, Boolean isDeleted, Boolean isVisible, Boolean isAvailable) {
        super(name, category, description, price, discount, imageUrls, isDeleted, isVisible, isAvailable);
    }

    @Override
    public String toString() {
        return "Product {" +
                "name='" + getName() + '\'' +
                ", category=" + getCategory() +
                ", description='" + getDescription() + '\'' +
                ", price=" + getPrice() +
                ", discount=" + getDiscount() +
                ", imageUrls=" + getImageUrls() +
                ", isDeleted=" + getIsDeleted() +
                ", isVisible=" + getIsVisible() +
                ", isAvailable=" + getIsAvailable() +
                '}';
    }
}
