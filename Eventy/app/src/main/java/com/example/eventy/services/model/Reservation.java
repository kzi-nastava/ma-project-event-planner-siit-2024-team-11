package com.example.eventy.services.model;

import java.time.LocalDateTime;

public class Reservation {
    private Long selectedEventId;
    private Long selectedServiceId;
    private LocalDateTime reservationStartDateTime;
    private LocalDateTime reservationEndDateTime;

    public Reservation() {}

    public Reservation(Long selectedEventId, Long selectedServiceId, LocalDateTime reservationStartDateTime, LocalDateTime reservationEndDateTime) {
        this.selectedEventId = selectedEventId;
        this.selectedServiceId = selectedServiceId;
        this.reservationStartDateTime = reservationStartDateTime;
        this.reservationEndDateTime = reservationEndDateTime;
    }

    public Long getSelectedEventId() {
        return selectedEventId;
    }

    public void setSelectedEventId(Long selectedEventId) {
        this.selectedEventId = selectedEventId;
    }

    public Long getSelectedServiceId() {
        return selectedServiceId;
    }

    public void setSelectedServiceId(Long selectedServiceId) {
        this.selectedServiceId = selectedServiceId;
    }

    public LocalDateTime getReservationStartDateTime() {
        return reservationStartDateTime;
    }

    public void setReservationStartDateTime(LocalDateTime reservationStartDateTime) {
        this.reservationStartDateTime = reservationStartDateTime;
    }

    public LocalDateTime getReservationEndDateTime() {
        return reservationEndDateTime;
    }

    public void setReservationEndDateTime(LocalDateTime reservationEndDateTime) {
        this.reservationEndDateTime = reservationEndDateTime;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "selectedEventId=" + selectedEventId +
                ", selectedServiceId=" + selectedServiceId +
                ", reservationStartDateTime=" + reservationStartDateTime +
                ", reservationEndDateTime=" + reservationEndDateTime +
                '}';
    }
}

