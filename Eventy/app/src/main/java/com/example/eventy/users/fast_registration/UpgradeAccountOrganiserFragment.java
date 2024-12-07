package com.example.eventy.users.fast_registration;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentUserFastRegistrationUpgradeAccountOrganiserBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UpgradeAccountOrganiserFragment extends Fragment {
    private FragmentUserFastRegistrationUpgradeAccountOrganiserBinding binding;
    private ActivityResultLauncher<Intent> galleryPickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationUpgradeAccountOrganiserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.profilePictureContainer.setClipToOutline(true);
        galleryPickerLauncher = registerForActivityResult(
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
        binding.profilePictureContainer.setOnClickListener(v -> openGalleryPicker());
        binding.changePicture.setOnClickListener(v -> openGalleryPicker());

        String email = "example@gmail.com";
        String firstName = "Tac";
        String lastName = "Tackovic";
        String address = "Najblizi zbun za hibernaciju";
        String phoneNumber = "123456";

        binding.emailInput.setText(email);
        binding.firstNameInput.setText(firstName);
        binding.lastNameInput.setText(lastName);
        binding.addressInput.setText(address);
        binding.phoneNumberInput.setText(phoneNumber);

        binding.registerButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("UPGRADED!")
                    .setMessage("You are upgraded to organiser!")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.icon_error)
                    .show();

            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
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
}
