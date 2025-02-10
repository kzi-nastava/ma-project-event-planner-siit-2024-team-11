package com.example.eventy.users.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.MainActivity;
import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentLoginBinding;
import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.model.LoginData;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.users.view_model.UserNotificationInfoViewModel;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.registerHereButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack(); // Problem with back button so we clear the backstack
            navController.navigate(R.id.nav_register);
        });

        binding.loginButton.setOnClickListener(v -> {
            Call<AuthResponse> call = ClientUtils.authService.login(
                    new LoginData(binding.emailInput.getText().toString(),
                    binding.passwordInput.getText().toString())
            );
            call.enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("JWT_TOKEN", response.body().getAccessToken());
                        editor.apply();

                        LoggedInHelperService.manageNavigationItems();

                        // for notifications
                        UserNotificationInfoViewModel userNotificationInfoViewModel = new ViewModelProvider(requireActivity()).get(UserNotificationInfoViewModel.class);
                        userNotificationInfoViewModel.setLoggedInUserId(LoggedInHelperService.getId());
                        String token = response.body().getAccessToken();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).connectToMobileWebSocket(token);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("reviewEvents", true);

                        NavController navController = Navigation.findNavController(v);
                        navController.popBackStack();
                        navController.navigate(R.id.nav_home, bundle);

                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Email and password don't match!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Email and password don't match!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
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