package com.example.eventy.users.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserOrganizerEditBinding;
import com.example.eventy.users.model.UpdateUser;
import com.example.eventy.users.model.User;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrganizerEditFragment extends Fragment {

    private FragmentUserOrganizerEditBinding binding;
    private ActivityResultLauncher<Intent> galleryPickerLauncher;

    private User user;

    private String profilePicture;

    public UserOrganizerEditFragment(User user) {
        this.user = user;
        this.profilePicture = this.user.getProfilePictures().get(0) != null ? this.user.getProfilePictures().get(0) : PictureHelperService.defaultProfilePicture;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserOrganizerEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(this.user.getProfilePictures() != null && !this.user.getProfilePictures().isEmpty()) {
            binding.profilePicture.setImageURI(Uri.parse(this.user.getProfilePictures().get(0)));
        }
        binding.emailInput.setText(this.user.getEmail());
        binding.firstNameInput.setText(this.user.getFirstName());
        binding.lastNameInput.setText(this.user.getLastName());
        binding.addressInput.setText(this.user.getAddress());
        binding.phoneNumberInput.setText(this.user.getPhoneNumber());

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
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while selecting the picture!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }
                    }
                }
        );

        binding.profilePicture.setOnClickListener(v -> openGalleryPicker());

        addValidation(binding.emailInputLayout, binding.emailInput, this::validateEmail);
        addValidation(binding.passwordInputLayout, binding.passwordInput, this::validateRequired);
        addValidation(binding.oldPasswordInputLayout, binding.oldPasswordInput, this::validateRequired);
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

    private void openGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    public void confirmEdit(View v) {
        binding.emailInput.setText(binding.emailInput.getText());
        binding.oldPasswordInput.setText(binding.oldPasswordInput.getText());
        binding.passwordInput.setText(binding.passwordInput.getText());
        binding.confirmPasswordInput.setText(binding.confirmPasswordInput.getText());
        binding.firstNameInput.setText(binding.firstNameInput.getText());
        binding.lastNameInput.setText(binding.lastNameInput.getText());
        binding.addressInput.setText(binding.addressInput.getText());
        binding.phoneNumberInput.setText(binding.phoneNumberInput.getText());

        if(binding.emailInputLayout.getError() == null &&
                binding.oldPasswordInput.getError() == null &&
                binding.passwordInputLayout.getError() == null &&
                binding.confirmPasswordInputLayout.getError() == null &&
                binding.firstNameInputLayout.getError() == null &&
                binding.lastNameInputLayout.getError() == null &&
                binding.addressInputLayout.getError() == null &&
                binding.phoneNumberInputLayout.getError() == null) {

            Call<User> call = ClientUtils.userService.update(new UpdateUser(
                    this.user.getId(),
                    Arrays.asList(this.profilePicture),
                    binding.emailInput.getText().toString(),
                    binding.oldPasswordInput.getText().toString(),
                    binding.passwordInput.getText().toString(),
                    binding.confirmPasswordInput.getText().toString(),
                    binding.firstNameInput.getText().toString(),
                    binding.lastNameInput.getText().toString(),
                    null,
                    null,
                    binding.addressInput.getText().toString(),
                    binding.phoneNumberInput.getText().toString()
            ));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(" Successful edit")
                                .setMessage("User is now edited successfully.")
                                .setIcon(R.drawable.icon_success_png)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // this leads to home (for now), will lead to the event page or user profile
                                        NavController navController = Navigation.findNavController(v);
                                        navController.popBackStack();
                                        navController.navigate(R.id.nav_my_profile);
                                    }})
                                .show();
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while editing user! Please check you data!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while editing user! Please check you data!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        } else {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while editing user! Please check you data!");
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }
}