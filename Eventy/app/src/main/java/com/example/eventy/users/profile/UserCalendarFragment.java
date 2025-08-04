package com.example.eventy.users.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserCalendarBinding;
import com.example.eventy.users.model.CalendarOccupancy;
import com.example.eventy.users.model.OccupancyType;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCalendarFragment extends Fragment {

    private FragmentUserCalendarBinding binding;
    private HashMap<CalendarDay, String> eventDates;
    private HashMap<CalendarDay, String> productDates;
    private HashMap<CalendarDay, String> serviceDates;
    private HashSet<CalendarDay> eventDateKeys;
    private HashSet<CalendarDay> productDateKeys;
    private HashSet<CalendarDay> serviceDateKeys;

    public UserCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MaterialCalendarView calendarView = binding.calendarView;

        int[] colors = new int[] {
                Color.parseColor("#808AAC"), // event
                Color.parseColor("#FAD609"), // product
                Color.parseColor("#DD79AE")  // service
        };

        // Mark some dates with events
        eventDates = new HashMap<>();
        productDates = new HashMap<>();
        serviceDates = new HashMap<>();
        addEventsForCurrentMonth(calendarView.getCurrentDate());
        eventDateKeys = new HashSet<>(eventDates.keySet());
        productDateKeys = new HashSet<>(productDates.keySet());
        serviceDateKeys = new HashSet<>(serviceDates.keySet());

        // Add a decorators to the calendar
        calendarView.addDecorators(new EventDecorator(colors[0], eventDateKeys),
                new EventDecorator(colors[1], productDateKeys),
                new EventDecorator(colors[2], serviceDateKeys));

        calendarView.setOnMonthChangedListener((widget, date) -> {
            eventDates.clear();
            eventDateKeys.clear();
            productDates.clear();
            productDateKeys.clear();
            serviceDates.clear();
            serviceDateKeys.clear();
            addEventsForCurrentMonth(date);
        });

        binding.calendarView.setOnDateLongClickListener((widget, date) -> {
            StringBuilder message = new StringBuilder();

            for (CalendarDay day : eventDates.keySet()) {
                if (date.equals(day)) {
                    message.append("Event: ").append(eventDates.get(day)).append("\n");
                }
            }

            for (CalendarDay day : productDates.keySet()) {
                if (date.equals(day)) {
                    message.append("Product: ").append(productDates.get(day)).append("\n");
                }
            }

            for (CalendarDay day : serviceDates.keySet()) {
                if (date.equals(day)) {
                    message.append("Service: ").append(serviceDates.get(day)).append("\n");
                }
            }

            if (message.length() == 0) {
                message = new StringBuilder("There is nothing important on this day!");
            }
            else {
                message = new StringBuilder(message.substring(0, message.length() - 1));
            }

            Toast.makeText(getContext(), message.toString(), Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    public static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, HashSet<CalendarDay> dates) {
            this.color = color;
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10f, color)); // DotSpan for marking events
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addEventsForCurrentMonth(CalendarDay currentMonth) {
        LocalDate date = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), currentMonth.getDay());

        // Get the first day of the month
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);

        // Get the last day of the month
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        Call<CalendarOccupancy[]> call = ClientUtils.userService.getMyCalendar(
                LoggedInHelperService.getId(),
                firstDayOfMonth,
                lastDayOfMonth
        );
        call.enqueue(new Callback<CalendarOccupancy[]>() {
            @Override
            public void onResponse(Call<CalendarOccupancy[]> call, Response<CalendarOccupancy[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for(CalendarOccupancy occupancy : response.body()) {
                        if(occupancy.getOccupancyType() == OccupancyType.EVENT) {
                            LocalDate iterDate = occupancy.getOccupationStartDate();

                            while (!iterDate.isAfter(occupancy.getOccupationEndDate())) {
                                eventDates.put(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()),
                                        occupancy.getTitle());
                                eventDateKeys.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate = iterDate.plusDays(1);
                            }
                        }
                        else if(occupancy.getOccupancyType() == OccupancyType.PRODUCT) {
                            LocalDate iterDate = occupancy.getOccupationStartDate();

                            while (!iterDate.isAfter(occupancy.getOccupationEndDate())) {
                                productDates.put(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()),
                                        occupancy.getTitle());
                                productDateKeys.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate = iterDate.plusDays(1);
                            }
                        }
                        else {
                            LocalDate iterDate = occupancy.getOccupationStartDate();

                            while (!iterDate.isAfter(occupancy.getOccupationEndDate())) {
                                serviceDates.put(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()),
                                        occupancy.getTitle());
                                serviceDateKeys.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate = iterDate.plusDays(1);
                            }
                        }
                    }

                    binding.calendarView.invalidateDecorators();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting your calendar!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<CalendarOccupancy[]> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting your calendar!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }
}