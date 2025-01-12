package com.example.eventy.events.model;

import com.example.eventy.model.solution.CategoryWithID;

import java.util.List;
import java.util.Objects;

public class EventType {
    private Long id;
    private String name;
    private String description;
    private List<CategoryWithID> recommendedSolutionCategories;

    public EventType() {}

    public EventType(Long id, String name, String description, List<CategoryWithID> recommendedSolutionCategories) {
        this.name = name;
        this.description = description;
        this.recommendedSolutionCategories = recommendedSolutionCategories;
    }

    public EventType(String name, String description, boolean isActive) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CategoryWithID> getRecommendedSolutionCategories() {
        return recommendedSolutionCategories;
    }

    public void setRecommendedSolutionCategories(List<CategoryWithID> recommendedSolutionCategories) {
        this.recommendedSolutionCategories = recommendedSolutionCategories;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return Objects.equals(id, eventType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "EventType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
