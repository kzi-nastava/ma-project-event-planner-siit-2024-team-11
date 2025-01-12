package com.example.eventy.events.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.events.model.CreatedEventType;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.events.model.EventTypeWithActivity;
import com.example.eventy.events.model.EventType;
import com.example.eventy.events.model.UpdateEventType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

public interface EventTypeService {
    String prefix = "events/types";
    @GET(prefix)
    Call<PagedResponse<EventTypeCard>> getEventTypes(@Query("search") String search, @Query("page") Integer page,
                                                     @Query("size") Integer size);

    @GET(prefix + "/active")
    Call<EventTypeCard[]> getActiveEventTypes();

    @GET(prefix + "/{id}")
    Call<EventTypeWithActivity> get(@Path("id") Long id);

    @POST(prefix)
    Call<EventType> add(@Body CreatedEventType type);

    @PUT(prefix + "/{id}/activation")
    Call<EventTypeWithActivity> toggleActivate(@Path("id") Long id);

    @PUT(prefix)
    Call<EventType> update(@Body UpdateEventType updateEventType);
}
