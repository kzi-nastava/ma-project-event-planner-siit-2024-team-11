package com.example.eventy.users.fast_registration;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.example.eventy.custom.ValidOkDialog;
import com.example.eventy.databinding.FragmentUserFastRegistrationBinding;
import com.example.eventy.services.model.Reservation;
import com.example.eventy.users.model.FastRegistration;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FastRegistrationFragment extends Fragment {
    private FragmentUserFastRegistrationBinding binding;
    private Boolean isFastRegistrationCreating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String email;
        String encryptedEmail;

        Bundle arguments = getArguments();
        if (arguments != null) {
            email = arguments.getString("email");
            encryptedEmail = arguments.getString("encryptedEmail");
        } else {
            encryptedEmail = "";
            email = "anonymous user";
            showErrorDialog("No email provided. Please try again.");
        }

        binding.welcomeEmailText.setText("‣  " + email + "!");
        binding.emailInput.setText(email);
        binding.emailInput.setEnabled(false);

        addValidation(binding.passwordInputLayout, binding.passwordInput, this::validateRequired);
        addValidation(binding.confirmPasswordInputLayout, binding.confirmPasswordInput, this::validateConfirmPassword);
        addValidation(binding.addressInputLayout, binding.addressInput, this::validateRequired);
        addValidation(binding.phoneNumberInputLayout, binding.phoneNumberInput, this::validatePhoneNumber);

        binding.registerButton.setOnClickListener(v -> {
            binding.passwordInput.setText(binding.passwordInput.getText());
            binding.confirmPasswordInput.setText(binding.confirmPasswordInput.getText());
            binding.addressInput.setText(binding.addressInput.getText());
            binding.phoneNumberInput.setText(binding.phoneNumberInput.getText());

            if (binding.passwordInputLayout.getError() == null &&
                binding.confirmPasswordInputLayout.getError() == null &&
                binding.addressInputLayout.getError() == null &&
                binding.phoneNumberInputLayout.getError() == null &&
                !Objects.equals(encryptedEmail, "")) {

                FastRegistration fastRegistrationData = new FastRegistration();
                fastRegistrationData.setEncryptedEmail(encryptedEmail);
                fastRegistrationData.setPassword(String.valueOf(binding.passwordInput.getText()));
                fastRegistrationData.setConfirmedPassword(String.valueOf(binding.confirmPasswordInput.getText()));
                fastRegistrationData.setAddress(String.valueOf(binding.addressInput.getText()));
                fastRegistrationData.setPhoneNumber(String.valueOf(binding.phoneNumberInput.getText()));

                createFastRegistrationRequest(fastRegistrationData, this.getContext(), container);

            } else {
                showErrorDialog("Please ensure all fields are filled with valid values.");
            }
        });

        return root;
    }

    private void createFastRegistrationRequest(FastRegistration fastRegistrationData, Context context, ViewGroup container) {
        if (isFastRegistrationCreating) {
            return;
        }
        isFastRegistrationCreating = true;

        Call<ResponseBody> call = ClientUtils.authService.fastRegister(fastRegistrationData);
        ValidOkDialog loadingDialog = new ValidOkDialog(getActivity(),
                "Loading",
                "Please wait a few seconds!");
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    if (isAdded() && getActivity() != null) {
                        loadingDialog.cancel();

                        Toast.makeText(context, fastRegistrationData.toString(), Toast.LENGTH_LONG).show();

                        ValidOkDialog validOkDialog = new ValidOkDialog(getActivity(),
                                "Email Confirmation Needed",
                                "Confirmation email sent to the email address!");
                        validOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        validOkDialog.setOnDismissListener(dialog -> {
                            // open Gmail or email client
                            Intent emailIntent = new Intent(Intent.ACTION_MAIN);
                            emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                getActivity().startActivity(emailIntent);
                            } catch (ActivityNotFoundException e) {
                                NavController navController = Navigation.findNavController(container);
                                navController.popBackStack();
                                navController.navigate(R.id.nav_home);
                            }
                        });
                        validOkDialog.show();
                    }
                } else {
                    loadingDialog.cancel();

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            String errorMessage = errorBody.replaceAll("[\\[\\]\"]", "");
                            String reason = errorMessage.split(":")[1].trim();

                            showErrorDialog("Error: " + reason);
                        } else {
                            showErrorDialog("An unknown error occurred while creating a registration request!");
                        }
                    } catch (IOException e) {
                        showErrorDialog("An unknown error occurred while creating a registration request");
                    }
                }
                isFastRegistrationCreating = false;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialog.cancel();

                showErrorDialog(t.getMessage());
                isFastRegistrationCreating = false;
            }
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Creation Failed", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
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

