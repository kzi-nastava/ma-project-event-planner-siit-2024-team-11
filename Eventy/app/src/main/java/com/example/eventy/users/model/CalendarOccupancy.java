package com.example.eventy.users.model;

import java.time.LocalDate;

public class CalendarOccupancy {
    private String title;
    private Long id;
    private OccupancyType occupancyType;
    private LocalDate occupationStartDate;
    private LocalDate occupationEndDate;

    public CalendarOccupancy() {

    }

    public CalendarOccupancy(String title, Long id, OccupancyType occupancyType, LocalDate occupationStartDate, LocalDate occupationEndDate) {
        this.title = title;
        this.id = id;
        this.occupancyType = occupancyType;
        this.occupationStartDate = occupationStartDate;
        this.occupationEndDate = occupationEndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OccupancyType getOccupancyType() {
        return occupancyType;
    }

    public void setOccupancyType(OccupancyType occupancyType) {
        this.occupancyType = occupancyType;
    }

    public LocalDate getOccupationStartDate() {
        return occupationStartDate;
    }

    public void setOccupationStartDate(LocalDate occupationStartDate) {
        this.occupationStartDate = occupationStartDate;
    }

    public LocalDate getOccupationEndDate() {
        return occupationEndDate;
    }

    public void setOccupationEndDate(LocalDate occupationEndDate) {
        this.occupationEndDate = occupationEndDate;
    }
}

