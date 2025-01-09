package com.example.eventy.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentLoginBinding;
import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.model.LoginData;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.registerHereButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_register);
        });

        binding.loginButton.setOnClickListener(v -> {
            Call<AuthResponse> call = ClientUtils.authService.login(
                    new LoginData(binding.emailInput.getText().toString(),
                            binding.passwordInput.getText().toString()));
            call.enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("JWT_TOKEN", response.body().getAccessToken());
                        editor.apply();

                        LoggedInHelperService.manageNavigationItems();

                        NavController navController = Navigation.findNavController(v);

                        navController.popBackStack();

                        navController.navigate(R.id.nav_home);
                    } else {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Invalid input")
                                .setMessage("Email and password don't match!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Invalid input")
                            .setMessage("Email and password don't match!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.icon_error)
                            .show();
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}