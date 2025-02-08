package com.example.eventy.reviews.service;

import com.example.eventy.reviews.model.CreateReview;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewService {
    String prefix = "reviews";

    @POST(prefix)
    Call<CreateReview> createReview(@Body CreateReview createReview);

    @GET(prefix + "/user/{userId}/solution/{solutionId}")
    Call<Boolean> isSolutionReviewedByUser(@Path("userId") Long userId,
                                           @Path("solutionId") Long solutionId);
}
