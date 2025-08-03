package com.example.eventy.events.model;

import com.example.eventy.solutions.model.SolutionHistory;

import java.util.List;

public class BudgetItem {
    private Long id;
    private String category;
    private double plannedFunds;
    private List<SolutionHistory> budgetedEntries;

    public BudgetItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPlannedFunds() {
        return plannedFunds;
    }

    public void setPlannedFunds(double plannedFunds) {
        this.plannedFunds = plannedFunds;
    }

    public List<SolutionHistory> getBudgetedEntries() {
        return budgetedEntries;
    }

    public void setBudgetedEntries(List<SolutionHistory> budgetedEntries) {
        this.budgetedEntries = budgetedEntries;
    }
}
