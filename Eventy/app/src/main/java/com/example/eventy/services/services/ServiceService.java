package com.example.eventy.services.services;

import com.example.eventy.solutions.model.SolutionCard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceService {
    String prefix = "services";

    @GET(prefix + "/cards/{serviceId}")
    Call<SolutionCard> getService(@Path("serviceId") Long serviceId);
}
