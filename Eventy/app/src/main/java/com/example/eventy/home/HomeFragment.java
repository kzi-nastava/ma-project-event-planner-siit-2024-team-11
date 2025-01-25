package com.example.eventy.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentHomeBinding;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.home.events.EventsFragment;
import com.example.eventy.home.events.featured_events.FeaturedEventsFragment;
import com.example.eventy.home.events.featured_events.FeaturedEventsTitleFragment;
import com.example.eventy.home.events.filters.EventFilterBottomSheetFragment;
import com.example.eventy.home.solutions.SolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsTitleFragment;

import com.example.eventy.home.solutions.filters.SolutionFilterBottomSheetFragment;
import com.example.eventy.solutions.model.SolutionsFilter;
import com.example.eventy.utils.ClientUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements EventFilterBottomSheetFragment.FilterListener,
                                                      SolutionFilterBottomSheetFragment.FilterListener {
    private FragmentHomeBinding binding;

    private EventsFragment eventsFragment;
    private EventFilters eventFilters;
    private ArrayList<String> eventTypesEvents = new ArrayList<>();
    private ArrayList<String> locationsEvents = new ArrayList<>();
    private boolean isEventFilterOpened = false;
    private boolean areEventTypesEventsLoading = false;
    private boolean areLocationsEventsLoading = false;

    private SolutionsFragment solutionsFragment;
    private SolutionsFilter solutionsFilter;
    private ArrayList<String> eventTypesSolutions = new ArrayList<>();
    private ArrayList<String> categoriesSolutions  = new ArrayList<>();
    private ArrayList<String> companiesSolutions  = new ArrayList<>();
    private boolean isSolutionFilterOpened = false;
    private boolean areEventTypesSolutionsLoading = false;
    private boolean areCategoriesSolutionsLoading = false;
    private boolean areCompaniesSolutionsLoading = false;

    public HomeFragment() {
        eventFilters = new EventFilters("", "-", new ArrayList<String>(), null, null, null);
        solutionsFilter = new SolutionsFilter("", "Any", new ArrayList<String>(), new ArrayList<String>(), "-", null, null,  null, null, true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        EventsFragment fragmentEvents = new EventsFragment(eventFilters);
        this.eventsFragment = fragmentEvents;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentEvents)
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
        EventsFragment fragmentEvents = new EventsFragment(eventFilters);
        this.eventsFragment = fragmentEvents;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentEvents)
                .addToBackStack(null)
                .commit();
    }

    private void setupEventSearch() {
        SearchView searchView = binding.searchInput;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                eventsFragment.updateSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventsFragment.updateSearch(newText);
                return true;
            }
        });
    }

    private void setupEventFilters() {
        binding.filterButton.setOnClickListener(v -> {
            if (isEventFilterOpened) {
                return;
            }
            isEventFilterOpened = true;

            if (isAdded()) {
                loadEventTypesEvents();
                loadLocationsEvents();
            }
            EventFilterBottomSheetFragment bottomSheetFragment = new EventFilterBottomSheetFragment(eventTypesEvents, locationsEvents);

            Bundle args = new Bundle();
            args.putString("location", eventFilters.getSelectedLocation());
            args.putStringArrayList("eventTypes", eventFilters.getSelectedEventTypes());
            args.putString("maxParticipants", eventFilters.getMaxParticipants());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
            String dateRangeSummary = (eventFilters.getSelectedStartDateTime() != null && eventFilters.getSelectedEndDateTime() != null)
                    ? (eventFilters.getSelectedStartDateTime().format(formatter) + " - " +  eventFilters.getSelectedEndDateTime().format(formatter))
                    : "Not selected";
            args.putString("dateRange", dateRangeSummary);
            bottomSheetFragment.setArguments(args);

            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void loadEventTypesEvents() {
        if (areEventTypesEventsLoading) return;
        areEventTypesEventsLoading = true;

        Call<String[]> call = ClientUtils.eventService.getAllUniqueEventTypesForEvents();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    String[] eventTypeNames = response.body();
                    eventTypesEvents.clear();
                    eventTypesEvents.addAll(Arrays.asList(eventTypeNames));
                } else {
                    showErrorDialog("Error while loading event types!");
                    showErrorDialog(response.message());
                }
                areEventTypesEventsLoading = false;
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                showErrorDialog("Error while loading event types!");
                showErrorDialog(t.getMessage());
                areEventTypesEventsLoading = false;
            }
        });
    }

    private void loadLocationsEvents() {
        if (areLocationsEventsLoading) return;
        areLocationsEventsLoading = true;

        Call<String[]> call = ClientUtils.eventService.getAllUniqueLocationsForEvents();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    String[] locationNames = response.body();
                    locationsEvents.clear();
                    locationsEvents.addAll(Arrays.asList(locationNames));
                } else {
                    showErrorDialog("Error while loading locations!");
                    showErrorDialog(response.message());
                }
                areLocationsEventsLoading = false;
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                showErrorDialog("Error while loading locations!");
                showErrorDialog(t.getMessage());
                areLocationsEventsLoading = false;
            }
        });
    }

    private void setupEventSort() {
        Spinner spinner = binding.sortButton;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_sort_options));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortValue = arrayAdapter.getItem(position);
                String selectedSort = "type";
                switch (sortValue) {
                    case "Event Type": selectedSort = "type"; break;
                    case "Name": selectedSort = "name"; break;
                    case "Max Participants ASC": selectedSort = "maxNumberParticipants,asc"; break;
                    case "Max Participants DESC": selectedSort = "maxNumberParticipants,desc"; break;
                    case "Location": selectedSort = "location"; break;
                    case "Date ASC": selectedSort = "date,asc"; break;
                    case "Date DESC": selectedSort = "date,desc"; break;
                }

                eventsFragment.updateSort(selectedSort);
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
        SolutionsFragment fragmentSolutions = new SolutionsFragment(solutionsFilter);
        this.solutionsFragment = fragmentSolutions;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentSolutions)
                .addToBackStack(null)
                .commit();
    }

    private void setupSolutionSearch() {
        SearchView searchView = binding.searchInput;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                solutionsFragment.updateSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                solutionsFragment.updateSearch(newText);
                return true;
            }
        });
    }

    private void setupSolutionFilters() {
        binding.filterButton.setOnClickListener(v -> {
            if (isSolutionFilterOpened) {
                return;
            }
            isSolutionFilterOpened = true;

            if (isAdded()) {
                loadEventTypesSolutions();
                loadCategoriesSolutions();
                loadCompaniesSolutions();
            }
            SolutionFilterBottomSheetFragment bottomSheetFragment = new SolutionFilterBottomSheetFragment(eventTypesSolutions, categoriesSolutions, companiesSolutions);

            Bundle args = new Bundle();
            args.putString("type", solutionsFilter.getType());
            args.putStringArrayList("eventTypes", solutionsFilter.getEventTypes());
            args.putStringArrayList("categories", solutionsFilter.getCategories());
            args.putString("company", solutionsFilter.getCompany());
            args.putString("minPrice", solutionsFilter.getMinPrice());
            args.putString("maxPrice", solutionsFilter.getMaxPrice());
            args.putBoolean("available", solutionsFilter.getAvailable());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
            String dateRangeSummary = (solutionsFilter.getStartDate() != null && solutionsFilter.getEndDate() != null)
                    ? (solutionsFilter.getStartDate().format(formatter) + " - " +  solutionsFilter.getEndDate().format(formatter))
                    : "Not selected";
            args.putString("dateRange", dateRangeSummary);
            bottomSheetFragment.setArguments(args);

            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void loadEventTypesSolutions() {
        if (areEventTypesSolutionsLoading) return;
        areEventTypesSolutionsLoading = true;

        Call<String[]> call = ClientUtils.solutionService.getAllUniqueEventTypesForSolutions();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    String[] eventTypeNames = response.body();
                    eventTypesSolutions.clear();
                    eventTypesSolutions.addAll(Arrays.asList(eventTypeNames));
                } else {
                    showErrorDialog("Error while loading event types!");
                    showErrorDialog(response.message());
                }
                areEventTypesSolutionsLoading = false;
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                showErrorDialog("Error while loading event types!");
                showErrorDialog(t.getMessage());
                areEventTypesSolutionsLoading = false;
            }
        });
    }

    private void loadCategoriesSolutions() {
        if (areCategoriesSolutionsLoading) return;
        areCategoriesSolutionsLoading = true;

        Call<String[]> call = ClientUtils.solutionService.getAllUniqueCategoriesForSolutions();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    String[] categoryNames = response.body();
                    categoriesSolutions.clear();
                    categoriesSolutions.addAll(Arrays.asList(categoryNames));
                } else {
                    showErrorDialog("Error while loading categories!");
                    showErrorDialog(response.message());
                }
                areCategoriesSolutionsLoading = false;
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                showErrorDialog("Error while loading categories!");
                showErrorDialog(t.getMessage());
                areCategoriesSolutionsLoading = false;
            }
        });
    }

    private void loadCompaniesSolutions() {
        if (areCompaniesSolutionsLoading) return;
        areCompaniesSolutionsLoading = true;

        Call<String[]> call = ClientUtils.solutionService.getAllUniqueCompaniesForSolutions();
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    String[] companyNames = response.body();
                    companiesSolutions.clear();
                    companiesSolutions.addAll(Arrays.asList(companyNames));
                } else {
                    showErrorDialog("Error while loading companies!");
                    showErrorDialog(response.message());
                }
                areCompaniesSolutionsLoading = false;
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                showErrorDialog("Error while loading companies!");
                showErrorDialog(t.getMessage());
                areCompaniesSolutionsLoading = false;
            }
        });
    }

    private void setupSolutionSort() {
        Spinner spinner = binding.sortButton;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.solution_sort_options));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortValue = arrayAdapter.getItem(position);
                String selectedSort = "category";
                switch (sortValue) {
                    case "Category": selectedSort = "category,asc"; break;
                    case "Name": selectedSort = "name,asc"; break;
                    case "Price ASC": selectedSort = "price,asc"; break;
                    case "Price DESC": selectedSort = "price,desc"; break;
                }

                solutionsFragment.updateSort(selectedSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    @Override
    public void onFiltersSelected(EventFilters filterValues) {
        isEventFilterOpened = false;
        this.eventFilters = filterValues;

        eventsFragment.updateFilters(filterValues);
    }

    @Override
    public void onFiltersSelected(SolutionsFilter filterValues) {
        isSolutionFilterOpened = false;
        this.solutionsFilter = filterValues;

        solutionsFragment.updateFilters(filterValues);
    }

    @Override
    public void onFiltersClosed() {
        isEventFilterOpened = false;
        isSolutionFilterOpened = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}