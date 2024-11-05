package com.example.eventy.register;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.databinding.FragmentRegisterOrganiserBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class RegisterOrganiserFragment extends Fragment {
    private FragmentRegisterOrganiserBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterOrganiserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Register the ActivityResultLauncher for picking images
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            binding.profilePicture.setImageURI(imageUri);
                        }
                    }
                }
        );

        binding.profilePicture.setOnClickListener(v -> {
            openImagePicker(); // Directly open the image picker
        });

        addValidation(binding.emailInputLayout, binding.emailInput, this::validateEmail);
        addValidation(binding.passwordInputLayout, binding.passwordInput, this::validateRequired);
        addValidation(binding.confirmPasswordInputLayout, binding.confirmPasswordInput, this::validateConfirmPassword);
        addValidation(binding.firstNameInputLayout, binding.firstNameInput, this::validateRequired);
        addValidation(binding.lastNameInputLayout, binding.lastNameInput, this::validateRequired);
        addValidation(binding.addressInputLayout, binding.addressInput, this::validateRequired);
        addValidation(binding.phoneNumberInputLayout, binding.phoneNumberInput, this::validatePhoneNumber);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Use ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Specify the type of files to pick
        imagePickerLauncher.launch(intent); // Launch the image picker
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        // real time field validation
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input as the user types
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                validateRequired(String.valueOf(textInputEditText.getText()), textInputLayout);
            }
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required"); // Set error message
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }

    private void validateEmail(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputText).matches()) {
            textInputLayout.setError("Invalid email format");
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }

    private void validatePhoneNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!Patterns.PHONE.matcher(inputText).matches()) {
            textInputLayout.setError("Invalid phone number format");
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }
    
    private void validateConfirmPassword(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (binding.passwordInput.getText() != binding.confirmPasswordInput.getText()) {
            textInputLayout.setError("Passwords don't match!");
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }
}