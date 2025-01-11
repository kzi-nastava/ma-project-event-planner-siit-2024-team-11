package com.example.eventy.users.services;

import android.content.Context;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventy.R;
import com.google.android.material.navigation.NavigationView;

public class LoggedInHelperService {
    private static Context appContext;
    private static NavigationView navigationView;
    private static AppCompatActivity mainActivity;

    public static void init(Context context, NavigationView navigationView, AppCompatActivity mainActivity) {
        LoggedInHelperService.appContext = context;
        LoggedInHelperService.navigationView = navigationView;
        LoggedInHelperService.mainActivity = mainActivity;
    }

    public static String getRole() {
        String jwtToken = appContext.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if(jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("role").asString();
        }

        return null;
    }

    public static Long getId() {
        String jwtToken = appContext.getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if(jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            return decodedJWT.getClaim("id").asLong();
        }

        return null;
    }

    public static void manageNavigationItems() {
        LoggedInHelperService.mainActivity.supportInvalidateOptionsMenu();;

        String role = LoggedInHelperService.getRole();

        Menu menu = LoggedInHelperService.navigationView.getMenu();

        menu.findItem(R.id.nav_login).setVisible(false);
        menu.findItem(R.id.nav_register).setVisible(false);
        menu.findItem(R.id.nav_add_service).setVisible(false);
        menu.findItem(R.id.nav_edit_service).setVisible(false);
        menu.findItem(R.id.nav_event_organization).setVisible(false);
        menu.findItem(R.id.nav_own_services_test).setVisible(false);
        menu.findItem(R.id.nav_event_types).setVisible(false);
        menu.findItem(R.id.service_reservation).setVisible(false);
        menu.findItem(R.id.fast_registration).setVisible(false);
        menu.findItem(R.id.upgrade_profile).setVisible(false);
        menu.findItem(R.id.nav_category_management).setVisible(false);

        // Example logic: Show/Hide items based on role
        if ("ROLE_Admin".equals(role)) {
            menu.findItem(R.id.nav_event_types).setVisible(true);
            menu.findItem(R.id.nav_category_management).setVisible(true);
        } else if ("ROLE_Organizer".equals(role)){
            menu.findItem(R.id.nav_event_organization).setVisible(true);
            menu.findItem(R.id.service_reservation).setVisible(true);
        } else if ("ROLE_Provider".equals(role)) {
            menu.findItem(R.id.nav_add_service).setVisible(true);
            menu.findItem(R.id.nav_edit_service).setVisible(true);
            menu.findItem(R.id.nav_own_services_test).setVisible(true);
        } else if ("ROLE_AuthenticatedUser".equals(role)) {
            menu.findItem(R.id.upgrade_profile).setVisible(true);
        } else if (role == null) {
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_register).setVisible(true);
            menu.findItem(R.id.fast_registration).setVisible(true);
        }
    }
}
