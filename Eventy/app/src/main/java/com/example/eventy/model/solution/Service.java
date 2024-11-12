package com.example.eventy.model.solution;

import com.example.eventy.model.enums.ReservationConfirmationType;

import java.util.ArrayList;
import java.util.Objects;

public class Service extends Solution {
    private String specifics;
    private Integer minReservationTime;
    private Integer maxReservationTime;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private ReservationConfirmationType reservationConfirmationType;

    public Service() {

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

    public ReservationConfirmationType getReservationConfirmationType() {
        return reservationConfirmationType;
    }

    public void setReservationConfirmationType(ReservationConfirmationType reservationConfirmationType) {
        this.reservationConfirmationType = reservationConfirmationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        if (!super.equals(o)) return false;
        Service service = (Service) o;
        return Objects.equals(specifics, service.specifics) &&
                Objects.equals(minReservationTime, service.minReservationTime) &&
                Objects.equals(maxReservationTime, service.maxReservationTime) &&
                Objects.equals(reservationDeadline, service.reservationDeadline) &&
                Objects.equals(cancellationDeadline, service.cancellationDeadline) &&
                reservationConfirmationType == service.reservationConfirmationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specifics, minReservationTime, maxReservationTime,
                reservationDeadline, cancellationDeadline, reservationConfirmationType);
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + getName() + '\'' +
                ", category='" + getCategory() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", price=" + getPrice() +
                ", discount=" + getDiscount() +
                ", imageUrls=" + getImageUrls() +
                ", isDeleted=" + getIsDeleted() +
                ", isVisible=" + getIsVisible() +
                ", isAvailable=" + getIsAvailable() +
                ", specifics='" + specifics + '\'' +
                ", minReservationTime=" + minReservationTime +
                ", maxReservationTime=" + maxReservationTime +
                ", reservationDeadline=" + reservationDeadline +
                ", cancellationDeadline=" + cancellationDeadline +
                ", reservationConfirmationType=" + reservationConfirmationType +
                '}';
    }
}
