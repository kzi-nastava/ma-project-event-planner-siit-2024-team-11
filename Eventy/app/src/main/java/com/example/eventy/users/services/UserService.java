package com.example.eventy.users.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.users.model.CalendarOccupancy;
import com.example.eventy.users.model.UpdateUser;
import com.example.eventy.users.model.User;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    String userProfilePrefix = "users";
    String eventsPrefix = "events";
    String solutionsPrefix = "solutions";

    @GET(userProfilePrefix + "/{id}")
    Call<User> get(@Path("id") Long id);

    @PUT(userProfilePrefix)
    Call<User> update(@Body UpdateUser updateUser);

    @DELETE(userProfilePrefix + "/{id}")
    Call<Void> deactivate(@Path("id") Long id);

    @GET(userProfilePrefix + "/{id}/calendar")
    Call<CalendarOccupancy[]> getMyCalendar(@Path("id") Long id, @Query("startDate") LocalDate startDate,
                                            @Query("endDate") LocalDate endDate);

    @GET(eventsPrefix + "/favorite/{userId}")
    Call<PagedResponse<EventCard>> getMyFavoriteEvents(@Path("userId") Long userId, @Query("search") String search,
                                                       @Query("page") int page, @Query("size") int size);

    @GET(solutionsPrefix + "/favorite/{userId}")
    Call<PagedResponse<SolutionCard>> getMyFavoriteSolutions(@Path("userId") Long userId, @Query("search") String search,
                                                         @Query("page") int page, @Query("size") int size);

    @GET(eventsPrefix + "/organized/{userId}")
    Call<PagedResponse<EventCard>> getMyEvents(@Path("userId") Long userId, @Query("search") String search,
                                               @Query("page") int page, @Query("size") int size);

    @GET(eventsPrefix + "/catalog/{userId}")
    Call<PagedResponse<SolutionCard>> getMySolutions(@Path("userId") Long userId, @Query("search") String search,
                                                     @Query("page") int page, @Query("size") int size);
}
