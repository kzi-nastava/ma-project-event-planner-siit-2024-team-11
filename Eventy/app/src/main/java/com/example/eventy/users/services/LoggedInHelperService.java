package com.example.eventy.users.services;

import android.content.Context;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class LoggedInHelperService {
    private static Context context;

    public static void init(Context context) {
        LoggedInHelperService.context = context;
    }

    public static String getRole() {
        if(LoggedInHelperService.isLoggedIn()) {
            String jwtToken = context.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);

            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("role").asString();
        }

        return null;
    }

    public static Long getId() {
        if(LoggedInHelperService.isLoggedIn()) {
            String jwtToken = context.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);

            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("id").asLong();
        }

        return null;
    }

    public static boolean isLoggedIn() {
        String jwtToken = context.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        return jwtToken != null;
    }
}
