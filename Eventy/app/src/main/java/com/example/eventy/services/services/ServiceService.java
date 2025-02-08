package com.example.eventy.services.services;

import com.example.eventy.model.solution.Service;
import com.example.eventy.services.model.CreateService;
import com.example.eventy.services.model.UpdateService;
import com.example.eventy.solutions.model.SolutionCard;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceService {
    String prefix = "services";

    @GET(prefix + "/{serviceId}")
    Call<Service> getService(@Path("serviceId") Long serviceId);

    @GET(prefix + "/cards/{serviceId}")
    Call<SolutionCard> getServiceCard(@Path("serviceId") Long serviceId);

    @PUT(prefix)
    Call<Service> updateService(@Body UpdateService service);

    @POST(prefix)
    Call<Service> createService(@Body CreateService service);
}
