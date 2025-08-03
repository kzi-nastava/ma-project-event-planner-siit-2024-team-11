package com.example.eventy.products;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.custom.CreateReviewDialog;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.custom.ValidOkDialog;
import com.example.eventy.databinding.FragmentProductPurchaseBinding;
import com.example.eventy.databinding.FragmentServiceReservationSelectEventBinding;
import com.example.eventy.events.SelectEventFragment;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.home.events.filters.EventFilterBottomSheetFragment;
import com.example.eventy.products.model.Purchase;
import com.example.eventy.reviews.model.CreateReview;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductPurchaseFragment extends Fragment implements EventFilterBottomSheetFragment.FilterListener {
    private FragmentProductPurchaseBinding binding;
    private SelectEventFragment selectEventFragment;
    private EventFilters eventFilters;
    private ArrayList<String> eventTypesEvents = new ArrayList<>();
    private ArrayList<String> locationsEvents = new ArrayList<>();
    private boolean isEventFilterOpened = false;
    private boolean areEventTypesEventsLoading = false;
    private boolean areLocationsEventsLoading = false;
    private long productId = -1L;
    private String productName = "";
    private boolean isServiceLoading = false; // obrisati kasnije!!

    public ProductPurchaseFragment() {
        eventFilters = new EventFilters("", "-", new ArrayList<String>(), null, null, null);
        //this.selectedServiceCard = selectedServiceCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductPurchaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() == null) {

        } else {
            this.productId = getArguments().getLong("productId");
            this.productName = getArguments().getString("productName");
            loadInitialItems();
            setupEventSearch();
            setupEventFilters();
            setupEventSort();

            AppCompatButton continueButton = binding.confirmPurchaseButton;
            continueButton.setOnClickListener(v1 -> {
                EventCard selectedEventCard = selectEventFragment.getSelectedEventCard();
                if (selectedEventCard == null) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(this.getActivity(), "Event Not Selected", "Please choose an event for which to purchase this product");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure you want to buy " + productName + " for the event " + selectedEventCard.getName() + "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<Void> call = ClientUtils.productService.purchase(new Purchase(productId, selectedEventCard.getEventId()));
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                ValidOkDialog validOkDialog = new ValidOkDialog(getActivity(), "Purchase Successful", "Your purchase was successful!");
                                                validOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                validOkDialog.setOnDismissListener(dialog -> {
                                                    handleReviewService(container);
                                                });
                                                validOkDialog.show();
                                            } else {
                                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error validating purchase", "Your purchase could not be carried through");
                                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                errorOkDialog.show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            errorOkDialog.show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    dialog.show();
                }
            });
            binding.cancelButton.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putLong("solutionId", this.productId);
                NavController navController = Navigation.findNavController(getView());
                navController.popBackStack();
                navController.navigate(R.id.nav_solution_details, args);
            });
        }


        return root;
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

    private void handleReviewService(ViewGroup container) {
        Call<Boolean> call = ClientUtils.reviewService.isSolutionReviewedByUser(LoggedInHelperService.getId(), productId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    if (isAdded() && getActivity() != null) {
                        Boolean isReviewed = response.body();

                        if (!isReviewed) {
                            CreateReview createReview = new CreateReview(
                                    LoggedInHelperService.getId(),
                                    productId,
                                    null,
                                    null,
                                    null
                            );

                            CreateReviewDialog createReviewDialog = new CreateReviewDialog(getActivity(), "\"" + productName + "\"", "Please rate the product you purchased!", createReview);
                            createReviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            createReviewDialog.setCanceledOnTouchOutside(false);
                            createReviewDialog.setOnDismissListener(dialog -> {
                                NavController navController = Navigation.findNavController(container);
                                navController.popBackStack();
                                navController.navigate(R.id.nav_home);
                            });
                            createReviewDialog.show();

                        } else {
                            NavController navController = Navigation.findNavController(container);
                            navController.popBackStack();
                            navController.navigate(R.id.nav_home);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {}
        });
    }
}
