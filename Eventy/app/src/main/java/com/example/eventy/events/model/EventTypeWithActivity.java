package com.example.eventy.events.model;

import com.example.eventy.solutions.model.CategoryWithID;

import java.util.List;

public class EventTypeWithActivity {
    private Long id;
    private String name;
    private String description;
    private List<CategoryWithID> recommendedSolutionCategories;
    private boolean isActive;

    public EventTypeWithActivity() {

    }

    public EventTypeWithActivity(Long id, String name, String description, List<CategoryWithID> recommendedSolutionCategories,
                                    boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedSolutionCategories = recommendedSolutionCategories;
        this.isActive = isActive;
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

    public List<CategoryWithID> getRecommendedSolutionCategories() {
        return recommendedSolutionCategories;
    }

    public void setRecommendedSolutionCategories(List<CategoryWithID> recommendedSolutionCategories) {
        this.recommendedSolutionCategories = recommendedSolutionCategories;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

