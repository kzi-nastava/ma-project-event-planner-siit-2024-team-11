package com.example.eventy.services;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.eventy.databinding.FragmentServiceReservationBinding;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.utils.Location;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationFragment extends Fragment {
    private FragmentServiceReservationBinding binding;
    private Event selectedEvent;
    private Service selectedService;
    private boolean isDatePickerOpened = false;
    private int selectedStartHour = -1;
    private int selectedStartMinute = -1;
    private String formattedStartTime = "__:__";
    private int selectedEndHour = -1;
    private int selectedEndMinute = -1;
    private String formattedEndTime = "__:__";
    private boolean isStartDatePickerOpened = false;
    private boolean isEndDatePickerOpened = false;
    private Boolean isTimeValid = null;

    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceReservationBinding.inflate(inflater, container, false);

        selectedEvent = getEvent();
        setupEventDetails();

        selectedService = getService();
        setupServiceDetails();

        setupDatePicker();
        setupTimePicker();

        return binding.getRoot();
    }

    private Event getEvent() {
        EventType weddingType = new EventType("Wedding", "An unforgettable celebration of love and commitment", true);
        Location weddingLocation = new Location("Grand Hall", "123 Wedding St, Cityville", 40.7128, -74.0060);

        return new Event("Mark & Jana's Wedding", "An unforgettable celebration of love and commitment", 200, PrivacyType.PUBLIC, new Date(), weddingLocation, weddingType);
    }

    private void setupEventDetails() {

    }

    private Service getService() {
        ArrayList<EventType> eventTypes = new ArrayList<>();
        EventType eventType1 = new EventType();
        eventType1.setName("Wedding");
        EventType eventType2 = new EventType();
        eventType2.setName("Sport");
        EventType eventType3 = new EventType();
        eventType3.setName("Conference");
        EventType eventType4 = new EventType();
        eventType4.setName("Party");

        eventTypes.add(eventType1);
        eventTypes.add(eventType2);
        eventTypes.add(eventType3);
        eventTypes.add(eventType4);

        return new Service(
            "Bon Jovi",
            new Category("music", "Neki description", Status.ACCEPTED),
            "Best band ever!", 800.0, 5,
            new ArrayList<>(Arrays.asList("dj1.jpg", "dj2.jpg")),
            false, true, true, "Includes sound and lighting equipment",
            60, 120, 14, 7,
            ReservationConfirmationType.MANUAL, eventTypes
        );
    }

    private void setupServiceDetails() {
        TextView name = binding.service.name;
        name.setText(selectedService.getName());

        TextView category = binding.service.category;
        category.setText("Category: " + selectedService.getCategory().getName());

        TextView duration = binding.service.duration;
        String durationString = (selectedService.getMinReservationTime().equals(selectedService.getMaxReservationTime())) ? "Duration: " + selectedService.getMinReservationTime() + "min" : "Duration: " + selectedService.getMinReservationTime() + "-" + selectedService.getMaxReservationTime() + "min";
        duration.setText(durationString);

        TextView reservationType = binding.service.reservationType;
        reservationType.setText("Reservation: " + ((selectedService.getReservationConfirmationType()).equals(ReservationConfirmationType.AUTOMATIC) ? "auto" : "manual"));

        String eventTypeString1 = selectedService.getEventTypes().get(0).getName();
        String eventTypeString2 = selectedService.getEventTypes().get(1).getName();

        TextView eventType1 = binding.service.eventType1;
        category.setText(eventTypeString1);

        TextView eventType2 = binding.service.eventType2;
        category.setText(eventTypeString2);

        TextView beforePrice = binding.service.beforePrice;
        TextView crossedOutPrice = binding.service.crossedOutPrice;
        String currentPriceString = String.valueOf(selectedService.getPrice());
        beforePrice.setText(currentPriceString);
        crossedOutPrice.setText(currentPriceString);

        TextView currentPrice = binding.service.currentPrice;
        double discountedPrice = selectedService.getPrice() - selectedService.getPrice() * selectedService.getDiscount() / 100;
        String discountedPriceString = String.format("%.2f", discountedPrice);
        currentPrice.setText(discountedPriceString);

        TextView reserveBy = binding.reserveBy;
        reserveBy.setText(selectedService.getReservationDeadline() + " days");

        TextView cancelBy = binding.cancelBy;
        cancelBy.setText(selectedService.getCancellationDeadline() + " days");
    }

    private void setupDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Calculate the minimum date (5 days from today)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, selectedService.getReservationDeadline()); // Add 5 days to the current date
        long minDate = calendar.getTimeInMillis();

        // Set constraints to disable past dates
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(minDate)); // Only future dates are valid
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

        Button dateRangeButton = binding.selectDateButton;
        dateRangeButton.setOnClickListener(v1 -> {
            if (!isDatePickerOpened) {
                materialDatePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                isDatePickerOpened = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            isDatePickerOpened = false;

            Long startDate = selection.first;
            Long endDate = selection.second;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString.equals(endDateString) ? startDateString : startDateString + " - " + endDateString;
            dateRangeButton.setText(selectedDateRange);

            TextView showSelectedDateText = binding.eventShowSelectedDate;
            showSelectedDateText.setText(startDateString.equals(endDateString) ? "Selected date is: " + selectedDateRange : "Selected dates are: " + selectedDateRange);
        });

        materialDatePicker.addOnNegativeButtonClickListener(dialog -> {
            isDatePickerOpened = false;
        });

        materialDatePicker.addOnCancelListener(dialog -> {
            isDatePickerOpened = false;
        });
    }

    private void setupTimePicker() {
        Button selectStartTimeButton = binding.selectStartTimeButton;
        Button selectEndTimeButton = binding.selectEndTimeButton;
        TextView showSelectedTimeRangeTextView = binding.showSelectedTimeRange;

        selectStartTimeButton.setOnClickListener(v -> {
            if (isStartDatePickerOpened) {
                return;
            }
            isStartDatePickerOpened = true;

            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT START TIME")
                .setHour(selectedStartHour == -1 ? 8 : selectedStartHour)
                .setMinute(selectedStartMinute == -1 ? 0 : selectedStartMinute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

            materialTimePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_START_TIME_PICKER");

            materialTimePicker.addOnPositiveButtonClickListener((v1) -> {
                isStartDatePickerOpened = false;

                selectedStartHour = materialTimePicker.getHour();
                selectedStartMinute = materialTimePicker.getMinute();

                if (selectedStartHour >= 10) {
                    if (selectedStartMinute < 10) {
                        formattedStartTime = selectedStartHour + ":0" + selectedStartMinute;
                    } else {
                        formattedStartTime = selectedStartHour + ":" + selectedStartMinute;
                    }
                } else {
                    if (selectedStartMinute < 10) {
                        formattedStartTime = "0" + selectedStartHour + ":0" + selectedStartMinute;
                    } else {
                        formattedStartTime = "0" + selectedStartHour + ":" + selectedStartMinute;
                    }
                }

                String timeRangeButtonString = formattedStartTime + "h";
                selectStartTimeButton.setText(timeRangeButtonString);

                String timeRangeString = "";
                if (selectedStartHour != -1 && selectedEndHour != -1) {
                    int minutes = (selectedStartHour <= selectedEndHour) ? (selectedEndHour - selectedStartHour) * 60 : (24 - selectedEndHour + selectedStartHour) * 60;

                    if (minutes < selectedService.getMinReservationTime()) {
                        timeRangeString = "Minimum reservation time is " + selectedService.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        return;
                    } else if (minutes > selectedService.getMaxReservationTime()) {
                        timeRangeString = "Maximum reservation time is " + selectedService.getMaxReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        return;
                    }
                }

                timeRangeString = "Selected time range is: " + formattedStartTime + " - " + formattedEndTime + "h";
                showSelectedTimeRangeTextView.setText(timeRangeString);
                showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#BFBABA"));
            });

            materialTimePicker.addOnNegativeButtonClickListener(dialog -> {
                isStartDatePickerOpened = false;
            });

            materialTimePicker.addOnCancelListener(dialog -> {
                isStartDatePickerOpened = false;
            });
        });


        selectEndTimeButton.setOnClickListener(v -> {
            if (isEndDatePickerOpened) {
                return;
            }
            isEndDatePickerOpened = true;

            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT END TIME")
                .setHour(selectedEndHour == -1 ? 8 : selectedEndHour)
                .setMinute(selectedEndMinute == -1 ? 0 : selectedEndMinute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

            materialTimePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_END_TIME_PICKER");

            materialTimePicker.addOnPositiveButtonClickListener((v1) -> {
                isEndDatePickerOpened = false;

                selectedEndHour = materialTimePicker.getHour();
                selectedEndMinute = materialTimePicker.getMinute();

                if (selectedEndHour >= 10) {
                    if (selectedEndMinute < 10) {
                        formattedEndTime = selectedEndHour + ":0" + selectedEndMinute;
                    } else {
                        formattedEndTime = selectedEndHour + ":" + selectedEndMinute;
                    }
                } else {
                    if (selectedEndMinute < 10) {
                        formattedEndTime = "0" + selectedEndHour + ":0" + selectedEndMinute;
                    } else {
                        formattedEndTime = "0" + selectedEndHour + ":" + selectedEndMinute;
                    }
                }

                String timeRangeButtonString = formattedEndTime + "h";
                selectEndTimeButton.setText(timeRangeButtonString);

                String timeRangeString = "";
                if (selectedStartHour != -1 && selectedEndHour != -1) {
                    int minutes = (selectedStartHour <= selectedEndHour) ? (selectedEndHour - selectedStartHour) * 60 : (24 - selectedEndHour + selectedStartHour) * 60;

                    if (minutes < selectedService.getMinReservationTime()) {
                        timeRangeString = "Minimum reservation time is " + selectedService.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        return;
                    } else if (minutes > selectedService.getMaxReservationTime()) {
                        timeRangeString = "Maximum reservation time is " + selectedService.getMaxReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        return;
                    }
                }

                timeRangeString = "Selected time range is: " + formattedStartTime + " - " + formattedEndTime + "h";
                showSelectedTimeRangeTextView.setText(timeRangeString);
                showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#BFBABA"));
            });

            materialTimePicker.addOnNegativeButtonClickListener(dialog -> {
                isEndDatePickerOpened = false;
            });

            materialTimePicker.addOnCancelListener(dialog -> {
                isEndDatePickerOpened = false;
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ArrayList<Event> events = getEvents();

        //eventsAdapter = new EventsAdapter(requireContext(), events);

        //binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        //binding.eventsRecycler.setAdapter(eventsAdapter);
    }

    @NonNull
    private static ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<>();
        return events;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

