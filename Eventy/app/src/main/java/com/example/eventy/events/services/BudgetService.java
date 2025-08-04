package com.example.eventy.events.services;

import com.example.eventy.events.model.Budget;
import com.example.eventy.events.model.BudgetItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetService {
    String prefix = "budget";

    @GET(prefix + "/{eventId}")
    Call<Budget> getBudget(@Path("eventId") Long eventId);

    @POST(prefix + "/{eventId}/item/{categoryId}")
    Call<BudgetItem> createBudgetItem(@Path("eventId") Long eventId,
                                      @Path("categoryId") Long categoryId,
                                      @Body Double allocatedFunds);

    @DELETE(prefix + "/{eventId}/item/{budgetItemId}")
    Call<Boolean> removeBudgetItem(@Path("eventId") Long eventId,
                                   @Path("budgetItemId") Long budgetItemId);

    @PUT(prefix + "/item/{budgetItemId}")
    Call<BudgetItem> updateBudgetItemFunds(@Path("budgetItemId") Long budgetItemId,
                                           @Body Double allocatedFunds);

    @DELETE(prefix + "/item/{budgetItemId}/solution/{solutionHistoryId}")
    Call<Boolean> removeBudgetItemSolution(@Path("budgetItemId") Long budgetItemId,
                                           @Path("solutionHistoryId") Long solutionHistoryId);
}
