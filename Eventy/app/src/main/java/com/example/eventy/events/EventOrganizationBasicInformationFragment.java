package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.eventy.databinding.FragmentEventOrganizationBasicInformationBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

        MaterialAutoCompleteTextView eventTypeAutoCompleteTextView = binding.eventTypeAutoCompleteTextView;
        String[] eventTypes = {"Wedding", "Graduation", "Conference"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes);
        eventTypeAutoCompleteTextView.setAdapter(adapter);

        // Ensure the dropdown appears when the view is tapped
        eventTypeAutoCompleteTextView.setOnClickListener(v -> {
            if (!eventTypeAutoCompleteTextView.isPopupShowing()) {
                eventTypeAutoCompleteTextView.showDropDown();
            }
        });

        binding.dateRangeInput.setOnClickListener(v -> showDateRangePicker());

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

    public void showDateRangePicker() {
        // Configure date range picker constraints
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()); // Optional: Limits to future dates only

        // Build the date range picker
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        builder.setCalendarConstraints(constraintsBuilder.build());

        // Create the picker
        final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        // Show the picker
        dateRangePicker.show(getParentFragmentManager(), "date_range_picker");

        dateRangePicker.addOnNegativeButtonClickListener(selection -> {
            if(!binding.dateRangeInput.getText().toString().contains("-")) {
                binding.dateRangeInputLayout.setError("You have to enter full range!");
            }
        });

        // Handle the result when dates are selected
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Check that the selection is not null
            if (startDate != null && endDate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedStart = formatter.format(new Date(startDate));
                String formattedEnd = formatter.format(new Date(endDate));

                // Display the selected date range in the TextInputEditText
                binding.dateRangeInput.setText(formattedStart + " - " + formattedEnd);
                binding.dateRangeInputLayout.setError(null);
            }
            else {
                if(!binding.dateRangeInput.getText().toString().contains("-")) {
                    binding.dateRangeInputLayout.setError("You have to enter full range!");
                }
            }
        });
    }
}