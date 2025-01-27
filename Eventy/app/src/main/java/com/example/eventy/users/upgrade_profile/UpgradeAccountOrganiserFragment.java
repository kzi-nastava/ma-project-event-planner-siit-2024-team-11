package com.example.eventy.users.upgrade_profile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.custom.LoadingDialog;
import com.example.eventy.custom.ValidOkDialog;
import com.example.eventy.databinding.FragmentUserFastRegistrationUpgradeAccountOrganiserBinding;
import com.example.eventy.users.model.UpgradeProfile;
import com.example.eventy.users.model.User;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpgradeAccountOrganiserFragment extends Fragment {
    private FragmentUserFastRegistrationUpgradeAccountOrganiserBinding binding;
    private ActivityResultLauncher<Intent> galleryPickerLauncher;
    private String profilePicture = PictureHelperService.defaultOrganizerProfilePicture;
    private User currentUser;
    private Boolean isUpgradeProfileCreating = false;

    public UpgradeAccountOrganiserFragment() {}

    public UpgradeAccountOrganiserFragment(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationUpgradeAccountOrganiserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.profilePictureContainer.setClipToOutline(true);
        galleryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(), data.getData());
                            binding.profilePicture.setImageBitmap(bitmap);
                            this.profilePicture = PictureHelperService.bitmapToBase64(bitmap);
                        }
                        catch (Exception e) {
                            showErrorDialog("Error while selecting the picture!");
                        }
                    }
                }
            }
        );
        binding.profilePictureContainer.setOnClickListener(v -> openGalleryPicker());
        binding.changePicture.setOnClickListener(v -> openGalleryPicker());

        populateInputsWithExistingValues();

        addValidation(binding.firstNameInputLayout, binding.firstNameInput, this::validateRequired);
        addValidation(binding.lastNameInputLayout, binding.lastNameInput, this::validateRequired);

        binding.registerButton.setOnClickListener(v -> {
            binding.firstNameInput.setText(binding.firstNameInput.getText());
            binding.lastNameInput.setText(binding.lastNameInput.getText());

            if (binding.firstNameInputLayout.getError() == null &&
                binding.lastNameInputLayout.getError() == null &&
                currentUser != null) {

                UpgradeProfile upgradeProfileData = new UpgradeProfile();
                upgradeProfileData.setEmail(currentUser.getEmail());
                upgradeProfileData.setAccountType("EVENT ORGANIZER");
                upgradeProfileData.setFirstName(String.valueOf(binding.firstNameInput.getText()));
                upgradeProfileData.setLastName(String.valueOf(binding.lastNameInput.getText()));
                upgradeProfileData.setProfilePictures(Arrays.asList(this.profilePicture));

                createUpgradeProfileRequest(upgradeProfileData, this.getContext(), container);

            } else {
                showErrorDialog("Please ensure all fields are filled with valid values.");
            }

            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        });

        return root;
    }

    private void populateInputsWithExistingValues() {
        String email = currentUser.getEmail();
        binding.emailInput.setText(email);

        String address = currentUser.getAddress();
        binding.addressInput.setText(address);

        String phoneNumber = currentUser.getPhoneNumber();
        binding.phoneNumberInput.setText(phoneNumber);
    }

    private void createUpgradeProfileRequest(UpgradeProfile upgradeProfileData, Context context, ViewGroup container) {
        if (isUpgradeProfileCreating) {
            return;
        }
        isUpgradeProfileCreating = true;

        Call<ResponseBody> call = ClientUtils.authService.upgradeProfile(upgradeProfileData);
        LoadingDialog loadingDialog = new LoadingDialog(getActivity(),
                "Loading",
                "We are currently processing your request. Please wait a few seconds!");
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    if (isAdded() && getActivity() != null) {
                        loadingDialog.cancel();

                        Toast.makeText(context, upgradeProfileData.toString(), Toast.LENGTH_LONG).show();

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
                            showErrorDialog("An unknown error occurred while creating a upgrade profile request!");
                        }
                    } catch (IOException e) {
                        showErrorDialog("An unknown error occurred while creating a upgrade profile request");
                    }
                }
                isUpgradeProfileCreating = false;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialog.cancel();

                showErrorDialog(t.getMessage());
                isUpgradeProfileCreating = false;
            }
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryPickerLauncher.launch(intent);
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
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
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
            textInputLayout.setErrorEnabled(true);
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }
}

