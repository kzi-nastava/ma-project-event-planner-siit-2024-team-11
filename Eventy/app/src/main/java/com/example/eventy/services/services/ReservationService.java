package com.example.eventy.services.services;

import com.example.eventy.services.model.Reservation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReservationService {
    String prefix = "reservations";

    @POST(prefix)
    Call<Reservation> createReservation(@Body Reservation newReservation);
}
