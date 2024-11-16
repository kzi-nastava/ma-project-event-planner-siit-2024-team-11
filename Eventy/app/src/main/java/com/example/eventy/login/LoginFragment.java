package com.example.eventy.login;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
            if(binding.emailInput.getText().toString().equals("good@email.com") &&
            binding.passwordInput.getText().toString().equals("pass1234")) {
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
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}