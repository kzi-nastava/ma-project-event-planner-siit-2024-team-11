package com.example.eventy.services;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentServiceReservationBinding;
import com.example.eventy.events.EventDetailsDialog;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Reservation;
import com.example.eventy.model.solution.Service;
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
    // event & service
    private Event selectedEvent = null;
    private Service selectedService = null;
    // date
    private boolean isDatePickerOpened = false;
    private Long eventDate = null;
    // start hour
    private boolean isStartDatePickerOpened = false;
    private int selectedStartHour = -1;
    private int selectedStartMinute = -1;
    private String formattedStartTime = "__:__";
    // end hour
    private boolean isEndDatePickerOpened = false;
    private int selectedEndHour = -1;
    private int selectedEndMinute = -1;
    private String formattedEndTime = "__:__";
    // is selected time range valid
    private Boolean isTimeValid = false;

    public ReservationFragment() {
        // Required empty public constructor
    }

    public ReservationFragment(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceReservationBinding.inflate(inflater, container, false);

        if (selectedEvent == null) {
            NavController navController = Navigation.findNavController(container);
            navController.popBackStack();
            navController.navigate(R.id.service_reservation);
        }
        selectedService = getService();
        setupServiceDetails();

        setupSeeEventButton();

        setupDatePicker();
        setupTimePicker();

        binding.confirmReservationButton.setOnClickListener(v -> {
            Calendar startDateTime = Calendar.getInstance();
            if (selectedStartHour == -1 || selectedStartMinute == -1 || eventDate == null) {
                startDateTime = null;
                if (eventDate == null) {
                    TextView showSelectedDateText = binding.eventShowSelectedDate;
                    showSelectedDateText.setText("Date needs to be selected!");
                    showSelectedDateText.setTextColor(Color.parseColor("#F40F0F"));
                }
                if (selectedStartHour == -1 || selectedStartMinute == -1) {
                    TextView showSelectedDateText = binding.showSelectedTimeRange;
                    showSelectedDateText.setText("Time range needs to be selected!");
                    showSelectedDateText.setTextColor(Color.parseColor("#F40F0F"));
                }
            } else {
                startDateTime.setTimeInMillis(eventDate);
                startDateTime.set(Calendar.HOUR_OF_DAY, selectedStartHour);
                startDateTime.set(Calendar.MINUTE, selectedStartMinute);
            }

            Calendar endDateTime = Calendar.getInstance();
            if (selectedEndHour == -1 || selectedEndMinute == -1 || eventDate == null) {
                endDateTime = null;
                if (eventDate == null) {
                    TextView showSelectedDateText = binding.eventShowSelectedDate;
                    showSelectedDateText.setText("Date needs to be selected!");
                    showSelectedDateText.setTextColor(Color.parseColor("#F40F0F"));
                }
                if (selectedEndHour == -1 || selectedEndMinute == -1) {
                    TextView showSelectedDateText = binding.showSelectedTimeRange;
                    showSelectedDateText.setText("Time range needs to be selected!");
                    showSelectedDateText.setTextColor(Color.parseColor("#F40F0F"));
                }
            } else {
                endDateTime.setTimeInMillis(eventDate);
                endDateTime.set(Calendar.HOUR_OF_DAY, selectedEndHour);
                endDateTime.set(Calendar.MINUTE, selectedEndMinute);
            }

            String startDateTimeString = startDateTime == null ? "start: NULL" :
                    startDateTime.get(Calendar.DAY_OF_MONTH) + "." +
                    startDateTime.get(Calendar.MONTH) + "." +
                    startDateTime.get(Calendar.YEAR) + ". " +
                    startDateTime.get(Calendar.HOUR_OF_DAY) + "h " +
                    startDateTime.get(Calendar.MINUTE) + "min";

            String endDateTimeString = endDateTime == null ? "end: NULL" :
                    endDateTime.get(Calendar.DAY_OF_MONTH) + "." +
                    endDateTime.get(Calendar.MONTH) + "." +
                    endDateTime.get(Calendar.YEAR) + ". " +
                    endDateTime.get(Calendar.HOUR_OF_DAY) + "h " +
                    endDateTime.get(Calendar.MINUTE) + "min";

            if (isTimeValid) {
                Toast.makeText(this.getContext(), startDateTimeString, Toast.LENGTH_SHORT).show();
                Toast.makeText(this.getContext(), endDateTimeString, Toast.LENGTH_SHORT).show();
                Reservation newReservation = new Reservation();
                newReservation.setId(1L);
                newReservation.setSelectedEvent(selectedEvent);
                newReservation.setSelectedService(selectedService);
                newReservation.setReservationStartDateTime(startDateTime);
                newReservation.setReservationEndDateTime(endDateTime);
                Toast.makeText(this.getContext(), newReservation.toString(), Toast.LENGTH_LONG).show();
                NavController navController = Navigation.findNavController(container);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            } else {
                Toast.makeText(this.getContext(), "Time is not valid!", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
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
            90, 120, 14, 7,
            ReservationConfirmationType.MANUAL, eventTypes
        );
    }

    private void setupServiceDetails() {
        TextView name = binding.service.name;
        name.setText('"' + selectedService.getName() + '"');

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
        eventType1.setText(eventTypeString1);

        TextView eventType2 = binding.service.eventType2;
        eventType2.setText(eventTypeString2);

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

    private void setupSeeEventButton() {
        Button seeEventButton = binding.seeEventButton;
        seeEventButton.setOnClickListener(v -> {
            EventDetailsDialog cdd = new EventDetailsDialog(this.getActivity(), selectedEvent);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });
    }

    private void setupDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a date");

        // Calculate the minimum date (5 days from today)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, selectedService.getReservationDeadline()); // Add 5 days to the current date
        long minDate = calendar.getTimeInMillis();

        // Set constraints to disable past dates
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(minDate)); // Only future dates are valid
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();

        Button dateRangeButton = binding.selectDateButton;
        dateRangeButton.setOnClickListener(v1 -> {
            if (!isDatePickerOpened) {
                materialDatePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                isDatePickerOpened = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            isDatePickerOpened = false;
            eventDate = selection;

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String eventDateString = sdf.format(new Date(eventDate));
            dateRangeButton.setText(eventDateString);

            binding.selectDateButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);

            sdf = new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault());
            eventDateString = sdf.format(new Date(eventDate));
            TextView showSelectedDateText = binding.eventShowSelectedDate;
            showSelectedDateText.setText("Selected date is: " + eventDateString);
            showSelectedDateText.setTextColor(Color.parseColor("#BFBABA"));
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

        // duration fixed
        if (selectedService.getMinReservationTime().equals(selectedService.getMaxReservationTime())) {
            selectEndTimeButton.setEnabled(false);
            selectEndTimeButton.getBackground().setAlpha(50);
            binding.selectEndTimeText.setText("* Duration is fixed.");
        }

        TextView showSelectedTimeRangeTextView = binding.showSelectedTimeRange;

        selectStartTimeButton.setOnClickListener(v -> {
            if (isStartDatePickerOpened) {
                return;
            }
            isStartDatePickerOpened = true;

            int totalEndMinutes = selectedEndHour * 60 + selectedEndMinute;
            int totalStartMinutes = (totalEndMinutes - selectedService.getMinReservationTime() + 24 * 60) % (24 * 60);
            int predictedStartHour = totalStartMinutes / 60;
            int predictedStartMinute = totalStartMinutes % 60;
            MaterialTimePicker materialStartTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT START TIME")
                .setHour(selectedStartHour == -1 ? (selectedEndHour == -1 ? 8 : predictedStartHour) : selectedStartHour)
                .setMinute(selectedStartMinute == -1 ? (selectedEndMinute == -1 ? 0 : predictedStartMinute) : selectedStartMinute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

            materialStartTimePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_START_TIME_PICKER");

            materialStartTimePicker.addOnPositiveButtonClickListener((v1) -> {
                isStartDatePickerOpened = false;

                selectedStartHour = materialStartTimePicker.getHour();
                selectedStartMinute = materialStartTimePicker.getMinute();

                // duration fixed
                if (selectedService.getMinReservationTime().equals(selectedService.getMaxReservationTime())) {
                    int minutesAll = selectedStartHour * 60 + selectedStartMinute + selectedService.getMinReservationTime();
                    int hour = (minutesAll / 60) % 24;
                    int minute = minutesAll - (minutesAll / 60) * 60;

                    selectedEndHour = hour;
                    selectedEndMinute = minute;

                    if (selectedEndHour >= 10 & selectedEndHour < 24) {
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

                    binding.selectEndTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);

                    String endTimeRangeButtonString = formattedEndTime + "h";
                    selectEndTimeButton.setText(endTimeRangeButtonString);
                }

                if (selectedStartHour >= 10 & selectedStartHour < 24) {
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
                    int minutes = (selectedStartHour <= selectedEndHour) ? (selectedEndHour * 60 + selectedEndMinute - (selectedStartHour * 60 + selectedStartMinute)) : (24 * 60 - (selectedStartHour * 60 + selectedStartMinute) + selectedEndHour * 60 + selectedEndMinute);

                    if (minutes < selectedService.getMinReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Minimum reservation time is " + selectedService.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    } else if (minutes > selectedService.getMaxReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Maximum reservation time is " + selectedService.getMaxReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    }
                }

                if (selectedEndHour != -1) {
                    binding.selectEndTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);
                    if (selectedService.getMinReservationTime().equals(selectedService.getMaxReservationTime())) {
                        binding.selectEndTimeButton.getBackground().setAlpha(50);
                    }
                }

                binding.selectStartTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);
                timeRangeString = "Selected time range is: " + formattedStartTime + "-" + formattedEndTime + "h";
                showSelectedTimeRangeTextView.setText(timeRangeString);
                showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#BFBABA"));
                isTimeValid = selectedEndHour != -1;
            });

            materialStartTimePicker.addOnNegativeButtonClickListener(dialog -> {
                isStartDatePickerOpened = false;
            });

            materialStartTimePicker.addOnCancelListener(dialog -> {
                isStartDatePickerOpened = false;
            });
        });

        selectEndTimeButton.setOnClickListener(v -> {
            if (isEndDatePickerOpened) {
                return;
            }
            isEndDatePickerOpened = true;

            int totalStartMinutes = selectedStartHour * 60 + selectedStartMinute;
            int totalEndMinutes = (totalStartMinutes + selectedService.getMinReservationTime() + 24 * 60) % (24 * 60);
            int predictedEndHour = totalEndMinutes / 60;
            int predictedEndMinute = totalEndMinutes % 60;
            MaterialTimePicker materialEndTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT END TIME")
                .setHour(selectedEndHour == -1 ? (selectedStartHour == -1 ? ((8 + selectedService.getMinReservationTime() / 60 % 24 + 24) % 24) : predictedEndHour) : selectedEndHour)
                .setMinute(selectedEndMinute == -1 ? (selectedStartMinute == -1 ? ((selectedService.getMinReservationTime() % 60 + 60) % 60): predictedEndMinute) : selectedEndMinute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

            materialEndTimePicker.show(ReservationFragment.this.getParentFragmentManager(), "MATERIAL_END_TIME_PICKER");

            materialEndTimePicker.addOnPositiveButtonClickListener((v1) -> {
                isEndDatePickerOpened = false;

                selectedEndHour = materialEndTimePicker.getHour();
                selectedEndMinute = materialEndTimePicker.getMinute();

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
                    int minutes = (selectedStartHour <= selectedEndHour) ? (selectedEndHour * 60 + selectedEndMinute - (selectedStartHour * 60 + selectedStartMinute)) : (24 * 60 - (selectedStartHour * 60 + selectedStartMinute) + selectedEndHour * 60 + selectedEndMinute);
                    if (minutes < selectedService.getMinReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Minimum reservation time is " + selectedService.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    } else if (minutes > selectedService.getMaxReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Maximum reservation time is " + selectedService.getMaxReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    }
                }

                if (selectedStartHour != -1) {
                    binding.selectStartTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);
                }

                binding.selectEndTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);
                timeRangeString = "Selected time range is: " + formattedStartTime + "-" + formattedEndTime + "h";
                showSelectedTimeRangeTextView.setText(timeRangeString);
                showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#BFBABA"));
                isTimeValid = selectedStartHour != -1;
            });

            materialEndTimePicker.addOnNegativeButtonClickListener(dialog -> {
                isEndDatePickerOpened = false;
            });

            materialEndTimePicker.addOnCancelListener(dialog -> {
                isEndDatePickerOpened = false;
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

