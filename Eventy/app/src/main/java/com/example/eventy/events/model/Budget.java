package com.example.eventy.events.model;

import java.time.LocalDateTime;
import java.util.List;

public class Budget {
    private List<BudgetItem> categoryItems;
    private LocalDateTime eventDate;

    public Budget() {}

    public List<BudgetItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(List<BudgetItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
