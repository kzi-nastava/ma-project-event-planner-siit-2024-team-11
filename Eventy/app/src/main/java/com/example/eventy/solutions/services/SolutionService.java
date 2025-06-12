package com.example.eventy.solutions.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.solutions.model.PricelistItem;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.solutions.model.SolutionDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @GET(prefix + "/cards/{solutionId}")
    Call<SolutionCard> getSolution(@Path("solutionId") Long solutionId);

    @PUT(prefix + "/favorite/{solutionId}")
    Call<Boolean> toggleFavorite(@Path("solutionId") Long solutionId);

    @GET(prefix + "/{solutionId}")
    Call<SolutionDetails> getSolutionDetails(@Path("solutionId") Long solutionId);

    @PUT(prefix + "/{solutionId}/visibility")
    Call<Void> toggleVisibility(@Path("solutionId") Long solutionId);

    @PUT(prefix + "/{solutionId}/availability")
    Call<Void> toggleAvailability(@Path("solutionId") Long solutionId);

    @DELETE(prefix + "/{solutionId}")
    Call<Void> delete(@Path("solutionId") Long solutionId);

    @GET(prefix + "/pricelist")
    Call<PagedResponse<PricelistItem>> getPricelist(@Query("page") int page,
                                                    @Query("size") int pageSize);

    @PUT(prefix + "/pricelist")
    Call<PricelistItem> updatePrice(@Body PricelistItem updatedItem);
}
