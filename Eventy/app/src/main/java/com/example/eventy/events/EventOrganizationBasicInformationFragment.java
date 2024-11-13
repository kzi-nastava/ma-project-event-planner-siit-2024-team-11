package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.example.eventy.databinding.FragmentEventOrganizationBasicInformationBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class EventOrganizationBasicInformationFragment extends Fragment {
    private FragmentEventOrganizationBasicInformationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventOrganizationBasicInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.maxParticipantsInputLayout, binding.maxParticipantsInput, this::validateNumber);

        binding.radioPublic.setOnClickListener(v -> {
            binding.radioPrivate.setChecked(false);
            binding.radioPublic.setChecked(true);
        });

        binding.radioPrivate.setOnClickListener(v -> {
            binding.radioPublic.setChecked(false);
            binding.radioPrivate.setChecked(true);
        });

        Spinner spinner = binding.eventTypeSpinner;
        String[] options = {"Wedding", "Graduation", "Conference"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection
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
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required"); // Set error message
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }

    private void validateNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required"); // Set error message
        }
        else if(!inputText.matches("^[1-9][0-9]*$")) {
            textInputLayout.setError("The value should be a positive integer"); // Set error message
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }
}