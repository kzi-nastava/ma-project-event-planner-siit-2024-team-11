package com.example.eventy.users.services;

import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.model.LoginData;
import com.example.eventy.users.model.RegisterData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/*
 * Interfejs koji opisuje putanju servisa.
 * Opisuje koji metod koristimo ali i sta ocekujemo kao rezultat
 * */
public interface AuthService {
    String prefix = "authentication/";

    @POST(prefix + "login")
    Call<AuthResponse> login(@Body LoginData loginData);

    @POST(prefix + "registration")
    Call<ResponseBody> register(@Body RegisterData registerData);

    @PUT(prefix + "registration-confirmation/{requestId}")
    Call<AuthResponse> confirmRegistration(@Path("requestId") Long requestId);
}
