package com.example.eventy.events.services;

import com.example.eventy.events.model.EventTypeWithActivity;
import com.example.eventy.events.model.Location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LocationService {
    String prefix = "locations";

    @GET(prefix + "/{locationId}")
    Call<Location> getLocation(@Path("locationId") Long locationId);

    @GET(prefix)
    Call<Location[]> getLocations();
}
