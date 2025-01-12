package com.example.eventy.events.services;

import com.example.eventy.events.model.OrganizeEvent;
import com.example.eventy.events.model.Event;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventService {
    String prefix = "events";

    @POST(prefix)
    Call<Event> organizeEvent(@Body OrganizeEvent organizeEvent);
}
