package com.example.eventy.reviews.service;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.reviews.model.CreateReview;
import com.example.eventy.reviews.model.Review;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewService {
    String prefix = "reviews";

    @POST(prefix)
    Call<CreateReview> createReview(@Body CreateReview createReview);

    @GET(prefix + "/user/{userId}/solution/{solutionId}")
    Call<Boolean> isSolutionReviewedByUser(@Path("userId") Long userId,
                                           @Path("solutionId") Long solutionId);

    @GET(prefix + "/pending")
    Call<PagedResponse<Review>> getPendingReviews(@Query("page") int page,
                                                  @Query("size") int pageSize);

    @PUT(prefix + "/{reviewId}/accept")
    Call<Review> acceptReview(@Path("reviewId") Long reviewId);

    @PUT(prefix + "/{reviewId}/decline")
    Call<Review> declineReview(@Path("reviewId") Long reviewId);
}
