package com.example.eventy.home;

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
    private Button dateRangeButton;
    private TextView showSelectedDateText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LoadInitialView();

        SetupTabEvents();
        SetupTabSolutions();

        SetupSearch();
        SetupFilters();
        SetupSort();

        return root;
    }

    private void LoadInitialView() {
        LoadInitialTitle();
        LoadInitialFeaturedItems();
        LoadInitialItems();
    }

    private void LoadInitialTitle() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, new FeaturedEventsTitleFragment())
                .commit();
    }

    private void LoadInitialFeaturedItems() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, new FeaturedEventsFragment())
                .commit();
    }

    private void LoadInitialItems() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, new EventsFragment())
                .commit();
    }

    private void SetupTabEvents() {
        binding.tabEvent.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            LoadEventsTitle();
            LoadFeaturedEvents();
            LoadEvents();
        });
    }

    private void LoadEventsTitle() {
        Fragment featuredEventsTitleFragment = new FeaturedEventsTitleFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, featuredEventsTitleFragment)
                .addToBackStack(null)
                .commit();
    }

    private void LoadFeaturedEvents() {
        Fragment fragmentFeaturedEvents = new FeaturedEventsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, fragmentFeaturedEvents)
                .addToBackStack(null)
                .commit();
    }

    private void LoadEvents() {
        Fragment fragmentEvents = new EventsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentEvents)
                .addToBackStack(null)
                .commit();
    }

    private void SetupTabSolutions() {
        binding.tabSolutions.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            LoadSolutionsTitle();
            LoadFeaturedSolutions();
            LoadSolutions();
        });
    }

    private void LoadSolutionsTitle() {
        Fragment featuredSolutionsTitleFragment = new FeaturedSolutionsTitleFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, featuredSolutionsTitleFragment)
                .addToBackStack(null)
                .commit();
    }

    private void LoadFeaturedSolutions() {
        Fragment fragmentFeaturedSolutions = new FeaturedSolutionsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, fragmentFeaturedSolutions)
                .addToBackStack(null)
                .commit();
    }

    private void LoadSolutions() {
        Fragment fragmentSolutions = new SolutionsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentSolutions)
                .addToBackStack(null)
                .commit();
    }

    private void SetupSearch() {
        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        SearchView searchView = binding.searchInput;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);
    }

    private void SetupFilters() {
        binding.filterButton.setOnClickListener(v -> {
            Toast.makeText(this.getContext(), "Filter button clicked!", Toast.LENGTH_SHORT).show();

            BottomSheetDialog bottomSheetDialog = LoadAndGetBottomSheetFilterDialog();

            SetupFilterEventTypes(bottomSheetDialog);
            SetupFilterLocation(bottomSheetDialog);

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = BuildAndGetMaterialDatePicker();

            setupFilterDateSelection(bottomSheetDialog, materialDatePicker);
        });
    }

    @NonNull
    private BottomSheetDialog LoadAndGetBottomSheetFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_home_events_filter, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        return bottomSheetDialog;
    }

    private void SetupFilterEventTypes(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner eventTypeMultiSpinner = bottomSheetDialog.findViewById(R.id.event_type_filter);

        ArrayList<String> eventTypes = new ArrayList<>();
        eventTypes.add("wedding"); eventTypes.add("sport"); eventTypes.add("conference");
        eventTypes.add("party"); eventTypes.add("prom"); eventTypes.add("big party");
        eventTypeMultiSpinner.setItems(eventTypes, "-", this, "Event types");
    }

    private void SetupFilterLocation(BottomSheetDialog bottomSheetDialog) {
        MultiSpinner locationMultiSpinner = bottomSheetDialog.findViewById(R.id.location_filter);

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Belgrade");locations.add("Gradiška");locations.add("New York");
        locations.add("Paris");locations.add("Kuala Lumpur");locations.add("Banja Luka");
        locationMultiSpinner.setItems(locations, "-", this, "Locations");
    }

    @NonNull
    private static MaterialDatePicker<Pair<Long, Long>> BuildAndGetMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        return materialDatePicker;
    }

    private void setupFilterDateSelection(BottomSheetDialog bottomSheetDialog, MaterialDatePicker<Pair<Long, Long>> materialDatePicker) {
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

            showSelectedDateText = bottomSheetDialog.findViewById(R.id.show_selected_date);
            showSelectedDateText.setText(startDateString.equals(endDateString) ? "Selected date is : " + selectedDateRange : "Selected date range is : " + selectedDateRange);
        });
    }

    private void SetupSort() {
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