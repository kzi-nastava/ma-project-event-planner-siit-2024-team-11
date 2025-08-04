package com.example.eventy.events.organizeevent;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentAddActivityBinding;
import com.example.eventy.events.model.CreateActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiConsumer;

public class AddActivityFragment extends Fragment {

    private FragmentAddActivityBinding binding;
    private ArrayList<CreateActivity> agenda;

    private LocalDateTime eventDate;

    public AddActivityFragment(ArrayList<CreateActivity> agenda, LocalDateTime eventDate) {
        this.agenda = agenda;
        this.eventDate = eventDate;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddActivityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.locationInputLayout, binding.locationInput, this::validateRequired);

        binding.activityTimeRangeInput.setOnClickListener(v -> showTimeRangePicker());

        binding.addActivityButton.setOnClickListener(v -> {
            binding.nameInput.setText(binding.nameInput.getText());
            binding.descriptionInput.setText(binding.descriptionInput.getText());
            binding.locationInput.setText(binding.locationInput.getText());

            if (binding.nameInputLayout.getError() == null &&
            binding.descriptionInputLayout.getError() == null &&
            binding.locationInputLayout.getError() == null &&
            binding.activityTimeRangeInputLayout.getError() == null &&
            !binding.activityTimeRangeInput.getText().toString().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String[] timeRange = binding.activityTimeRangeInput.getText().toString().split(" - ");
                LocalDateTime start = LocalDateTime.parse(timeRange[0], formatter);
                LocalDateTime end = LocalDateTime.parse(timeRange[1], formatter);


                agenda.add(new CreateActivity(binding.nameInput.getText().toString(), binding.descriptionInput.getText().toString(),
                        binding.locationInput.getText().toString(), start, end));

                binding.nameInput.setText("");
                binding.nameInputLayout.setError(null);
                binding.descriptionInput.setText("");
                binding.descriptionInputLayout.setError(null);
                binding.locationInput.setText("");
                binding.locationInputLayout.setError(null);
                binding.activityTimeRangeInput.setText("");
                binding.activityTimeRangeInputLayout.setError(null);
            } else {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) requireContext(), "Error", "Validation failed!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    private void showTimeRangePicker() {
        showTimePicker("Select Start Time", (startHour, startMinute) -> {
            showTimePicker("Select End Time", (endHour, endMinute) -> {
                // Convert eventDate to LocalDate
                LocalDateTime start = eventDate.withHour(startHour).withMinute(startMinute).withSecond(0);
                LocalDateTime end = eventDate.withHour(endHour).withMinute(endMinute).withSecond(0);

                if (start.isBefore(end)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String formattedRange = formatter.format(start) + " - " + formatter.format(end);
                    binding.activityTimeRangeInput.setText(formattedRange);
                    binding.activityTimeRangeInputLayout.setError(null);
                } else {
                    binding.activityTimeRangeInputLayout.setError("Start time must be before end time.");
                }
            });
        });
    }

    private void showTimePicker(String title, OnTimeSelectedListener listener) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(title)
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            listener.onTimeSelected(hour, minute);
        });

        timePicker.show(getParentFragmentManager(), "TimePicker");
    }

    private interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }
}