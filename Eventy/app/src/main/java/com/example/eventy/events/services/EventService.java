package com.example.eventy.events.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.OrganizeEvent;
import com.example.eventy.events.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventService {
    String prefix = "events";

    @POST(prefix)
    Call<Event> organizeEvent(@Body OrganizeEvent organizeEvent);

    @GET(prefix)
    Call<PagedResponse<EventCard>> getEvents(@Query("search") String search,
                                             @Query("eventTypes") ArrayList<String> eventTypes,
                                             @Query("maxParticipants") Integer maxParticipants,
                                             @Query("location") String location,
                                             @Query("startDate") LocalDateTime startDate,
                                             @Query("endDate") LocalDateTime endDate,
                                             @Query("page") int page,
                                             @Query("size") int pageSize,
                                             @Query("sort") String sort);
}
