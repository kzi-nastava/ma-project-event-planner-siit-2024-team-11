package com.example.eventy.home;

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
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentHomeBinding;
import com.example.eventy.home.events.EventsFragment;
import com.example.eventy.home.events.EventsViewModel;
import com.example.eventy.home.events.featured_events.FeaturedEventsFragment;
import com.example.eventy.home.events.featured_events.FeaturedEventsTitleFragment;
import com.example.eventy.home.solutions.SolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsTitleFragment;
import com.example.eventy.custom.MultiSpinner;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment implements MultiSpinner.MultiSpinnerListener {
    private FragmentHomeBinding binding;
    private EventsViewModel eventsViewModel;
    // private SolutionsViewModel solutionsViewModel;
    private Button dateRangeButton;
    private TextView showSelectedDateText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadInitialView();

        setupTabEvents();
        setupTabSolutions();

        return root;
    }

    private void loadInitialView() {
        loadInitialTitle();
        loadInitialFeaturedItems();
        loadInitialItems();

        setupEventSearch();
        setupEventFilters();
        setupEventSort();
    }

    private void loadInitialTitle() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, new FeaturedEventsTitleFragment())
                .commit();
    }

    private void loadInitialFeaturedItems() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, new FeaturedEventsFragment())
                .commit();
    }

    private void loadInitialItems() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, new EventsFragment())
                .commit();
    }

    private void setupTabEvents() {
        binding.tabEvent.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            loadEventsTitle();
            loadFeaturedEvents();
            loadEvents();

            setupEventSearch();
            setupEventFilters();
            setupEventSort();
        });
    }

    private void loadEventsTitle() {
        Fragment featuredEventsTitleFragment = new FeaturedEventsTitleFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, featuredEventsTitleFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadFeaturedEvents() {
        Fragment fragmentFeaturedEvents = new FeaturedEventsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, fragmentFeaturedEvents)
                .addToBackStack(null)
                .commit();
    }

    private void loadEvents() {
        Fragment fragmentEvents = new EventsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentEvents)
                .addToBackStack(null)
                .commit();
    }

    private void setupEventSearch() {
        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        SearchView searchView = binding.searchInput;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);
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

    private void setupTabSolutions() {
        binding.tabSolutions.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            loadSolutionsTitle();
            loadFeaturedSolutions();
            loadSolutions();

            setupSolutionSearch();
            setupSolutionFilters();
            setupSolutionSort();
        });
    }

    private void loadSolutionsTitle() {
        Fragment featuredSolutionsTitleFragment = new FeaturedSolutionsTitleFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, featuredSolutionsTitleFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadFeaturedSolutions() {
        Fragment fragmentFeaturedSolutions = new FeaturedSolutionsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, fragmentFeaturedSolutions)
                .addToBackStack(null)
                .commit();
    }

    private void loadSolutions() {
        Fragment fragmentSolutions = new SolutionsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentSolutions)
                .addToBackStack(null)
                .commit();
    }

    private void setupSolutionSearch() {
        // TODO: 11/14/2024
        //eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        //SearchView searchView = binding.searchInput;
        //eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);
    }

    private void setupSolutionFilters() {
        binding.filterButton.setOnClickListener(v -> {
            Toast.makeText(this.getContext(), "Solution Filter button clicked!", Toast.LENGTH_SHORT).show();

            BottomSheetDialog bottomSheetDialog = loadAndGetSolutionBottomSheetFilterDialog();

            setupSolutionFilterType(bottomSheetDialog);
            setupSolutionFilterCategoryType(bottomSheetDialog);
            setupSolutionFilterEventTypes(bottomSheetDialog);
            setupSolutionFilterCompany(bottomSheetDialog);
            setupSolutionFilterDay(bottomSheetDialog);

            showSelectedDateText = bottomSheetDialog.findViewById(R.id.show_selected_date);

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = buildAndGetSolutionMaterialDatePicker();
            setupSolutionFilterDateSelection(bottomSheetDialog, materialDatePicker);

            setupSolutionConfirmFilter(bottomSheetDialog);
        });
    }

    private void setupSolutionConfirmFilter(BottomSheetDialog bottomSheetDialog) {
        Button closeButton = bottomSheetDialog.findViewById(R.id.solutions_confirm_button);
        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    @NonNull
    private BottomSheetDialog loadAndGetSolutionBottomSheetFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_home_solutions_filter, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        return bottomSheetDialog;
    }

    private void setupSolutionFilterType(BottomSheetDialog bottomSheetDialog) {
        Spinner solutionTypeSpinner = bottomSheetDialog.findViewById(R.id.solution_type_filter);

        String[] types = new String[] {
            "-", "Services", "Products"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        solutionTypeSpinner.setAdapter(arrayAdapter);
        solutionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSolutionFilterCategoryType(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner solutionCategoryMultiSpinner = bottomSheetDialog.findViewById(R.id.solution_category_filter);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Food"); categories.add("Music"); categories.add("Catering");
        categories.add("Flowers"); categories.add("Formal attires"); categories.add("Party");
        solutionCategoryMultiSpinner.setItems(categories, "-", this, "Event types");
    }

    private void setupSolutionFilterEventTypes(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner eventTypeMultiSpinner = bottomSheetDialog.findViewById(R.id.solution_event_types_filter);

        ArrayList<String> eventTypes = new ArrayList<>();
        eventTypes.add("Wedding"); eventTypes.add("Sport"); eventTypes.add("Conference");
        eventTypes.add("Party"); eventTypes.add("Prom"); eventTypes.add("Big party");
        eventTypeMultiSpinner.setItems(eventTypes, "-", this, "Event types");
    }

    private void setupSolutionFilterCompany(BottomSheetDialog bottomSheetDialog) {
        String[] companies = new String[] {
            "-", "Beograd DOO", "Gradiška DOO", "New York DOO", "Paris DOO"
        };

        Spinner solutionTypeSpinner = bottomSheetDialog.findViewById(R.id.company_filter);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, companies);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        solutionTypeSpinner.setAdapter(arrayAdapter);
        solutionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setupSolutionFilterDay(BottomSheetDialog bottomSheetDialog) {
        Spinner daySpinner = bottomSheetDialog.findViewById(R.id.day_filter);

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

        Button dateRangeButton = bottomSheetDialog.findViewById(R.id.solution_date_range_filter);
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
    private static MaterialDatePicker<Pair<Long, Long>> buildAndGetSolutionMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        return materialDatePicker;
    }

    private void setupSolutionFilterDateSelection(BottomSheetDialog bottomSheetDialog, MaterialDatePicker<Pair<Long, Long>> materialDatePicker) {
        dateRangeButton = bottomSheetDialog.findViewById(R.id.solution_date_range_filter);
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

            showSelectedDateText = bottomSheetDialog.findViewById(R.id.show_selected_date);
            showSelectedDateText.setText(startDateString.equals(endDateString) ? "Selected date is: " + selectedDateRange : "Selected dates are: " + selectedDateRange);
        });
    }

    private void setupSolutionSort() {
        Spinner spinner = binding.sortButton;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.solution_sort_options));

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