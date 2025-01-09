package com.example.eventy.events.model;

import java.util.List;

public class CreatedEventType {
    private String name;
    private String description;
    private List<Long> recommendedSolutionCategoriesIds;

    public CreatedEventType() {

    }

    public CreatedEventType(String name, String description, List<Long> recommendedSolutionCategories) {
        this.name = name;
        this.description = description;
        this.recommendedSolutionCategoriesIds = recommendedSolutionCategories;
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

    public List<Long> getRecommendedSolutionCategoriesIds() {
        return recommendedSolutionCategoriesIds;
    }

    public void setRecommendedSolutionCategoriesIds(List<Long> recommendedSolutionCategories) {
        this.recommendedSolutionCategoriesIds = recommendedSolutionCategories;
    }
}

