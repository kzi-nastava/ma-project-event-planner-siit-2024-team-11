package com.example.eventy.utils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the token from SharedPreferences (or EncryptedSharedPreferences)
        String jwtToken = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        // Add the token to the request if it exists
        Request.Builder requestBuilder = chain.request().newBuilder();
        if (jwtToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + jwtToken);
        }

        return chain.proceed(requestBuilder.build());
    }
}
