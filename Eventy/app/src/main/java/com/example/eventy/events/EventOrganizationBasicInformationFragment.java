package com.example.eventy.events;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        eventTypeAutoCompleteTextView.setOnClickListener(v -> {
            if (!eventTypeAutoCompleteTextView.isPopupShowing()) {
                eventTypeAutoCompleteTextView.showDropDown();
            }
        });

        OnlineTileSourceBase cartoTileSource = new OnlineTileSourceBase(
                "CartoDB",
                0,
                19,
                256,
                ".png",
                new String[]{
                        "https://a.basemaps.cartocdn.com/light_all/",
                        "https://b.basemaps.cartocdn.com/light_all/",
                        "https://c.basemaps.cartocdn.com/light_all/",
                        "https://d.basemaps.cartocdn.com/light_all/"
                }) {

            @Override
            public String getTileURLString(long pMapTileIndex) {
                int zoomLevel = MapTileIndex.getZoom(pMapTileIndex);
                int xTile = MapTileIndex.getX(pMapTileIndex);
                int yTile = MapTileIndex.getY(pMapTileIndex);

                String[] subdomains = {"a", "b", "c", "d"};
                String subdomain = subdomains[(int) (Math.random() * subdomains.length)];

                return String.format("https://%s.basemaps.cartocdn.com/light_all/%d/%d/%d.png", subdomain, zoomLevel, xTile, yTile);
            }
        };

        binding.mapview.setTileSource(cartoTileSource);
        binding.mapview.invalidate(); // Refresh the map to load tiles
        binding.mapview.setMultiTouchControls(true);


        IMapController mapController = binding.mapview.getController();
        mapController.setCenter(new GeoPoint(45.2445, 19.8484));  // Default location: FTN
        mapController.setZoom(15.0);

        // so that I can move on the map nicely
        binding.mapview.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);

            return false;
        });

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint tappedPoint) {
                if (pinMarker != null) {
                    binding.mapview.getOverlays().remove(pinMarker);
                }

                pinMarker = new Marker(binding.mapview);
                pinMarker.setPosition(tappedPoint);
                pinMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                pinMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.icon_location_pin));

                binding.mapview.getOverlays().add(pinMarker);

                getAddressFromCoordinates(tappedPoint.getLatitude(), tappedPoint.getLongitude());

                binding.mapview.invalidate();

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                return false;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        binding.mapview.getOverlays().add(mapEventsOverlay);

        binding.dateRangeInput.setOnClickListener(v -> showDateRangePicker());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else {
            textInputLayout.setError(null);
        }
    }

    private void validateNumber(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        }
        else if(!inputText.matches("^[1-9][0-9]*$")) {
            textInputLayout.setError("The value should be a positive integer");
        } else {
            textInputLayout.setError(null);
        }
    }

    public void showDateRangePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        builder.setCalendarConstraints(constraintsBuilder.build());

        final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.show(getParentFragmentManager(), "date_range_picker");

        dateRangePicker.addOnNegativeButtonClickListener(selection -> {
            if(!binding.dateRangeInput.getText().toString().contains("-")) {
                binding.dateRangeInputLayout.setError("You have to enter full range!");
            }
        });

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            if (startDate != null && endDate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedStart = formatter.format(new Date(startDate));
                String formattedEnd = formatter.format(new Date(endDate));

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

    private void getAddressFromCoordinates(double latitude, double longitude) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            String addressText = "Address not found";

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder addressString = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressString.append(address.getAddressLine(i)).append("\n");
                    }
                    addressText = addressString.toString().trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String finalAddressText = addressText;
            handler.post(() -> {
                binding.mapLocationText.setText("Address: " + finalAddressText);
            });
        });
    }
}