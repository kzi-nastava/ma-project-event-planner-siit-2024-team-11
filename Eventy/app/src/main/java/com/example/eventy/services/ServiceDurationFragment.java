package com.example.eventy.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.databinding.FragmentServiceDurationBinding;
import com.google.android.material.radiobutton.MaterialRadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

public class ServiceDurationFragment extends Fragment {
    private FragmentServiceDurationBinding binding;
    private MaterialRadioButton fixedDurationButton, variableDurationButton;
    private TextInputLayout fixedDuration, minimumDuration, maximumDuration;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceDurationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fixedDurationButton = binding.fixedDurationRadioButton;
        variableDurationButton = binding.variableDurationRadioButton;
        fixedDuration = binding.fixedDurationInputLayout;
        minimumDuration = binding.minimumDurationInputLayout;
        maximumDuration = binding.maximumDurationInputLayout;

        fixedDurationButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fixedDuration.setVisibility(View.VISIBLE);
                minimumDuration.setVisibility(View.GONE);
                maximumDuration.setVisibility(View.GONE);
            }
        });

        variableDurationButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fixedDuration.setVisibility(View.GONE);
                minimumDuration.setVisibility(View.VISIBLE);
                maximumDuration.setVisibility(View.VISIBLE);
            }
        });


        return root;
    }
}
