package com.example.eventy.model.solution;

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

    public Solution() {

    }

    public Solution(String name, Category category, String description, double price, Integer discount, ArrayList<String> imageUrls, Boolean isDeleted, Boolean isVisible, Boolean isAvailable) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.imageUrls = imageUrls;
        this.isDeleted = isDeleted;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution solution = (Solution) o;
        return Double.compare(price, solution.price) == 0 && Objects.equals(name, solution.name) && Objects.equals(category, solution.category) && Objects.equals(description, solution.description) && Objects.equals(discount, solution.discount) && Objects.equals(imageUrls, solution.imageUrls) && Objects.equals(isDeleted, solution.isDeleted) && Objects.equals(isVisible, solution.isVisible) && Objects.equals(isAvailable, solution.isAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, description, price, discount, imageUrls, isDeleted, isVisible, isAvailable);
    }

    @Override
    public String toString() {
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
                '}';
    }
}
