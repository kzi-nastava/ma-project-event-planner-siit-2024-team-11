package com.example.eventy.home.events.filters;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.example.eventy.R;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.custom.SingleSpinner;
import com.example.eventy.databinding.BottomSheetHomeEventsFilterBinding;
import com.example.eventy.events.model.EventFilters;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetHomeEventsFilterBinding binding;
    private FilterListener listener;
    private boolean isDatePickerOpened = false;
    private LocalDateTime selectedStartDateTime = null;
    private LocalDateTime selectedEndDateTime = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetHomeEventsFilterBinding.inflate(inflater, container, false);

        // Check if listener is set
        if (getParentFragment() instanceof FilterListener) {
            listener = (FilterListener) getParentFragment();
        } else {
            throw new RuntimeException("Parent fragment must implement FilterSelectedListener");
        }

        setupInputs();

        if (getArguments() != null) {
            String selectedLocation = getArguments().getString("location");
            ArrayList<String> selectedEventTypes = getArguments().getStringArrayList("eventTypes");
            String maxParticipants = getArguments().getString("maxParticipants");
            String selectedDateRange = getArguments().getString("dateRange");

            // Pre-fill the inputs with these values
            binding.locationFilter.restoreSelectedItem(selectedLocation);
            binding.eventTypeFilter.restoreSelectedItem(selectedEventTypes);
            binding.maxParticipantsFilter.setText(maxParticipants);
            binding.eventShowSelectedDate.setText(selectedDateRange);
        }

        binding.eventsConfirmButton.setOnClickListener(v -> {
            EventFilters selectedFilters = getSelectedFilters();
            if (listener != null) {
                listener.onFiltersSelected(selectedFilters);
            }
            dismiss();
        });

        return binding.getRoot();
    }

    private void setupInputs() {
        setupEventFilterEventTypes();
        setupEventFilterLocation();
        setupEventFilterDay();
        setupEventFilterDateSelection();
    }

    private void setupEventFilterEventTypes() {
        MultiSpinner eventTypeMultiSpinner = binding.eventTypeFilter;

        ArrayList<String> eventTypes = new ArrayList<>();
        eventTypes.add("Wedding"); eventTypes.add("Sport"); eventTypes.add("Conference");
        eventTypes.add("Party"); eventTypes.add("Prom"); eventTypes.add("Big party");
        eventTypeMultiSpinner.setItems(eventTypes, "-", "Event types");
    }

    private void setupEventFilterLocation() {
        SingleSpinner locationSingleSpinner = binding.locationFilter;

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Belgrade");
        locations.add("Gradiška");
        locations.add("New York");
        locations.add("Paris");
        locations.add("Kuala Lumpur");
        locations.add("Banja Luka");

        // Set items for the spinner with a default text
        locationSingleSpinner.setItems(locations, "-", "Locations");
    }

    private void setupEventFilterDay() {
        Spinner daySpinner = binding.eventDayFilter;

        String[] dayTypes = new String[] {
                "Any day", "Custom"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, dayTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        daySpinner.setAdapter(arrayAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button dateRangeButton = binding.dateRangeFilter;
        dateRangeButton.setEnabled(false);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) {
                    dateRangeButton.setEnabled(true);
                    dateRangeButton.setBackgroundResource(R.drawable.filter_button_background);
                } else {
                    dateRangeButton.setEnabled(false);
                    dateRangeButton.setBackgroundResource(R.drawable.filter_button_background_disabled);
                    dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                    binding.eventShowSelectedDate.setText("No date selected");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dateRangeButton.setEnabled(false);
                dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                binding.eventShowSelectedDate.setText("No date selected");
            }
        });
    }

    private void setupEventFilterDateSelection() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

        binding.dateRangeFilter.setOnClickListener(v1 -> {
            if (!isDatePickerOpened) {
                materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                isDatePickerOpened = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            selectedStartDateTime = Instant.ofEpochMilli(startDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            selectedEndDateTime = Instant.ofEpochMilli(endDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString.equals(endDateString) ? startDateString : startDateString + " - " + endDateString;
            binding.dateRangeFilter.setText(selectedDateRange);

            binding.eventShowSelectedDate.setText(startDateString.equals(endDateString)
                    ? "Selected date is: " + selectedDateRange
                    : "Selected dates are: " + selectedDateRange);

            isDatePickerOpened = false;
        });

        materialDatePicker.addOnNegativeButtonClickListener(dialog -> {
            isDatePickerOpened = false;
        });

        materialDatePicker.addOnCancelListener(dialog -> {
            isDatePickerOpened = false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public EventFilters getSelectedFilters() {
        EventFilters selectedFilters = new EventFilters();

        // Selected Location
        String selectedLocation = binding.locationFilter.getSelectedItem().toString();
        selectedFilters.setSelectedLocation(selectedLocation);

        // Selected Event Types
        MultiSpinner multiSpinner = binding.eventTypeFilter;
        List<String> selectedEventTypes = multiSpinner.getSelectedItems();
        /*StringBuilder selectedEventTypesText = new StringBuilder();
        for (int i = 0; i < selectedEventTypes.size(); i++) {
            selectedEventTypesText.append(selectedEventTypes.get(i)).append(",");
        }
        if (selectedEventTypesText.length() > 0) { // remove last comma
            selectedEventTypesText.setLength(selectedEventTypesText.length() - 2);
        }*/
        selectedFilters.setSelectedEventTypes((ArrayList<String>) selectedEventTypes);

        // Max Participants
        String maxParticipants = binding.maxParticipantsFilter.getText().toString();
        selectedFilters.setMaxParticipants(maxParticipants);

        /*// Selected Date Range
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        String dateRangeSummary = (selectedStartDateTime != null && selectedEndDateTime != null)
                ? selectedStartDateTime.format(formatter) + " - " + selectedEndDateTime.format(formatter)
                : "Not selected";

        // Collect all the selected filter values, even if empty
        StringBuilder filtersSummary = new StringBuilder();
        filtersSummary.append("Location: ").append(selectedLocation.isEmpty() ? "Not selected" : selectedLocation).append("\n")
                .append("Event Types: ").append(selectedEventTypesText.length() > 0 ? selectedEventTypesText : "Not selected").append("\n")
                .append("Max Participants: ").append(maxParticipants.isEmpty() ? "Not selected" : maxParticipants).append("\n")
                .append("Date Range: ").append(dateRangeSummary);
         */
        selectedFilters.setSelectedStartDateTime(selectedStartDateTime);
        selectedFilters.setSelectedEndDateTime(selectedEndDateTime);

        // Display the selected filter values or pass them to another method or API
        return selectedFilters;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Notify the activity or reset the flag here
        if (getParentFragment() instanceof FilterListener) {
            ((FilterListener) getParentFragment()).onFiltersClosed();
        }
    }

    public interface FilterListener {
        void onFiltersSelected(EventFilters filterValues);
        void onFiltersClosed();
    }
}