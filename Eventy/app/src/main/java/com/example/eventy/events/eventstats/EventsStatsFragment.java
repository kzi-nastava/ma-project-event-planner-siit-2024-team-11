package com.example.eventy.events.eventstats;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.eventy.R;
import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventsStatsBinding;
import com.example.eventy.databinding.FragmentHomeEventsBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.home.events.filters.EventFilterBottomSheetFragment;
import com.example.eventy.utils.ClientUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsStatsFragment extends Fragment {
    private FragmentEventsStatsBinding binding;
    private EventsAdapter eventsAdapter;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 99;
    private String sort = "type";
    private String search = "";
    private EventFilters eventsFilters;
    private ArrayList<EventCard> paginatedEvents;
    private boolean isLoading = false;
    private ArrayList<String> eventTypesEvents = new ArrayList<>();
    private ArrayList<String> locationsEvents = new ArrayList<>();
    private boolean isEventFilterOpened = false;
    private boolean areEventTypesEventsLoading = false;
    private boolean areLocationsEventsLoading = false;

    public EventsStatsFragment() {
        eventsFilters = new EventFilters("", "-", new ArrayList<String>(), null, null, null);
        setupEventSearch();
        setupEventFilters();
        setupEventSort();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventsStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.paginatedEvents = new ArrayList<>();
        setupRecyclerView();
        setupPaginationControls();
        fetchEvents(search, eventsFilters, page, pageSize, sort);
    }

    private void setupRecyclerView() {
        eventsAdapter = new EventsAdapter(requireContext(), paginatedEvents);
        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsAdapter);
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                fetchEvents(search, eventsFilters, page, pageSize, sort);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                fetchEvents(search, eventsFilters, page, pageSize, sort);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_options,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(2);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                page = 0;
                fetchEvents(search, eventsFilters, page, pageSize, sort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void fetchEvents(String search, EventFilters eventFilters, int page, int pageSize, String sort) {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Integer maxParticipants;
        try {
            maxParticipants = Integer.parseInt(eventFilters.getMaxParticipants());
        } catch (Exception ignored) {
            maxParticipants = null;
        }
        String location = eventFilters.getSelectedLocation().equals("-") ? null : eventFilters.getSelectedLocation();

        Call<PagedResponse<EventCard>> call = ClientUtils.eventService.getEvents(
                search, eventFilters.getSelectedEventTypes(), maxParticipants, location,
                eventFilters.getSelectedStartDateTime(), eventFilters.getSelectedEndDateTime(),
                page, pageSize, sort
        );
        call.enqueue(new Callback<PagedResponse<EventCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<EventCard>> call, Response<PagedResponse<EventCard>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<EventCard> pagedResponse = response.body();

                    paginatedEvents.clear();
                    paginatedEvents.addAll(pagedResponse.getContent());
                    eventsAdapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();

                } else {
                    showErrorDialog("Error while loading events!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<EventCard>> call, Throwable t) {
                showErrorDialog("Error while loading events!");
                showErrorDialog(t.getMessage());
                isLoading = false;
            }
        });
    }

    private void updatePaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setEnabled(page > 0);
        binding.btnNext.setEnabled(page < totalPages - 1);

        binding.tvPageInfo.setText(String.format("Page %d of %d", page + 1, totalPages));
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    public void updateFilters(EventFilters eventFilters) {
        if (!isAdded()) {
            return;
        }

        this.eventsFilters = eventFilters;

        page = 0;
        pageSize = 5;
        binding.spinnerPageSize.setSelection(2);

        fetchEvents(search, eventsFilters, page, pageSize, sort);
    }

    public void updateSort(String selectedSort) {
        sort = selectedSort;
        fetchEvents(search, eventsFilters, page, pageSize, sort);
    }

    public void updateSearch(String searchValue) {
        search = searchValue;
        fetchEvents(search, eventsFilters, page, pageSize, sort);
    }

    private void setupEventSearch() {
        SearchView searchView = binding.searchInput;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearch(newText);
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
            args.putString("location", eventsFilters.getSelectedLocation());
            args.putStringArrayList("eventTypes", eventsFilters.getSelectedEventTypes());
            args.putString("maxParticipants", eventsFilters.getMaxParticipants());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
            String dateRangeSummary = (eventsFilters.getSelectedStartDateTime() != null && eventsFilters.getSelectedEndDateTime() != null)
                    ? (eventsFilters.getSelectedStartDateTime().format(formatter) + " - " +  eventsFilters.getSelectedEndDateTime().format(formatter))
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

                updateSort(selectedSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}