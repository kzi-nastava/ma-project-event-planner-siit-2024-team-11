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
        String jwtToken = context.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if(jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("role").asString();
        }

        return null;
    }

    public static Long getId() {
        String jwtToken = context.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if(jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("id").asLong();
        }

        return null;
    }
}
