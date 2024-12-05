package com.example.eventy.services;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.eventy.R;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.databinding.FragmentServiceReservationSelectEventBinding;
import com.example.eventy.events.SelectEventFragment;
import com.example.eventy.model.event.Event;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReservationSelectEventFragment extends Fragment implements MultiSpinner.MultiSpinnerListener  {
    private FragmentServiceReservationSelectEventBinding binding;
    // event & service
    private Event selectedEvent = null;
    private Button dateRangeButton;
    private TextView showSelectedDateText;

    public ReservationSelectEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceReservationSelectEventBinding.inflate(inflater, container, false);

        /*
        // Get screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        TextView selectEventText = binding.selectEventText;
        LinearLayout searchFilterContainer = binding.searchAndFilterContainer;
        LinearLayout filterSortContainer = binding.filterAndSortContainer;

        selectEventText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        searchFilterContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        filterSortContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int selectEventTextHeight = selectEventText.getMeasuredHeight();
        int searchFilterContainerHeight = searchFilterContainer.getMeasuredHeight();
        int filterSortContainerHeight = filterSortContainer.getMeasuredHeight();

        int newHeight = screenHeight - selectEventTextHeight - searchFilterContainerHeight - filterSortContainerHeight;

        LinearLayout containerLayout = binding.container;
        ViewGroup.LayoutParams params = containerLayout.getLayoutParams();
        params.height = newHeight;
        containerLayout.setLayoutParams(params);
*/
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, new SelectEventFragment(this))
                .commit();

        setupEventSearch();
        setupEventFilters();
        setupEventSort();

        return binding.getRoot();
    }

    private void setupEventSearch() {
        //eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        //SearchView searchView = binding.searchInput;
        //eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);
    }

    private void setupEventFilters() {
        binding.filterButton.setOnClickListener(v -> {
            Toast.makeText(this.getContext(), "Event Filter button clicked!", Toast.LENGTH_SHORT).show();

            BottomSheetDialog bottomSheetDialog = loadAndGetEventBottomSheetFilterDialog();

            setupEventFilterEventTypes(bottomSheetDialog);
            setupEventFilterLocation(bottomSheetDialog);
            setupEventFilterDay(bottomSheetDialog);

            showSelectedDateText = bottomSheetDialog.findViewById(R.id.event_show_selected_date);

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = buildAndGetEventMaterialDatePicker();
            setupEventFilterDateSelection(bottomSheetDialog, materialDatePicker);

            setupEventConfirmFilter(bottomSheetDialog);
        });
    }

    private void setupEventConfirmFilter(BottomSheetDialog bottomSheetDialog) {
        Button closeButton = bottomSheetDialog.findViewById(R.id.events_confirm_button);
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    @NonNull
    private BottomSheetDialog loadAndGetEventBottomSheetFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_home_events_filter, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        return bottomSheetDialog;
    }

    private void setupEventFilterEventTypes(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner eventTypeMultiSpinner = bottomSheetDialog.findViewById(R.id.event_type_filter);

        ArrayList<String> eventTypes = new ArrayList<>();
        eventTypes.add("Wedding"); eventTypes.add("Sport"); eventTypes.add("Conference");
        eventTypes.add("Party"); eventTypes.add("Prom"); eventTypes.add("Big party");
        eventTypeMultiSpinner.setItems(eventTypes, "-", this, "Event types");
    }

    private void setupEventFilterLocation(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner locationMultiSpinner = bottomSheetDialog.findViewById(R.id.location_filter);

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Belgrade");locations.add("Gradiška");locations.add("New York");
        locations.add("Paris");locations.add("Kuala Lumpur");locations.add("Banja Luka");
        locationMultiSpinner.setItems(locations, "-", this, "Locations");
    }

    private void setupEventFilterDay(BottomSheetDialog bottomSheetDialog) {
        Spinner daySpinner = bottomSheetDialog.findViewById(R.id.event_day_filter);

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

        Button dateRangeButton = bottomSheetDialog.findViewById(R.id.date_range_filter);
        dateRangeButton.setEnabled(false);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) {
                    dateRangeButton.setEnabled(true);
                } else {
                    dateRangeButton.setEnabled(false);
                    dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                    showSelectedDateText.setText("No date selected");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dateRangeButton.setEnabled(false);
                dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                showSelectedDateText.setText("No date selected");
            }
        });
    }

    @NonNull
    private static MaterialDatePicker<Pair<Long, Long>> buildAndGetEventMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        return materialDatePicker;
    }

    private void setupEventFilterDateSelection(BottomSheetDialog bottomSheetDialog, MaterialDatePicker<Pair<Long, Long>> materialDatePicker) {
        dateRangeButton = bottomSheetDialog.findViewById(R.id.date_range_filter);
        dateRangeButton.setOnClickListener(v1 ->
                materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER")
        );

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString.equals(endDateString) ? startDateString : startDateString + " - " + endDateString;
            dateRangeButton.setText(selectedDateRange);

            showSelectedDateText = bottomSheetDialog.findViewById(R.id.event_show_selected_date);
            showSelectedDateText.setText(startDateString.equals(endDateString) ? "Selected date is: " + selectedDateRange : "Selected dates are: " + selectedDateRange);
        });
    }

    private void setupEventSort() {
        Spinner spinner = binding.sortButton;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.event_sort_options));

        // Specify the layout to use when the list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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

    @Override
    public void onItemsSelected(boolean[] selected) {
        // Handle the selected items here
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                Log.d("MultiSpinner", "Item " + (i + 1) + " is selected");
            }
        }
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
}
