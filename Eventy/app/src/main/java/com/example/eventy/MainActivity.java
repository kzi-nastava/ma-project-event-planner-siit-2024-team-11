package com.example.eventy;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.eventy.databinding.ActivityMainBinding;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ClientUtils.init(getApplicationContext());
        LoggedInHelperService.init(getApplicationContext());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login, R.id.nav_register, R.id.nav_add_service, R.id.nav_edit_service,
                R.id.nav_event_organization, R.id.nav_own_services_test, R.id.nav_event_types, R.id.nav_add_event_type,
                R.id.nav_edit_event_type, R.id.nav_event_type_details, R.id.nav_other_user_profile_page,
                R.id.nav_edit_user, R.id.nav_my_profile, R.id.service_reservation, R.id.fast_registration,
                R.id.upgrade_profile, R.id.nav_category_management)
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

        manageNavigationDrawerItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

            manageNavigationDrawerItems();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            navController.popBackStack();

            navController.navigate(R.id.nav_my_profile);
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

    public void manageNavigationDrawerItems() {
        // Get the role from your role-determination function
        String role = LoggedInHelperService.getRole();

        // Get the Menu from the NavigationView
        Menu menu = binding.navView.getMenu();

        menu.findItem(R.id.action_profile).setVisible(false);
        menu.findItem(R.id.action_messages).setVisible(false);
        menu.findItem(R.id.action_notifications).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
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

        if (role != null) {
            menu.findItem(R.id.action_profile).setVisible(true);
            menu.findItem(R.id.action_messages).setVisible(true);
            menu.findItem(R.id.action_notifications).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(true);
        }

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

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("JWT_TOKEN");
        editor.apply();
    }
}