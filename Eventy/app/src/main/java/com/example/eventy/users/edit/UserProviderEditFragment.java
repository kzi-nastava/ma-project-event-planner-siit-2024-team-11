package com.example.eventy.users.edit;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ClipData;
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
import androidx.viewpager2.widget.ViewPager2;

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
import com.example.eventy.databinding.FragmentUserProviderEditBinding;
import com.example.eventy.users.model.UpdateUser;
import com.example.eventy.users.register.CarouselAdapter;
import com.example.eventy.users.model.User;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProviderEditFragment extends Fragment {

    private FragmentUserProviderEditBinding binding;
    private CarouselAdapter carouselAdapter;

    private List<String> images = Arrays.asList(
            PictureHelperService.defaultProfilePicture
    );

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private User user;

    public UserProviderEditFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProviderEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.emailInput.setText(this.user.getEmail());
        binding.nameInput.setText(this.user.getName());
        binding.descriptionInput.setText(this.user.getDescription());
        binding.addressInput.setText(this.user.getAddress());
        binding.phoneNumberInput.setText(this.user.getPhoneNumber());

        addValidation(binding.emailInputLayout, binding.emailInput, this::validateEmail);
        addValidation(binding.oldPasswordInputLayout, binding.oldPasswordInput, this::validateRequired);
        addValidation(binding.confirmPasswordInputLayout, binding.confirmPasswordInput, this::validateConfirmPassword);
        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.addressInputLayout, binding.addressInput, this::validateRequired);
        addValidation(binding.phoneNumberInputLayout, binding.phoneNumberInput, this::validatePhoneNumber);

        ViewPager2 viewPager = binding.viewPager;

        if(this.user.getProfilePictures() != null && !this.user.getProfilePictures().isEmpty()) {
            this.images = this.user.getProfilePictures();
        }

        carouselAdapter = new CarouselAdapter(images);
        viewPager.setAdapter(carouselAdapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    List<String> newImages = new ArrayList<>();
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            ClipData clipData = result.getData().getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                try {
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                            getActivity().getContentResolver(), imageUri);
                                    newImages.add(PictureHelperService.bitmapToBase64(bitmap));
                                }
                                catch (Exception e) {
                                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while selecting the picture!");
                                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    errorOkDialog.show();
                                }
                            }
                        } else if (result.getData().getData() != null) {
                            try {
                                Uri imageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getActivity().getContentResolver(), imageUri);
                                newImages.add(PictureHelperService.bitmapToBase64(bitmap));
                            }
                            catch (Exception e) {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while selecting the picture!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        carouselAdapter.updateImages(newImages);
                        images = newImages;
                    }
                }
        );

        binding.addPhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        // Real-time field validation
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
        if (!binding.passwordInput.getText().toString().equals(binding.confirmPasswordInput.getText().toString())) {
            if (binding.passwordInput.getText().toString().isEmpty()) {
                textInputLayout.setError(null);
                return;
            }

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
        binding.nameInput.setText(binding.nameInput.getText());
        binding.descriptionInput.setText(binding.descriptionInput.getText());
        binding.addressInput.setText(binding.addressInput.getText());
        binding.phoneNumberInput.setText(binding.phoneNumberInput.getText());

        if(binding.emailInputLayout.getError() == null &&
                binding.oldPasswordInputLayout.getError() == null &&
                binding.passwordInputLayout.getError() == null &&
                binding.confirmPasswordInputLayout.getError() == null &&
                binding.nameInputLayout.getError() == null &&
                binding.descriptionInputLayout.getError() == null &&
                binding.addressInputLayout.getError() == null &&
                binding.phoneNumberInputLayout.getError() == null) {
            Call<User> call = ClientUtils.userService.update(new UpdateUser(
                    this.user.getId(),
                    images,
                    binding.emailInput.getText().toString(),
                    binding.oldPasswordInput.getText().toString(),
                    binding.passwordInput.getText().toString(),
                    binding.confirmPasswordInput.getText().toString(),
                    "",
                    "",
                    binding.nameInput.getText().toString(),
                    binding.descriptionInput.getText().toString(),
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