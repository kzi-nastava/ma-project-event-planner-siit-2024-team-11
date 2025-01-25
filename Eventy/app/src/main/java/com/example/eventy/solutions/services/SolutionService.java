package com.example.eventy.solutions.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.solutions.model.SolutionCard;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SolutionService {
    String prefix = "solutions";

    @GET(prefix)
    Call<PagedResponse<SolutionCard>> getSolutions(@Query("search") String search,
                                                   @Query("type") String type,
                                                   @Query("categories") ArrayList<String> categories,
                                                   @Query("eventTypes") ArrayList<String> eventTypes,
                                                   @Query("company") String company,
                                                   @Query("minPrice") Double minPrice,
                                                   @Query("maxPrice") Double maxPrice,
                                                   @Query("startDate") LocalDateTime startDate,
                                                   @Query("endDate") LocalDateTime endDate,
                                                   @Query("isAvailable") Boolean isAvailable,
                                                   @Query("page") int page,
                                                   @Query("size") int pageSize,
                                                   @Query("sort") String sort);

    @GET(prefix + "/featured")
    Call<SolutionCard[]> getFeaturedSolutions();

    @GET(prefix + "/event-types")
    Call<String[]> getAllUniqueEventTypesForSolutions();

    @GET(prefix + "/categories")
    Call<String[]> getAllUniqueCategoriesForSolutions();

    @GET(prefix + "/companies")
    Call<String[]> getAllUniqueCompaniesForSolutions();
}
