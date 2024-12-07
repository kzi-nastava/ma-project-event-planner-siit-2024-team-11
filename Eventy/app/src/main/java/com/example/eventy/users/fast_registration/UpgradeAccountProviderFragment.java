package com.example.eventy.users.fast_registration;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentUserFastRegistrationUpgradeAccountProviderBinding;
import com.example.eventy.register.CarouselAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class UpgradeAccountProviderFragment extends Fragment {
    private FragmentUserFastRegistrationUpgradeAccountProviderBinding binding;
    private CarouselAdapter carouselAdapter;
    private List<Uri> images = Arrays.asList(
            Uri.parse("android.resource://com.example.eventy/" + R.drawable.solution_provider_profile_picture)
    );
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationUpgradeAccountProviderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ViewPager2 viewPager = binding.viewPager;
        carouselAdapter = new CarouselAdapter(images);
        viewPager.setAdapter(carouselAdapter);

        binding.profilePictureContainer.setClipToOutline(true);
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                List<Uri> newImages = new ArrayList<>();
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        ClipData clipData = result.getData().getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            newImages.add(imageUri);
                        }
                    } else if (result.getData().getData() != null) {
                        Uri imageUri = result.getData().getData();
                        newImages.add(imageUri);
                    }

                    carouselAdapter.updateImages(newImages);
                    images = newImages;
                }
            }
        );

        binding.addPhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });

        String email = "example@gmail.com";
        String address = "Najblizi zbun za hibernaciju";
        String phoneNumber = "123456";

        binding.emailInput.setText(email);
        binding.addressInput.setText(address);
        binding.phoneNumberInput.setText(phoneNumber);

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);

        binding.registerButton.setOnClickListener(v -> {
            binding.nameInput.setText(binding.nameInput.getText());
            binding.descriptionInput.setText(binding.descriptionInput.getText());

            if(binding.nameInputLayout.getError() == null &&
               binding.descriptionInputLayout.getError() == null) {
                new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("UPGRADED!")
                    .setMessage("You are upgraded to provider!")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.icon_error)
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
