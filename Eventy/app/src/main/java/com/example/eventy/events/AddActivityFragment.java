package com.example.eventy.events;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventy.databinding.FragmentAddActivityBinding;
import com.example.eventy.events.model.Activity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiConsumer;

public class AddActivityFragment extends Fragment {

    private FragmentAddActivityBinding binding;
    private ArrayList<Activity> agenda;

    private Date minDate;

    public AddActivityFragment(ArrayList<Activity> agenda) {
        this.agenda = agenda;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddActivityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.locationInputLayout, binding.locationInput, this::validateRequired);

        minDate = new Date();
        binding.activityTimeRangeInput.setOnClickListener(v -> showDateRangePicker());

        binding.addActivityButton.setOnClickListener(v -> {
            String[] dateTimeRange = binding.activityTimeRangeInput.getText().toString().split("-");
            agenda.add(new Activity(binding.nameInput.getText().toString(), binding.descriptionInput.getText().toString(),
                    binding.locationInput.getText().toString(), dateTimeRange[0], dateTimeRange[1]));

            binding.nameInput.setText("");
            binding.nameInputLayout.setError(null);
            binding.descriptionInput.setText("");
            binding.descriptionInputLayout.setError(null);
            binding.locationInput.setText("");
            binding.locationInputLayout.setError(null);
            binding.activityTimeRangeInput.setText("");
            binding.activityTimeRangeInputLayout.setError(null);
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

    private void showDateRangePicker() {
        // Assume minDate is defined somewhere in your code
        long minDate = System.currentTimeMillis(); // Example: current date and time as minimum date

        // Create MaterialDatePicker for start date
        MaterialDatePicker<Long> startDatePicker = createDatePicker("Select Start Date", minDate);

        startDatePicker.addOnPositiveButtonClickListener(startDate -> {
            // After selecting start date, show time picker
            showTimePicker("Select Start Time", (startHour, startMinute) -> {
                // Store selected start time
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTimeInMillis(startDate);
                startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
                startCalendar.set(Calendar.MINUTE, startMinute);

                // Show end date picker
                MaterialDatePicker<Long> endDatePicker = createDatePicker("Select End Date", startCalendar.getTimeInMillis());
                endDatePicker.addOnPositiveButtonClickListener(endDate -> {
                    // Show time picker for end time
                    showTimePicker("Select End Time", (endHour, endMinute) -> {
                        // Store selected end time
                        Calendar endCalendar = Calendar.getInstance();
                        endCalendar.setTimeInMillis(endDate);
                        endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
                        endCalendar.set(Calendar.MINUTE, endMinute);

                        // Check that start date-time is before end date-time
                        if (startCalendar.before(endCalendar)) {
                            // Display the selected date-time range
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                            String selectedRange = dateTimeFormat.format(startCalendar.getTime()) + " - " + dateTimeFormat.format(endCalendar.getTime());
                            binding.activityTimeRangeInput.setText(selectedRange);
                            binding.activityTimeRangeInputLayout.setError(null);
                        } else {
                            binding.activityTimeRangeInputLayout.setError("Start date-time must be earlier than end date-time.");
                        }
                    });
                });

                endDatePicker.show(getParentFragmentManager(), "EndDatePicker");
            });
        });

        startDatePicker.show(getParentFragmentManager(), "StartDatePicker");
    }

    private MaterialDatePicker<Long> createDatePicker(String title, long minDate) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(minDate); // Set the minimum date

        return MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
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