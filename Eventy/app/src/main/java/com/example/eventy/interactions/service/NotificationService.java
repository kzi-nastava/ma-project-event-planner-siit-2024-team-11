package com.example.eventy.interactions.service;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.interactions.model.Notification;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    String prefix = "notifications";

    @GET(prefix + "/{userId}")
    Call<PagedResponse<Notification>> getNotificationsByUserId(@Path("userId") Long userId,
                                                               @Query("page") int page,
                                                               @Query("size") int pageSize);
}
