package com.example.eventy.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentRegisterOrganiserBinding;
import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.model.LoginData;
import com.example.eventy.users.model.RegisterData;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterOrganiserFragment extends Fragment {
    private FragmentRegisterOrganiserBinding binding;
    private ActivityResultLauncher<Intent> galleryPickerLauncher;
    private String profilePictureUri =
            "/Users/rogan003/Desktop/Fakultet/5. semestar/Inzenjerstvo klijentskog sloja/Projekat/iks-project-event-planner-siit-2024-team-11/Eventy/public/ProfilePicture.png";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterOrganiserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            binding.profilePicture.setImageURI(imageUri);
                            this.profilePictureUri = imageUri.toString();
                        }
                    }
                }
        );

        binding.profilePicture.setOnClickListener(v -> openGalleryPicker());

        addValidation(binding.emailInputLayout, binding.emailInput, this::validateEmail);
        addValidation(binding.passwordInputLayout, binding.passwordInput, this::validateRequired);
        addValidation(binding.confirmPasswordInputLayout, binding.confirmPasswordInput, this::validateConfirmPassword);
        addValidation(binding.firstNameInputLayout, binding.firstNameInput, this::validateRequired);
        addValidation(binding.lastNameInputLayout, binding.lastNameInput, this::validateRequired);
        addValidation(binding.addressInputLayout, binding.addressInput, this::validateRequired);
        addValidation(binding.phoneNumberInputLayout, binding.phoneNumberInput, this::validatePhoneNumber);

        binding.registerButton.setOnClickListener(v -> {
            binding.emailInput.setText(binding.emailInput.getText());
            binding.passwordInput.setText(binding.passwordInput.getText());
            binding.confirmPasswordInput.setText(binding.confirmPasswordInput.getText());
            binding.firstNameInput.setText(binding.firstNameInput.getText());
            binding.lastNameInput.setText(binding.lastNameInput.getText());
            binding.addressInput.setText(binding.addressInput.getText());
            binding.phoneNumberInput.setText(binding.phoneNumberInput.getText());

            if(binding.emailInputLayout.getError() == null &&
                    binding.passwordInputLayout.getError() == null &&
                binding.confirmPasswordInputLayout.getError() == null &&
                binding.firstNameInputLayout.getError() == null &&
                binding.lastNameInputLayout.getError() == null &&
                binding.addressInputLayout.getError() == null &&
                binding.phoneNumberInputLayout.getError() == null) {

                Call<String> call = ClientUtils.authService.register(
                        new RegisterData(new ArrayList<String>(Arrays.asList(this.profilePictureUri)),
                                binding.emailInput.getText().toString(),
                                binding.passwordInput.getText().toString(),
                                binding.confirmPasswordInput.getText().toString(),
                                binding.firstNameInput.getText().toString(),
                                binding.lastNameInput.getText().toString(),
                                null,
                                null,
                                binding.addressInput.getText().toString(),
                                binding.phoneNumberInput.getText().toString()));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Confirmation email sent")
                                    .setMessage("Please check your email and confirm the registration!")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setIcon(R.drawable.icon_info)
                                    .show();

                            NavController navController = Navigation.findNavController(v);

                            navController.popBackStack();

                            navController.navigate(R.id.nav_home);
                        } else {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Invalid input")
                                    .setMessage("Invalid input data!")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setIcon(R.drawable.icon_error)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Invalid input")
                                .setMessage("Invalid input data!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }
                });
            } else {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Invalid input")
                        .setMessage("Invalid input data!")
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

    private void openGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryPickerLauncher.launch(intent);
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                action.accept(s.toString(), textInputLayout);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void validateEmail(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputText).matches()) {
            textInputLayout.setError("Invalid email format");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void validatePhoneNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!Patterns.PHONE.matcher(inputText).matches()) {
            textInputLayout.setError("Invalid phone number format");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void validateConfirmPassword(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!binding.passwordInput.getText().toString().equals(binding.confirmPasswordInput.getText().toString())) {
            textInputLayout.setError("Passwords don't match!");
        } else {
            textInputLayout.setError(null);
        }
    }
}