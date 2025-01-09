package com.example.eventy.events.model;

import java.util.List;

public class UpdateEventType {
    private Long id;
    private String name;
    private String description;
    private List<Long> recommendedSolutionCategoriesIds;

    public UpdateEventType() {

    }

    public UpdateEventType(Long id, String name, String description, List<Long> recommendedSolutionCategoriesIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedSolutionCategoriesIds = recommendedSolutionCategoriesIds;
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

    public List<Long> getRecommendedSolutionCategoriesIds() {
        return recommendedSolutionCategoriesIds;
    }

    public void setRecommendedSolutionCategoriesIds(List<Long> recommendedSolutionCategories) {
        this.recommendedSolutionCategoriesIds = recommendedSolutionCategories;
    }
}

