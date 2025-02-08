package com.example.eventy.reviews.model;

import com.example.eventy.events.model.Event;

public class UnreviewedEvent {
    private Long id;
    private String name;

    public UnreviewedEvent() {}

    public UnreviewedEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UnreviewedEvent(Event event) {
        this.id = event.getId();
        this.name = event.getName();
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
}

