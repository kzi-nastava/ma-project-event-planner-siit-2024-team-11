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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;

import com.example.eventy.R;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.custom.SingleSpinner;
import com.example.eventy.databinding.BottomSheetHomeEventsFilterBinding;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.events.model.Location;
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
    private ArrayList<String> eventTypes;
    private ArrayList<String> locations;

    public EventFilterBottomSheetFragment(ArrayList<String> eventTypes, ArrayList<String> locations) {
        this.eventTypes = eventTypes;
        this.locations = locations;
    }

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

        setupFilterInputs();

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

    private void setupFilterInputs() {
        setupFilterEventTypes();
        setupFilterLocation();
        setupFilterDay();
        setupFilterDateSelection();
        setupFilterResetAll();
    }

    private void setupFilterEventTypes() {
        MultiSpinner eventTypeMultiSpinner = binding.eventTypeFilter;
        eventTypeMultiSpinner.setItems(eventTypes, "-", "Event types");
    }

    private void setupFilterLocation() {
        SingleSpinner locationSingleSpinner = binding.locationFilter;
        locationSingleSpinner.setItems(locations, "-", "Locations");
    }

    private void setupFilterDay() {
        Spinner daySpinner = binding.eventDayFilter;

        String[] dayTypes = new String[] {"Any day", "Custom"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dayTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        daySpinner.setAdapter(arrayAdapter);

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
                    selectedStartDateTime = null;
                    selectedEndDateTime = null;
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

    private void setupFilterDateSelection() {
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

    private void setupFilterResetAll() {
        AppCompatButton resetAllButton = binding.resetAllFilter;
        resetAllButton.setOnClickListener(v -> {
            binding.locationFilter.restoreSelectedItem("-");
            binding.eventTypeFilter.restoreSelectedItem(new ArrayList<String>());
            binding.maxParticipantsFilter.setText(null);
            binding.eventDayFilter.setSelection(0);
            binding.eventShowSelectedDate.setText("Not selected");
            selectedStartDateTime = null;
            selectedEndDateTime = null;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public EventFilters getSelectedFilters() {
        EventFilters selectedFilters = new EventFilters();

        // Location
        String selectedLocation = binding.locationFilter.getSelectedItem().toString();
        selectedFilters.setSelectedLocation(selectedLocation);

        // Event Types
        MultiSpinner multiSpinner = binding.eventTypeFilter;
        List<String> selectedEventTypes = multiSpinner.getSelectedItems();
        selectedFilters.setSelectedEventTypes((ArrayList<String>) selectedEventTypes);

        // Max Participants
        String maxParticipants = binding.maxParticipantsFilter.getText().toString();
        selectedFilters.setMaxParticipants(maxParticipants);

        // Start Date & End Date
        selectedFilters.setSelectedStartDateTime(selectedStartDateTime);
        selectedFilters.setSelectedEndDateTime(selectedEndDateTime);

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