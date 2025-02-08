package com.example.eventy.services;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.CreateReviewDialog;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.custom.LoadingDialog;
import com.example.eventy.custom.ValidOkDialog;
import com.example.eventy.databinding.FragmentServiceReservationBinding;
import com.example.eventy.events.EventDetailsDialog;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.reviews.model.CreateReview;
import com.example.eventy.services.model.Reservation;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFragment extends Fragment {
    private FragmentServiceReservationBinding binding;
    // event & service
    private EventCard selectedEventCard = null;
    private SolutionCard selectedServiceCard = null;
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
    private Boolean isReservationCreating = false;

    public ReservationFragment() {}

    public ReservationFragment(EventCard selectedEventCard, SolutionCard selectedServiceCard) {
        this.selectedEventCard = selectedEventCard;
        this.selectedServiceCard = selectedServiceCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServiceReservationBinding.inflate(inflater, container, false);

        if (selectedEventCard == null || selectedServiceCard == null) {
            NavController navController = Navigation.findNavController(container);
            navController.popBackStack();
            navController.navigate(R.id.service_reservation);
            return binding.getRoot();
        }

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

            if (isTimeValid) {
                Reservation newReservation = new Reservation();
                newReservation.setSelectedEventId(selectedEventCard.getEventId());
                newReservation.setSelectedServiceId(selectedServiceCard.getSolutionId());
                newReservation.setReservationStartDateTime(LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneId.systemDefault()));
                newReservation.setReservationEndDateTime(LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneId.systemDefault()));

                createReservation(newReservation, this.getContext(), container);

            } else {
                showErrorDialog("Time is not valid!");
            }
        });

        return binding.getRoot();
    }

    private void createReservation(Reservation newReservation, Context context, ViewGroup container) {
        if (isReservationCreating) {
            return;
        }
        isReservationCreating = true;

        Call<Reservation> call = ClientUtils.reservationService.createReservation(newReservation);
        LoadingDialog loadingDialog = new LoadingDialog(getActivity(),
                "Loading",
                "We are currently processing your request. Please wait a few seconds!");
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();

        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    if (isAdded() && getActivity() != null) {
                        loadingDialog.cancel();

                        Toast.makeText(context, newReservation.toString(), Toast.LENGTH_LONG).show();

                        ValidOkDialog validOkDialog = new ValidOkDialog(getActivity(), "Creation Successful", "Your service reservation was successful!");
                        validOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        validOkDialog.setOnDismissListener(dialog -> {
                            handleReviewService(container);
                        });
                        validOkDialog.show();
                    }
                } else {
                    loadingDialog.cancel();

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            String errorMessage = errorBody.replaceAll("[\\[\\]\"]", "");
                            String reason = errorMessage.split(":")[1].trim();

                            showErrorDialog("Error: " + reason);
                        } else {
                            showErrorDialog("An unknown error occurred while creating a service!");
                        }
                    } catch (IOException e) {
                        showErrorDialog("An unknown error occurred while creating a service");
                    }
                }
                isReservationCreating = false;
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                loadingDialog.cancel();

                showErrorDialog(t.getMessage());
                isReservationCreating = false;
            }
        });
    }

    private void handleReviewService(ViewGroup container) {
        Call<Boolean> call = ClientUtils.reviewService.isSolutionReviewedByUser(LoggedInHelperService.getId(), selectedServiceCard.getSolutionId());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    if (isAdded() && getActivity() != null) {
                        Boolean isReviewed = response.body();

                        if (!isReviewed) {
                            CreateReview createReview = new CreateReview(
                                LoggedInHelperService.getId(),
                                selectedServiceCard.getSolutionId(),
                                null,
                                null,
                                null
                            );

                            CreateReviewDialog createReviewDialog = new CreateReviewDialog(getActivity(), "\"" + selectedServiceCard.getName() + "\"", "Please rate the service you reserved!", createReview);
                            createReviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            createReviewDialog.setCanceledOnTouchOutside(false);
                            createReviewDialog.setOnDismissListener(dialog -> {
                                NavController navController = Navigation.findNavController(container);
                                navController.popBackStack();
                                navController.navigate(R.id.nav_home);
                            });
                            createReviewDialog.show();

                        } else {
                            NavController navController = Navigation.findNavController(container);
                            navController.popBackStack();
                            navController.navigate(R.id.nav_home);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {}
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Creation Failed", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    private void setupServiceDetails() {
        TextView name = binding.service.name;
        name.setText('"' + selectedServiceCard.getName() + '"');

        TextView category = binding.service.category;
        category.setText("Category: " + selectedServiceCard.getCategoryName());

        TextView duration = binding.service.duration;
        int d1 = selectedServiceCard.getMinReservationTime();
        int d2 = selectedServiceCard.getMaxReservationTime();
        String durationText = "Duration: " + ((d1 == d2) ? d1 : d1 + "-" + d2) + "min";
        duration.setText(durationText);

        TextView reservationType = binding.service.reservationType;
        String resType = selectedServiceCard.getReservationType().equals(ReservationConfirmationType.MANUAL) ? "manual" : "auto";
        reservationType.setText("Reservation: " + resType);

        TextView eventType1 = binding.service.eventType1;
        TextView eventType2 = binding.service.eventType2;
        LinearLayout eventType2Container = binding.service.eventTypeContainer2;
        TextView dots = binding.service.threeDots;

        ArrayList<String> eventTypeNames = selectedServiceCard.getEventTypeNames();
        int size = eventTypeNames.size();
        String et1 = size >= 1 ? eventTypeNames.get(0) : "";
        eventType1.setText(et1);
        eventType2Container.setVisibility(View.GONE);
        dots.setVisibility(View.GONE);

        String et2 = "";
        if (size >= 2) {
            et2 = eventTypeNames.get(1);
            eventType2Container.setVisibility(View.VISIBLE);
            eventType2.setText(et2);
            dots.setVisibility(size > 2 ? View.VISIBLE : View.GONE);
        }

        String currentPriceString = String.valueOf(selectedServiceCard.getPrice());
        binding.service.beforePrice.setText(currentPriceString);
        binding.service.crossedOutPrice.setText(currentPriceString);

        double discountedPrice = selectedServiceCard.getPrice() - selectedServiceCard.getPrice() * selectedServiceCard.getDiscount() / 100;
        String discountedPriceString = String.format("%.2f", discountedPrice);
        binding.service.currentPrice.setText(discountedPriceString);

        View discountContainer = binding.service.discountContainer;
        if (selectedServiceCard.getDiscount() == 0) {
            discountContainer.setVisibility(View.GONE);
        } else {
            discountContainer.setVisibility(View.VISIBLE);
        }

        Drawable picture = PictureHelperService.getPicture(selectedServiceCard.getFirstImageUrl(), getContext());
        if (picture != null) {
            binding.service.image.setBackground(picture);
        }
        //binding.service.image.setImageBitmap(PictureHelperService.getPicture(selectedServiceCard.getFirstImageUrl()));

        TextView reserveBy = binding.reserveBy;
        reserveBy.setText(selectedServiceCard.getReservationDeadline() + " days");

        TextView cancelBy = binding.cancelBy;
        cancelBy.setText(selectedServiceCard.getCancellationDeadline() + " days");
    }

    private void setupSeeEventButton() {
        Button seeEventButton = binding.seeEventButton;
        seeEventButton.setOnClickListener(v -> {
            EventDetailsDialog cdd = new EventDetailsDialog(this.getActivity(), selectedEventCard);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });
    }

    private void setupDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a date");

        // Calculate the minimum date (5 days from today)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, selectedServiceCard.getReservationDeadline() - 1);
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
        if (selectedServiceCard.getMinReservationTime().equals(selectedServiceCard.getMaxReservationTime())) {
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
            int totalStartMinutes = (totalEndMinutes - selectedServiceCard.getMinReservationTime() + 24 * 60) % (24 * 60);
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
                if (selectedServiceCard.getMinReservationTime().equals(selectedServiceCard.getMaxReservationTime())) {
                    int minutesAll = selectedStartHour * 60 + selectedStartMinute + selectedServiceCard.getMinReservationTime();
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

                    if (minutes < selectedServiceCard.getMinReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Minimum reservation time is " + selectedServiceCard.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    } else if (minutes > selectedServiceCard.getMaxReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Maximum reservation time is " + selectedServiceCard.getMaxReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    }
                }

                if (selectedEndHour != -1) {
                    binding.selectEndTimeButton.setBackgroundResource(R.drawable.reservation_select_inputs_border);
                    if (selectedServiceCard.getMinReservationTime().equals(selectedServiceCard.getMaxReservationTime())) {
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
            int totalEndMinutes = (totalStartMinutes + selectedServiceCard.getMinReservationTime() + 24 * 60) % (24 * 60);
            int predictedEndHour = totalEndMinutes / 60;
            int predictedEndMinute = totalEndMinutes % 60;
            MaterialTimePicker materialEndTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT END TIME")
                .setHour(selectedEndHour == -1 ? (selectedStartHour == -1 ? ((8 + selectedServiceCard.getMinReservationTime() / 60 % 24 + 24) % 24) : predictedEndHour) : selectedEndHour)
                .setMinute(selectedEndMinute == -1 ? (selectedStartMinute == -1 ? ((selectedServiceCard.getMinReservationTime() % 60 + 60) % 60): predictedEndMinute) : selectedEndMinute)
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
                    if (minutes < selectedServiceCard.getMinReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Minimum reservation time is " + selectedServiceCard.getMinReservationTime() + " minutes!";
                        showSelectedTimeRangeTextView.setText(timeRangeString);
                        showSelectedTimeRangeTextView.setTextColor(Color.parseColor("#F40F0F"));
                        isTimeValid = false;
                        return;
                    } else if (minutes > selectedServiceCard.getMaxReservationTime()) {
                        binding.selectStartTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        binding.selectEndTimeButton.setBackgroundResource(R.drawable.event_card_description_background);
                        timeRangeString = "Maximum reservation time is " + selectedServiceCard.getMaxReservationTime() + " minutes!";
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

