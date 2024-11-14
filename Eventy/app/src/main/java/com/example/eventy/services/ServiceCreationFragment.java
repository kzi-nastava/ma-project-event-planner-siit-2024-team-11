package com.example.eventy.services;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eventy.R;
import com.example.eventy.databinding.FragmentServiceCreationBinding;
import com.example.eventy.register.CarouselAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceCreationFragment extends Fragment {

    private CarouselAdapter carouselAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private List<Uri> images = Arrays.asList(
            Uri.parse("android.resource://com.example.eventy/" + R.mipmap.logo)
    );
    private FragmentServiceCreationBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceCreationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.time_duration_fragment_container, new ServiceDurationFragment())
                .commit();

        ViewPager2 viewPager = binding.servicePhotos;

        carouselAdapter = new CarouselAdapter(images);
        viewPager.setAdapter(carouselAdapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    List<Uri> newImages = new ArrayList<>();
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            // Multiple images selected
                            ClipData clipData = result.getData().getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                newImages.add(imageUri);
                            }
                        }

                        carouselAdapter.updateImages(newImages);
                        images = newImages;
                    }
                }
        );

        binding.addServicePhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Allow multiple selection
            imagePickerLauncher.launch(intent);
        });

        return root;
    }
}