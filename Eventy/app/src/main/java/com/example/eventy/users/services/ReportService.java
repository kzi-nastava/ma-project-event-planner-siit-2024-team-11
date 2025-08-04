package com.example.eventy.users.services;

import com.example.eventy.common.PagedResponse;
import com.example.eventy.users.model.CreateReport;
import com.example.eventy.users.model.Report;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {
    String prefix = "reports";

    @POST(prefix)
    Call<CreateReport> createReport(@Body CreateReport createReport);

    @GET(prefix + "/pending")
    Call<PagedResponse<Report>> getPendingReports(@Query("page") int page,
                                                  @Query("size") int pageSize);

    @PUT(prefix + "/{reportId}/accept")
    Call<Report> acceptReport(@Path("reportId") Long reportId);

    @PUT(prefix + "/{reportId}/decline")
    Call<Report> declineReport(@Path("reportId") Long reportId);
}
