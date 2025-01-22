package com.example.eventy.solutions.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SolutionsFilter {
    private String search;
    private String type;
    private ArrayList<String> categories;
    private ArrayList<String> eventTypes;
    private String company;
    private String minPrice;
    private String maxPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAvailable;

    public SolutionsFilter() {}

    public SolutionsFilter(String search, String type, ArrayList<String> categories, ArrayList<String> eventTypes, String company, String minPrice, String maxPrice, LocalDateTime startDate, LocalDateTime endDate, Boolean isAvailable) {
        this.search = search;
        this.type = type;
        this.categories = categories;
        this.eventTypes = eventTypes;
        this.company = company;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAvailable = isAvailable;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(ArrayList<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "SolutionsFilter{" +
                "search='" + search + '\'' +
                ", type='" + type + '\'' +
                ", categories=" + categories +
                ", eventTypes=" + eventTypes +
                ", company='" + company + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
