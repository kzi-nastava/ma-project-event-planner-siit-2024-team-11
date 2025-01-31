package com.example.eventy.solutions.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SolutionCategoryService {
    String prefix = "categories";

    @GET(prefix)
    Call<List<CategoryWithID>> getActiveCategories();

    @GET(prefix + "/paged")
    Call<PagedResponse<CategoryWithID>> getActiveCategoriesPaged(@Query("page") int pageIndex,
                                                                 @Query("size") int pageSize);

    @POST(prefix)
    Call<CategoryWithID> createCategory(@Body Category category);

    @GET(prefix + "/requests")
    Call<PagedResponse<CategoryWithID>> getRequestsPaged(@Query("page") int pageIndex,
                                                         @Query("size") int pageSize);

    @PUT(prefix)
    Call<CategoryWithID> updateCategory(@Body CategoryWithID category);

    @PUT(prefix + "/requests/accept/{requestId}")
    Call<CategoryWithID> acceptRequest(@Path("requestId") Long requestId);

    @PUT(prefix + "/requests/change")
    Call<CategoryWithID> changeRequest(@Body CategoryWithID changedCategory);

    @PUT(prefix + "/requests/replace")
    Call<CategoryWithID> replaceRequest(@Query("replacedCategoryId") Long replacedCategoryId,
                                        @Query("newlyUsedCategoryId") Long newlyUsedCategoryId);

    @DELETE(prefix + "/{categoryId}")
    Call<Void> deleteCategory(@Path("categoryId") Long categoryId);

}
