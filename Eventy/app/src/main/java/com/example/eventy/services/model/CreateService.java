package com.example.eventy.services.model;

import java.util.ArrayList;
import java.util.List;

public class CreateService {
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private ArrayList<String> imageUrls;
    private Long providerId;
    private Long categoryId;
    private List<Long> relatedEventTypeIds;
    private String specifics;
    private Integer minReservationTime;
    private Integer maxReservationTime;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private Boolean automaticReservationAcceptance;

    public CreateService() {}

    public CreateService(String name, String description, double price, double discount, ArrayList<String> imageUrls, Long providerId, Long categoryId, List<Long> relatedEventTypeIds, String specifics, int minReservationTime, int maxReservationTime, int reservationDeadline, int cancellationDeadline, boolean automaticReservationAcceptance) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.imageUrls = imageUrls;
        this.providerId = providerId;
        this.categoryId = categoryId;
        this.relatedEventTypeIds = relatedEventTypeIds;
        this.specifics = specifics;
        this.minReservationTime = minReservationTime;
        this.maxReservationTime = maxReservationTime;
        this.reservationDeadline = reservationDeadline;
        this.cancellationDeadline = cancellationDeadline;
        this.automaticReservationAcceptance = automaticReservationAcceptance;
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

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getRelatedEventTypeIds() {
        return relatedEventTypeIds;
    }

    public void setRelatedEventTypeIds(List<Long> relatedEventTypeIds) {
        this.relatedEventTypeIds = relatedEventTypeIds;
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
