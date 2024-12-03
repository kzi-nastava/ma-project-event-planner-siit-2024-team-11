package com.example.eventy.users.pup;

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
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.R;
import com.example.eventy.adapters.solutions.SolutionsAdapter;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.databinding.FragmentHomeSolutionsBinding;
import com.example.eventy.databinding.FragmentPupOwnServicesBinding;
import com.example.eventy.home.solutions.SolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsFragment;
import com.example.eventy.home.solutions.featured_solutions.FeaturedSolutionsTitleFragment;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PUPOwnServicesFragment extends Fragment implements MultiSpinner.MultiSpinnerListener {
    private FragmentPupOwnServicesBinding binding;
    private SolutionsAdapter solutionsAdapter;
    private TextView showSelectedDateText;
    private Button dateRangeButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPupOwnServicesBinding.inflate(inflater, container, false);

        setupTabSolutions();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Solution> solutions = getSolutions();

        solutionsAdapter = new SolutionsAdapter(requireContext(), solutions);

        binding.solutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.solutionsRecycler.setAdapter(solutionsAdapter);
    }

    @NonNull
    private static ArrayList<Solution> getSolutions() {
        ArrayList<Solution> solutions = new ArrayList<>();

        ArrayList<EventType> eventTypes = new ArrayList<>();
        EventType eventType1 = new EventType();
        eventType1.setName("Wedding");
        EventType eventType2 = new EventType();
        eventType2.setName("Sport");
        EventType eventType3 = new EventType();
        eventType3.setName("Conference");
        EventType eventType4 = new EventType();
        eventType4.setName("Party");

        eventTypes.add(eventType1);
        eventTypes.add(eventType2);
        eventTypes.add(eventType3);
        eventTypes.add(eventType4);

        Service service1 = new Service(
                "Photography",
                new Category("photography", "Neki description", Status.ACCEPTED),
                "Professional wedding photography service.",
                1500.0, 10,
                new ArrayList<>(Arrays.asList("image1.jpg", "image2.jpg")),
                false, true, true, "Full-day photography",
                30, 180, 7, 3,
                ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Service service2 = new Service(
                "Bon Jovi",
                new Category("music", "Neki description", Status.ACCEPTED),
                "Best band ever!", 800.0, 5,
                new ArrayList<>(Arrays.asList("dj1.jpg", "dj2.jpg")),
                false, true, true, "Includes sound and lighting equipment",
                60, 120, 14, 7,
                ReservationConfirmationType.MANUAL, eventTypes
        );

        Service service3 = new Service(
                "Event Catering - The best",
                new Category("catering", "Neki description", Status.ACCEPTED),
                "Delicious catering service for all types of events.",
                1200.0, 15,
                new ArrayList<>(Arrays.asList("catering1.jpg", "catering2.jpg")),
                false, true, true, "Custom menu available",
                30, 150, 10, 5,
                ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Service service4 = new Service(
                "Event Decor",
                new Category("decor", "Neki description", Status.ACCEPTED),
                "Luxurious event decor for any theme.",
                1000.0, 12,
                new ArrayList<>(Arrays.asList("decor1.jpg", "decor2.jpg")),
                false, true, true, "Includes venue setup and takedown",
                45, 200, 10, 5,
                ReservationConfirmationType.MANUAL, eventTypes
        );

        Service service5 = new Service(
                "Makeup Artist",
                new Category("beauty", "Neki description", Status.ACCEPTED),
                "Professional makeup services for any occasion.",
                300.0, 0,
                new ArrayList<>(Arrays.asList("makeup1.jpg", "makeup2.jpg")),
                false, true, true, "Includes trial session and travel to venue",
                30, 90, 7, 3,
                ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Service service6 = new Service(
                "DJ Service",
                new Category("entertainment", "Neki description", Status.ACCEPTED),
                "High-energy DJ service for any event.",
                500.0, 8,
                new ArrayList<>(Arrays.asList("dj3.jpg", "dj4.jpg")),
                false, true, true, "Includes lighting and custom playlists",
                60, 180, 14, 7,
                ReservationConfirmationType.MANUAL, eventTypes
        );

        solutions.add(service1);
        solutions.add(service2);
        solutions.add(service3);
        solutions.add(service4);
        solutions.add(service5);
        solutions.add(service6);

        return solutions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupTabSolutions() {
        setupSolutionSearch();
        setupSolutionFilters();
        setupSolutionSort();
    }



    private void setupSolutionSearch() {
        // Not implemented yet
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
        solutionCategoryMultiSpinner.setItems(categories, "-", this , "Event types");
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
