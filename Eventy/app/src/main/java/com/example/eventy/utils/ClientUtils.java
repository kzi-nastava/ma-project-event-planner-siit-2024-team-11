package com.example.eventy.utils;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import com.example.eventy.BuildConfig;
import com.example.eventy.events.services.EventService;
import com.example.eventy.events.services.EventTypeService;
import com.example.eventy.events.services.LocationService;
import com.example.eventy.solutions.services.SolutionService;
import com.example.eventy.users.services.AuthService;
import com.example.eventy.users.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ClientUtils {

    //EXAMPLE: http://192.168.43.73:8080/api/
    public static final String SERVICE_API_PATH = "http://"+ BuildConfig.IP_ADDR +":8080/api/";

    private static Context appContext; // Store the application context

    public static void init(Context context) {
        appContext = context.getApplicationContext(); // Store application context

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVICE_API_PATH)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client())
                .build();

        authService = retrofit.create(AuthService.class);
        eventTypeService = retrofit.create(EventTypeService.class);
        eventService = retrofit.create(EventService.class);
        userService = retrofit.create(UserService.class);
        locationService = retrofit.create(LocationService.class);
        solutionService = retrofit.create(SolutionService.class);
    }

    /*
     * Ovo ce nam sluziti za debug, da vidimo da li zahtevi i odgovori idu
     * odnosno dolaze i kako izgeldaju.
     * */
    public static OkHttpClient client(){
        AuthInterceptor interceptor = new AuthInterceptor(appContext);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        return client;
    }

    /*
     * Prvo je potrebno da definisemo retrofit instancu preko koje ce komunikacija ici
     * */
    public static Retrofit retrofit;

    /*
     * Definisemo konkretnu instancu servisa na intnerntu sa kojim
     * vrsimo komunikaciju
     * */

    public static AuthService authService;
    public static EventTypeService eventTypeService;
    public static EventService eventService;
    public static UserService userService;
    public static LocationService locationService;
    public static SolutionService solutionService;
}
