package com.example.eventy.users.fast_registration;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserFastRegistrationBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class FastRegistrationFragment extends Fragment {
    private FragmentUserFastRegistrationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.emailInput.setText("example@gmail.com");

        addValidation(binding.passwordInputLayout, binding.passwordInput, this::validateRequired);
        addValidation(binding.confirmPasswordInputLayout, binding.confirmPasswordInput, this::validateConfirmPassword);
        addValidation(binding.firstNameInputLayout, binding.firstNameInput, this::validateRequired);
        addValidation(binding.lastNameInputLayout, binding.lastNameInput, this::validateRequired);
        addValidation(binding.addressInputLayout, binding.addressInput, this::validateRequired);
        addValidation(binding.phoneNumberInputLayout, binding.phoneNumberInput, this::validatePhoneNumber);

        binding.registerButton.setOnClickListener(v -> {
            binding.passwordInput.setText(binding.passwordInput.getText());
            binding.confirmPasswordInput.setText(binding.confirmPasswordInput.getText());
            binding.firstNameInput.setText(binding.firstNameInput.getText());
            binding.lastNameInput.setText(binding.lastNameInput.getText());
            binding.addressInput.setText(binding.addressInput.getText());
            binding.phoneNumberInput.setText(binding.phoneNumberInput.getText());

            if (binding.passwordInputLayout.getError() == null &&
                binding.confirmPasswordInputLayout.getError() == null &&
                binding.firstNameInputLayout.getError() == null &&
                binding.lastNameInputLayout.getError() == null &&
                binding.addressInputLayout.getError() == null &&
                binding.phoneNumberInputLayout.getError() == null) {

                NavController navController = Navigation.findNavController(container);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);

                Toast.makeText(this.getContext(), "Registration successful!!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            textInputLayout.setErrorEnabled(true); // show error space
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false); // remove error text extra space
        }
    }

    private void validateConfirmPassword(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!binding.passwordInput.getText().toString().equals(binding.confirmPasswordInput.getText().toString())) {
            textInputLayout.setError("Passwords don't match!");
            textInputLayout.setErrorEnabled(true);
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }

    private void validatePhoneNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else if (!Patterns.PHONE.matcher(inputText).matches()) {
            textInputLayout.setError("Invalid phone number format");
            textInputLayout.setErrorEnabled(true);
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }
}

