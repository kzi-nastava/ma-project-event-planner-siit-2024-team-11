package com.example.eventy.users.upgrade_profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserFastRegistrationUpgradeAccountBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeAccountFragment extends Fragment {
    private FragmentUserFastRegistrationUpgradeAccountBinding binding;
    private boolean isOrganiser = true;
    private User currentUser;

    public UpgradeAccountFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationUpgradeAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Call<User> call = ClientUtils.userService.get(LoggedInHelperService.getId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();

                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Unable to load the currently logged-in user!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.setOnDismissListener(dialog -> {
                        NavController navController = Navigation.findNavController(container);
                        navController.popBackStack();
                        navController.navigate(R.id.nav_home);
                    });
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Unable to load the currently logged-in user!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.setOnDismissListener(dialog -> {
                    NavController navController = Navigation.findNavController(container);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_home);
                });
                errorOkDialog.show();
            }
        });

        if (currentUser != null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UpgradeAccountOrganiserFragment(currentUser))
                    .commit();

            Button switchButton = binding.switchFragmentButton;
            switchButton.setOnClickListener(v -> {
                Fragment fragment = isOrganiser ? new UpgradeAccountProviderFragment(currentUser) : new UpgradeAccountOrganiserFragment(currentUser);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                isOrganiser = !isOrganiser;
                binding.registerLabel.setText(isOrganiser ? "Event Organiser" : "Solution Provider");
            });
        }

        binding.loginHereButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.nav_login);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

