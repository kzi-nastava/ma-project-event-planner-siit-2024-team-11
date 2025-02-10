package com.example.eventy.products.model;

import com.example.eventy.events.model.EventType;

import java.util.List;

public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<EventType> relatedEventTypes;
    private List<String> images;
    private boolean isVisible;
    private boolean isAvailable;

    public Product() {

    }

    public Product(Long id, String name, String description, double price, double discount, List<EventType> relatedEventTypes, List<String> images, boolean isVisible, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.relatedEventTypes = relatedEventTypes;
        this.images = images;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<EventType> getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public void setRelatedEventTypes(List<EventType> relatedEventTypes) {
        this.relatedEventTypes = relatedEventTypes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        this.isAvailable = available;
    }
}
