package com.example.eventy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.NavigationUI;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventy.common.EncryptionUtil;
import com.example.eventy.databinding.ActivityMainBinding;
import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.ZoneId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientUtils.init(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        LoggedInHelperService.init(getApplicationContext(), binding.navView, this);

        String jwtToken = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if(jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            LocalDateTime tokenExpires = decodedJWT.getExpiresAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if(tokenExpires.isBefore(LocalDateTime.now())) {
                this.logout();

                LoggedInHelperService.manageNavigationItems();

                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

                navController.popBackStack();

                navController.navigate(R.id.nav_home);
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login, R.id.nav_register, R.id.nav_add_service, R.id.nav_edit_service,
                R.id.nav_event_organization, R.id.nav_own_services_test, R.id.nav_event_types, R.id.nav_add_event_type,
                R.id.nav_edit_event_type, R.id.nav_event_type_details, R.id.nav_other_user_profile_page,
                R.id.nav_edit_user, R.id.nav_my_profile, R.id.service_reservation, R.id.fast_registration,
                R.id.upgrade_profile, R.id.nav_category_home, R.id.nav_event_details)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // so the title in the navbar doesn't show up
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
            }
        });

        LoggedInHelperService.manageNavigationItems();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null && "confirm-registration".equals(data.getHost())) {
            String id = data.getQueryParameter("id");
            if (id != null) {
                Call<AuthResponse> call = ClientUtils.authService.confirmRegistration(Long.valueOf(id));
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("JWT_TOKEN", response.body().getAccessToken());
                            editor.apply();

                            LoggedInHelperService.manageNavigationItems();
                        } else {
                            new MaterialAlertDialogBuilder(getApplicationContext())
                                    .setTitle("An error occured")
                                    .setMessage("An error occured!")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setIcon(R.drawable.icon_error)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        new MaterialAlertDialogBuilder(getApplicationContext())
                                .setTitle("An error occured")
                                .setMessage("An error occured!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }
                });
            }
        }

        if (data != null && "fast-registration".equals(data.getHost())) {
            String encryptedEmail = data.getQueryParameter("value");
            String email;
            try {
                email = EncryptionUtil.decrypt(encryptedEmail);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Pass the decrypted email to the FastRegistrationFragment
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("encryptedEmail", encryptedEmail);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.fast_registration, bundle);
        }

        if (data != null && "homepage".equals(data.getHost())) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        }

        if (data != null && "event-details".equals(data.getHost())) {
            String eventId = data.getQueryParameter("id");

            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            // DODATI OVDE DA NAVIGIRA U EVENT DETAILS I PROSLIJEDITI eventId U FRAGMENT preko Bundle
            // api/events/{eventId} --> za sad nav_home jer ne postoji event view? (nmg naci?)
            navController.navigate(R.id.nav_home);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        String role = LoggedInHelperService.getRole();
//
//        menu.findItem(R.id.action_profile).setVisible(role != null);
//        menu.findItem(R.id.action_messages).setVisible(role != null);
//        menu.findItem(R.id.action_notifications).setVisible(role != null);
//        menu.findItem(R.id.action_logout).setVisible(role != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            navController.popBackStack();

            navController.navigate(R.id.nav_my_profile);
            return true;
        } else if (id == R.id.action_messages) {

            return true;
        } else if (id == R.id.action_notifications) {

            return true;
        } else if(id == R.id.action_logout) {
            this.logout();

            LoggedInHelperService.manageNavigationItems();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            navController.popBackStack();

            navController.navigate(R.id.nav_home);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("JWT_TOKEN");
        editor.apply();
    }
}