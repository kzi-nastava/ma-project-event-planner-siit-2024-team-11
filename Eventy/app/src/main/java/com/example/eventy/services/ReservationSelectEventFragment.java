package com.example.eventy.services;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentServiceReservationSelectEventBinding;
import com.example.eventy.events.SelectEventFragment;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.home.events.filters.EventFilterBottomSheetFragment;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.utils.ClientUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationSelectEventFragment extends Fragment implements EventFilterBottomSheetFragment.FilterListener {
    private FragmentServiceReservationSelectEventBinding binding;
    private SelectEventFragment selectEventFragment;
    private EventFilters eventFilters;
    private SolutionCard selectedServiceCard;
    private ArrayList<String> eventTypesEvents = new ArrayList<>();
    private ArrayList<String> locationsEvents = new ArrayList<>();
    private boolean isEventFilterOpened = false;
    private boolean areEventTypesEventsLoading = false;
    private boolean areLocationsEventsLoading = false;
    private boolean isServiceLoading = false; // obrisati kasnije!!

    public ReservationSelectEventFragment() {
        eventFilters = new EventFilters("", "-", new ArrayList<String>(), null, null, null);
        //this.selectedServiceCard = selectedServiceCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServiceReservationSelectEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadMockService();

        loadInitialItems();

        setupEventSearch();
        setupEventFilters();
        setupEventSort();

        AppCompatButton continueButton = binding.confirmReservationButton;
        continueButton.setOnClickListener(v1 -> {
            EventCard selectedEventCard = selectEventFragment.getSelectedEventCard();
            if (selectedEventCard == null) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(this.getActivity(), "Event Not Selected", "Please choose an event to proceed to the next step.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            } else {
                getChildFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new ReservationFragment(selectedEventCard, selectedServiceCard))
                    .addToBackStack(null)
                    .commit();
            }
        });

        return root;
    }

    private void loadMockService() {
        if (isServiceLoading) return;
        isServiceLoading = true;

        Call<SolutionCard> call = ClientUtils.serviceService.getServiceCard(6L);
        call.enqueue(new Callback<SolutionCard>() {
            @Override
            public void onResponse(Call<SolutionCard> call, Response<SolutionCard> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    selectedServiceCard = response.body();

                } else {
                    showErrorDialog("Error while loading selected service!");
                    showErrorDialog(response.message());
                }
                isServiceLoading = false;
            }

            @Override
            public void onFailure(Call<SolutionCard> call, Throwable t) {
                showErrorDialog("Error while loading selected service!");
                showErrorDialog(t.getMessage());
                isServiceLoading = false;
            }
        });
    }

    private void loadInitialItems() {
        SelectEventFragment fragmentSelectEvent = new SelectEventFragment(eventFilters);
        this.selectEventFragment = fragmentSelectEvent;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_items, fragmentSelectEvent)
                .commit();
    }

    private void setupEventSearch() {
        SearchView searchView = binding.searchInput;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                selectEventFragment.updateSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectEventFragment.updateSearch(newText);
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

                selectEventFragment.updateSort(selectedSort);
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

        selectEventFragment.updateFilters(filterValues);
    }

    @Override
    public void onFiltersClosed() {
        isEventFilterOpened = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
