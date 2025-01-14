package com.example.eventy.users.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCalendarFragment extends Fragment {

    private FragmentUserCalendarBinding binding;
    private HashSet<CalendarDay> eventDates;
    private HashSet<CalendarDay> productDates;
    private HashSet<CalendarDay> serviceDates;

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
        eventDates = new HashSet<>();
        productDates = new HashSet<>();
        serviceDates = new HashSet<>();
        addEventsForCurrentMonth(calendarView.getCurrentDate());

        // Add a decorators to the calendar
        calendarView.addDecorators(new EventDecorator(colors[0], eventDates), new EventDecorator(colors[1], productDates), new EventDecorator(colors[2], serviceDates));

        calendarView.setOnMonthChangedListener((widget, date) -> {
            eventDates.clear();
            productDates.clear();
            serviceDates.clear();
            addEventsForCurrentMonth(date);
            calendarView.invalidateDecorators();
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
                                eventDates.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate.plusDays(1);
                            }
                        }
                        else if(occupancy.getOccupancyType() == OccupancyType.PRODUCT) {
                            LocalDate iterDate = occupancy.getOccupationStartDate();

                            while (!iterDate.isAfter(occupancy.getOccupationEndDate())) {
                                productDates.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate.plusDays(1);
                            }
                        }
                        else {
                            LocalDate iterDate = occupancy.getOccupationStartDate();

                            while (!iterDate.isAfter(occupancy.getOccupationEndDate())) {
                                serviceDates.add(CalendarDay.from(iterDate.getYear(), iterDate.getMonthValue(), iterDate.getDayOfMonth()));
                                iterDate.plusDays(1);
                            }
                        }
                    }
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