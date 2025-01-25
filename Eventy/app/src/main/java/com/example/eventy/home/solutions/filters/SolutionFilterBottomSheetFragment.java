package com.example.eventy.home.solutions.filters;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;

import com.example.eventy.R;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.custom.SingleSpinner;
import com.example.eventy.databinding.BottomSheetHomeSolutionsFilterBinding;
import com.example.eventy.solutions.model.SolutionsFilter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SolutionFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetHomeSolutionsFilterBinding binding;
    private FilterListener listener;
    private boolean isDatePickerOpened = false;
    private LocalDateTime selectedStartDateTime = null;
    private LocalDateTime selectedEndDateTime = null;
    private ArrayList<String> eventTypes;
    private ArrayList<String> categories;
    private ArrayList<String> companies;

    public SolutionFilterBottomSheetFragment(ArrayList<String> eventTypes, ArrayList<String> categories, ArrayList<String> companies) {
        this.eventTypes = eventTypes;
        this.categories = categories;
        this.companies = companies;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetHomeSolutionsFilterBinding.inflate(inflater, container, false);

        // Check if listener is set
        if (getParentFragment() instanceof SolutionFilterBottomSheetFragment.FilterListener) {
            listener = (SolutionFilterBottomSheetFragment.FilterListener) getParentFragment();
        } else {
            throw new RuntimeException("Parent fragment must implement Solution's FilterListener");
        }

        setupFilterInputs();

        if (getArguments() != null) {
            String selectedType = getArguments().getString("type");
            ArrayList<String> selectedEventTypes = getArguments().getStringArrayList("eventTypes");
            ArrayList<String> selectedCategories = getArguments().getStringArrayList("categories");
            String selectedCompany = getArguments().getString("company");
            String minPrice = getArguments().getString("minPrice");
            String maxPrice = getArguments().getString("maxPrice");
            Boolean available = getArguments().getBoolean("available");
            String selectedDateRange = getArguments().getString("dateRange");

            binding.solutionTypeFilter.setSelection(selectedType.equals("Any") ? 0 : selectedType.equals("Service") ? 1 : 2);
            binding.solutionCategoryFilter.restoreSelectedItem(selectedCategories);
            binding.solutionEventTypesFilter.restoreSelectedItem(selectedEventTypes);
            binding.companyFilter.restoreSelectedItem(selectedCompany);
            binding.minPriceFilter.setText(minPrice);
            binding.maxPriceFilter.setText(maxPrice);
            binding.availableFilter.setChecked(available);
            binding.showSelectedDate.setText(selectedDateRange);
        }

        binding.solutionsConfirmButton.setOnClickListener(v -> {
            SolutionsFilter selectedFilters = getSelectedFilters();
            if (listener != null) {
                listener.onFiltersSelected(selectedFilters);
            }
            dismiss();
        });

        return binding.getRoot();
    }

    private void setupFilterInputs() {
        setupFilterType();
        setupFilterCategories();
        setupFilterEventTypes();
        setupFilterCompanies();
        setupFilterDay();
        setupFilterDateSelection();
        setupFilterResetAll();
    }

    private void setupFilterType() {
        Spinner solutionTypeSpinner = binding.solutionTypeFilter;

        String[] types = new String[] {
            "-", "Service", "Product"
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

    private void setupFilterCategories() {
        MultiSpinner solutionCategoryMultiSpinner = binding.solutionCategoryFilter;
        solutionCategoryMultiSpinner.setItems(categories, "-", "Categories");
    }

    private void setupFilterEventTypes() {
        MultiSpinner eventTypeMultiSpinner = binding.solutionEventTypesFilter;
        eventTypeMultiSpinner.setItems(eventTypes, "-", "Event types");
    }

    private void setupFilterCompanies() {
        SingleSpinner companiesSingleSpinner = binding.companyFilter;
       companiesSingleSpinner.setItems(companies, "-", "Companies");
    }

    private void setupFilterDay() {
        Spinner daySpinner = binding.dayFilter;

        String[] dayTypes = new String[] {"Any day", "Custom"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dayTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        daySpinner.setAdapter(arrayAdapter);

        Button dateRangeButton = binding.solutionDateRangeFilter;
        dateRangeButton.setEnabled(false);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) {
                    dateRangeButton.setEnabled(true);
                    dateRangeButton.setBackgroundResource(R.drawable.filter_button_background);
                } else {
                    dateRangeButton.setEnabled(false);
                    dateRangeButton.setBackgroundResource(R.drawable.filter_button_background_disabled);
                    dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                    binding.showSelectedDate.setText("No date selected");
                    selectedStartDateTime = null;
                    selectedEndDateTime = null;
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dateRangeButton.setEnabled(false);
                dateRangeButton.setText("SELECT DATES \uD83D\uDDD3");
                binding.showSelectedDate.setText("No date selected");
            }
        });
    }

    private void setupFilterDateSelection() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

        binding.solutionDateRangeFilter.setOnClickListener(v1 -> {
            if (!isDatePickerOpened) {
                materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                isDatePickerOpened = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            selectedStartDateTime = Instant.ofEpochMilli(startDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            selectedEndDateTime = Instant.ofEpochMilli(endDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString.equals(endDateString) ? startDateString : startDateString + " - " + endDateString;
            binding.solutionDateRangeFilter.setText(selectedDateRange);

            binding.showSelectedDate.setText(startDateString.equals(endDateString)
                    ? "Selected date is: " + selectedDateRange
                    : "Selected dates are: " + selectedDateRange);

            isDatePickerOpened = false;
        });
    }

    private void setupFilterResetAll() {
        AppCompatButton resetAllButton = binding.resetAllFilter;
        resetAllButton.setOnClickListener(v -> {
            binding.solutionTypeFilter.setSelection(0);
            binding.solutionCategoryFilter.restoreSelectedItem(new ArrayList<String>());
            binding.solutionEventTypesFilter.restoreSelectedItem(new ArrayList<String>());
            binding.companyFilter.restoreSelectedItem("-");
            binding.minPriceFilter.setText(null);
            binding.maxPriceFilter.setText(null);
            binding.availableFilter.setChecked(true);

            binding.dayFilter.setSelection(0);
            binding.showSelectedDate.setText("Not selected");
            selectedStartDateTime = null;
            selectedEndDateTime = null;
        });
    }

    public SolutionsFilter getSelectedFilters() {
        SolutionsFilter selectedFilters = new SolutionsFilter();

        // Type
        String selectedType = binding.solutionTypeFilter.getSelectedItem().toString();
        if (selectedType.equals("-")) {
            selectedType = "Any";
        }
        selectedFilters.setType(selectedType);

        // Categories
        MultiSpinner multiSpinnerCategories = binding.solutionCategoryFilter;
        List<String> selectedCategories = multiSpinnerCategories.getSelectedItems();
        selectedFilters.setCategories((ArrayList<String>) selectedCategories);

        // Event Types
        MultiSpinner multiSpinnerEventTypes = binding.solutionEventTypesFilter;
        List<String> selectedEventTypes = multiSpinnerEventTypes.getSelectedItems();
        selectedFilters.setEventTypes((ArrayList<String>) selectedEventTypes);

        // Company
        String selectedCompany = binding.companyFilter.getSelectedItem();
        selectedFilters.setCompany(selectedCompany);

        // Min Price
        String minPrice = binding.minPriceFilter.getText().toString();
        selectedFilters.setMinPrice(minPrice);

        // Max Price
        String maxPrice = binding.maxPriceFilter.getText().toString();
        selectedFilters.setMaxPrice(maxPrice);

        // Start Date & End Date
        selectedFilters.setStartDate(selectedStartDateTime);
        selectedFilters.setEndDate(selectedEndDateTime);

        Boolean available = binding.availableFilter.isChecked();
        selectedFilters.setAvailable(available);

        return selectedFilters;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getParentFragment() instanceof SolutionFilterBottomSheetFragment.FilterListener) {
            ((SolutionFilterBottomSheetFragment.FilterListener) getParentFragment()).onFiltersClosed();
        }
    }

    public interface FilterListener {
        void onFiltersSelected(SolutionsFilter filterValues);
        void onFiltersClosed();
    }
}
