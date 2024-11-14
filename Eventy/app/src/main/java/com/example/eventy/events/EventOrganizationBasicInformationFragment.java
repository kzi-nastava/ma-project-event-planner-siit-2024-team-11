package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventOrganizationBasicInformationBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiConsumer;

public class EventOrganizationBasicInformationFragment extends Fragment {
    private FragmentEventOrganizationBasicInformationBinding binding;
    private Marker pinMarker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventOrganizationBasicInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.maxParticipantsInputLayout, binding.maxParticipantsInput, this::validateNumber);

        binding.radioPublic.setOnClickListener(v -> {
            binding.radioPrivate.setChecked(false);
            binding.radioPublic.setChecked(true);
        });

        binding.radioPrivate.setOnClickListener(v -> {
            binding.radioPublic.setChecked(false);
            binding.radioPrivate.setChecked(true);
        });

        MaterialAutoCompleteTextView eventTypeAutoCompleteTextView = binding.eventTypeAutoCompleteTextView;
        String[] eventTypes = {"Wedding", "Graduation", "Conference"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes);
        eventTypeAutoCompleteTextView.setAdapter(adapter);

        // Ensure the dropdown appears when the view is tapped
        eventTypeAutoCompleteTextView.setOnClickListener(v -> {
            if (!eventTypeAutoCompleteTextView.isPopupShowing()) {
                eventTypeAutoCompleteTextView.showDropDown();
            }
        });

        /* OnlineTileSourceBase cartoTileSource = new OnlineTileSourceBase(
                "CartoDB",
                0, // min zoom level
                19, // max zoom level
                256, // tile size
                ".png", // file extension
                new String[]{
                        "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png" // CartoDB Light All (Positron)
                }) {

            @Override
            public String getTileURLString(long pMapTileIndex) {
                // Extract the zoom level, x, and y from the pMapTileIndex
                int zoomLevel = (int) (pMapTileIndex >> 38); // Zoom level is stored in the higher bits
                int xTile = (int) ((pMapTileIndex >> 19) & 511); // x coordinate (bits 19-28)
                int yTile = (int) (pMapTileIndex & 511); // y coordinate (lower 19 bits)

                // Construct the URL for CartoDB tiles
                return String.format("https://{s}.basemaps.cartocdn.com/light_all/%d/%d/%d.png", zoomLevel, xTile, yTile);
            }
        }; */

        binding.mapview.setTileSource(TileSourceFactory.OpenTopo);
        binding.mapview.setMultiTouchControls(true);

        // Set initial zoom level and center
        IMapController mapController = binding.mapview.getController();
        mapController.setCenter(new GeoPoint(45.2445, 19.8484));  // Set to a default location
        mapController.setZoom(15.0);
        // Set the click listener for the map
        binding.mapview.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Convert the touch coordinates into GeoPoint
                GeoPoint tappedPoint = (GeoPoint) binding.mapview.getProjection().fromPixels((int) event.getX(), (int) event.getY());

                // Remove existing pin if it exists
                if (pinMarker != null) {
                    binding.mapview.getOverlays().remove(pinMarker);
                }

                // Add new pin at the tapped location
                pinMarker = new Marker(binding.mapview);
                pinMarker.setPosition(tappedPoint);
                pinMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                // Set the marker icon using ContextCompat for newer versions
                pinMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.location_pin_icon));

                binding.mapview.getOverlays().add(pinMarker);

                // Show location as a Toast or do something with it
                binding.mapLocationText.setText("Pinned Location: " + tappedPoint.getLatitude() + ", " + tappedPoint.getLongitude());
            }
            return true;  // Return true to indicate that the touch event is handled
        });

        binding.dateRangeInput.setOnClickListener(v -> showDateRangePicker());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        // real time field validation
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input as the user types
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required"); // Set error message
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }

    private void validateNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required"); // Set error message
        }
        else if(!inputText.matches("^[1-9][0-9]*$")) {
            textInputLayout.setError("The value should be a positive integer"); // Set error message
        } else {
            textInputLayout.setError(null); // Clear error if valid
        }
    }

    public void showDateRangePicker() {
        // Configure date range picker constraints
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()); // Optional: Limits to future dates only

        // Build the date range picker
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        builder.setCalendarConstraints(constraintsBuilder.build());

        // Create the picker
        final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        // Show the picker
        dateRangePicker.show(getParentFragmentManager(), "date_range_picker");

        dateRangePicker.addOnNegativeButtonClickListener(selection -> {
            if(!binding.dateRangeInput.getText().toString().contains("-")) {
                binding.dateRangeInputLayout.setError("You have to enter full range!");
            }
        });

        // Handle the result when dates are selected
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Check that the selection is not null
            if (startDate != null && endDate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedStart = formatter.format(new Date(startDate));
                String formattedEnd = formatter.format(new Date(endDate));

                // Display the selected date range in the TextInputEditText
                binding.dateRangeInput.setText(formattedStart + " - " + formattedEnd);
                binding.dateRangeInputLayout.setError(null);
            }
            else {
                if(!binding.dateRangeInput.getText().toString().contains("-")) {
                    binding.dateRangeInputLayout.setError("You have to enter full range!");
                }
            }
        });
    }
}