package com.example.eventy.users;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.example.eventy.R;
import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.databinding.FragmentOrganizerEventsBinding;
import com.example.eventy.home.events.EventsViewModel;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.utils.Location;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrganizerEventsFragment extends Fragment implements MultiSpinner.MultiSpinnerListener {
    private FragmentOrganizerEventsBinding binding;
    private EventsAdapter eventsAdapter;
    private EventsViewModel eventsViewModel;
    // private SolutionsViewModel solutionsViewModel;
    private Button dateRangeButton;
    private TextView showSelectedDateText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrganizerEventsBinding.inflate(inflater, container, false);

        setupTabSolutions();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Event> events = getEvents();

        eventsAdapter = new EventsAdapter(requireContext(), events);

        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsAdapter);
    }

    @NonNull
    private static ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<>();

        // event types
        EventType weddingType = new EventType("Wedding", "An unforgettable celebration of love and commitment", true);
        EventType conferenceType = new EventType("Conference", "A professional event for exchanging ideas and insights", true);
        EventType concertType = new EventType("Concert", "A thrilling live performance of music", true);
        EventType partyType = new EventType("Party", "An entertaining social gathering filled with fun", true);
        EventType meetingType = new EventType("Meeting", "A strategic assembly to align goals and plans", true);
        EventType workshopType = new EventType("Workshop", "An interactive session focused on skill development", true);
        EventType seminarType = new EventType("Seminar", "An educational event with expert speakers", true);
        EventType festivalType = new EventType("Festival", "A lively celebration with cultural activities", true);
        EventType sportsEventType = new EventType("Sports Event", "A competition showcasing athletic talent", true);
        EventType charityEventType = new EventType("Charity Event", "A fundraising event for a noble cause", true);

        // locations
        Location weddingLocation = new Location("Grand Hall", "123 Wedding St, Cityville", 40.7128, -74.0060);
        Location conferenceLocation = new Location("Tech Center", "456 Innovation Blvd, Tech City", 37.7749, -122.4194);
        Location concertLocation = new Location("Stadium Arena", "789 Music Ave, Townsville", 34.0522, -118.2437);
        Location partyLocation = new Location("Luxe Lounge", "101 Party Ln, Fun Town", 51.5074, -0.1278);
        Location meetingLocation = new Location("Boardroom", "200 Corporate Rd, Business Park", 42.3601, -71.0589);
        Location workshopLocation = new Location("SkillSpace", "321 Learning Dr, Workshop City", 45.4215, -75.6972);
        Location seminarLocation = new Location("Knowledge Auditorium", "555 Wisdom Blvd, Studyville", 34.0522, -118.2437);
        Location festivalLocation = new Location("Festival Grounds", "789 Culture St, Festive Town", 55.7558, 37.6173);
        Location sportsEventLocation = new Location("Olympic Stadium", "101 Sport Ave, Game City", 48.8566, 2.3522);
        Location charityEventLocation = new Location("Community Center", "500 Giving Ln, Charity Village", 40.7306, -73.9352);

        // add to events
        events.add(new Event("Mark & Jana's Wedding", "An unforgettable celebration of love and commitment", 200, PrivacyType.PUBLIC, new Date(), weddingLocation, weddingType));
        events.add(new Event("Tech Conference", "A tech conference with industry leaders", 500, PrivacyType.PRIVATE, new Date(), conferenceLocation, conferenceType));
        events.add(new Event("Summer Music Concert", "Enjoy the best live music performances", 1000, PrivacyType.PUBLIC, new Date(), concertLocation, concertType));
        events.add(new Event("VIP PartyLounge", "An exclusive party for select guests", 100, PrivacyType.PRIVATE, new Date(), partyLocation, partyType));
        events.add(new Event("Business Meeting", "Discussing the upcoming quarter's goals", 30, PrivacyType.PUBLIC, new Date(), meetingLocation, meetingType));
        events.add(new Event("Art of Coding Workshop", "A hands-on coding workshop for developers", 50, PrivacyType.PUBLIC, new Date(), workshopLocation, workshopType));
        events.add(new Event("Science & Health Seminar", "A seminar exploring the latest in science and health", 300, PrivacyType.PRIVATE, new Date(), seminarLocation, seminarType));
        events.add(new Event("Cultural Fest 2024", "A celebration of cultures with music, food, and art", 1500, PrivacyType.PUBLIC, new Date(), festivalLocation, festivalType));
        events.add(new Event("Championship Finals", "The most exciting sports event of the season", 5000, PrivacyType.PUBLIC, new Date(), sportsEventLocation, sportsEventType));
        events.add(new Event("Hope Foundation Gala", "A charity gala supporting local communities", 200, PrivacyType.PRIVATE, new Date(), charityEventLocation, charityEventType));

        return events;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupTabSolutions() {
        setupEventSearch();
        setupEventFilters();
        setupEventSort();
    }

    private void setupEventSearch() {
        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        SearchView searchView = binding.searchInput;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);
    }

    @NonNull
    private BottomSheetDialog loadAndGetEventBottomSheetFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_home_events_filter, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        return bottomSheetDialog;
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
    public void onItemsSelected(boolean[] selected) {
        // Handle the selected items here
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                Log.d("MultiSpinner", "Item " + (i + 1) + " is selected");
            }
        }
    }
}