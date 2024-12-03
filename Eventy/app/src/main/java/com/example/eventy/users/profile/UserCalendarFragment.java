package com.example.eventy.users.profile;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentUserCalendarBinding;
import com.example.eventy.databinding.FragmentUserMyProfilePageBinding;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;

public class UserCalendarFragment extends Fragment {

    private FragmentUserCalendarBinding binding;

    public UserCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MaterialCalendarView calendarView = binding.calendarView;

        // Mark some dates with events
        HashSet<CalendarDay> eventDates = new HashSet<>();
        eventDates.add(CalendarDay.from(2024, 12, 5));
        eventDates.add(CalendarDay.from(2024, 12, 15));
        eventDates.add(CalendarDay.from(2024, 12, 20));

        // Add a decorator to the calendar
        calendarView.addDecorator(new EventDecorator(Color.RED, eventDates));

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
}