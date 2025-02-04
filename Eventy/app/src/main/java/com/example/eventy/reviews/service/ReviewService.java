package com.example.eventy.reviews.service;

import com.example.eventy.reviews.model.CreateReview;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReviewService {
    String prefix = "reviews";

    @POST(prefix)
    Call<CreateReview> createReview(@Body CreateReview createReview);
}
