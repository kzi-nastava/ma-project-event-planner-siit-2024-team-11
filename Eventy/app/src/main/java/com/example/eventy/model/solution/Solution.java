package com.example.eventy.model.solution;

import com.example.eventy.model.event.EventType;

import java.util.ArrayList;
import java.util.Objects;

public class Solution {
    private String name;
    private Category category;
    private String description;
    private double price;
    private Integer discount;
    private ArrayList<String> imageUrls;
    private Boolean isDeleted;
    private Boolean isVisible;
    private Boolean isAvailable;
    private ArrayList<EventType> eventTypes;

    public Solution() {

    }

    public Solution(String name, Category category, String description, double price, Integer discount, ArrayList<String> imageUrls, Boolean isDeleted, Boolean isVisible, Boolean isAvailable, ArrayList<EventType> eventTypes) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.imageUrls = imageUrls;
        this.isDeleted = isDeleted;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
        this.eventTypes = eventTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public ArrayList<EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(ArrayList<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution solution = (Solution) o;
        return Double.compare(price, solution.price) == 0 && Objects.equals(name, solution.name) && Objects.equals(category, solution.category) && Objects.equals(description, solution.description) && Objects.equals(discount, solution.discount) && Objects.equals(imageUrls, solution.imageUrls) && Objects.equals(isDeleted, solution.isDeleted) && Objects.equals(isVisible, solution.isVisible) && Objects.equals(isAvailable, solution.isAvailable) && Objects.equals(eventTypes, solution.eventTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, description, price, discount, imageUrls, isDeleted, isVisible, isAvailable, eventTypes);
    }

    @Override
    public String toString() {
        StringBuilder eventTypesString = new StringBuilder();
        for (EventType eventType : eventTypes) {
            eventTypesString.append(eventType);
            eventTypesString.append(",");
        }

        return "Solution{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", imageUrls=" + imageUrls +
                ", isDeleted=" + isDeleted +
                ", isVisible=" + isVisible +
                ", isAvailable=" + isAvailable +
                ", eventTypes=" + eventTypesString +
                '}';
    }
}
