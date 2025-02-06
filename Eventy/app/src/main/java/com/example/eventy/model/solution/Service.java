package com.example.eventy.model.solution;

import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.events.model.EventType;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Service {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer discount;
    private List<String> imageUrls;
    @JsonProperty("isVisible")
    private Boolean isVisible;
    @JsonProperty("isAvailable")
    private Boolean isAvailable;
    private Category category;
    private Collection<EventType> relatedEventTypes;
    private String specifics;
    private Integer minReservationTime;
    private Integer maxReservationTime;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    @JsonProperty("automaticReservationAcceptance")
    private Boolean automaticReservationAcceptance;

    public Service() {

    }

    public Service(long id, String name, String description, double price, int discount, List<String> imageUrls, boolean isVisible, boolean isAvailable, Category category, Collection<EventType> relatedEventTypes, String specifics, int minReservationTime, int maxReservationTime, int reservationDeadline, int cancellationDeadline, boolean automaticReservationAcceptance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.imageUrls = imageUrls;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
        this.category = category;
        this.relatedEventTypes = relatedEventTypes;
        this.specifics = specifics;
        this.minReservationTime = minReservationTime;
        this.maxReservationTime = maxReservationTime;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.automaticReservationAcceptance = automaticReservationAcceptance;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Collection<EventType> getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public void setRelatedEventTypes(Collection<EventType> relatedEventTypes) {
        this.relatedEventTypes = relatedEventTypes;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public Integer getMinReservationTime() {
        return minReservationTime;
    }

    public void setMinReservationTime(Integer minReservationTime) {
        this.minReservationTime = minReservationTime;
    }

    public Integer getMaxReservationTime() {
        return maxReservationTime;
    }

    public void setMaxReservationTime(Integer maxReservationTime) {
        this.maxReservationTime = maxReservationTime;
    }

    public Integer getReservationDeadline() {
        return reservationDeadline;
    }

    public void setReservationDeadline(Integer reservationDeadline) {
        this.reservationDeadline = reservationDeadline;
    }

    public Integer getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(Integer cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Boolean getAutomaticReservationAcceptance() {
        return automaticReservationAcceptance;
    }

    public void setAutomaticReservationAcceptance(Boolean automaticReservationAcceptance) {
        this.automaticReservationAcceptance = automaticReservationAcceptance;
    }
}
