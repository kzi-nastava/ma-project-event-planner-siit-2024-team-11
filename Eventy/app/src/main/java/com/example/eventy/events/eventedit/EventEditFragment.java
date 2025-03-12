package com.example.eventy.events.eventedit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventEditBinding;
import com.example.eventy.events.model.CreateActivity;
import com.example.eventy.events.model.CreateLocation;
import com.example.eventy.events.model.Event;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.events.model.OrganizeEvent;
import com.example.eventy.events.model.UpdateEvent;
import com.example.eventy.events.organizeevent.EventAgendaCreation;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventEditFragment extends Fragment {
    private FragmentEventEditBinding binding;
    private Marker pinMarker;
    private Long selectedEventTypeId = -1L;

    private LocalDateTime selectedDate = null;

    private double latitude = -1L;
    private double longtitude = -1L;
    private EventAgendaCreation eventAgendaCreation;
    private Long eventId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventId = getArguments().getLong("EventID");

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);
        addValidation(binding.maxParticipantsInputLayout, binding.maxParticipantsInput, this::validateNumber);
        addValidation(binding.dateInputLayout, binding.dateInput, this::validateRequired);

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

                latitude = tappedPoint.getLatitude();
                longtitude = tappedPoint.getLongitude();
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

        binding.dateInput.setOnClickListener(v -> showDatePicker());

        Call<EventTypeCard[]> call = ClientUtils.eventTypeService.getActiveEventTypes();
        call.enqueue(new Callback<EventTypeCard[]>() {
            @Override
            public void onResponse(Call<EventTypeCard[]> call, Response<EventTypeCard[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventTypeCard[] eventTypeCards = response.body();
                    MaterialAutoCompleteTextView eventTypeAutoCompleteTextView = binding.eventTypeAutoCompleteTextView;
                    ArrayAdapter<EventTypeCard> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypeCards);
                    eventTypeAutoCompleteTextView.setAdapter(adapter);

                    eventTypeAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        // Get the selected EventTypeCard object
                        EventTypeCard selectedCard = (EventTypeCard) parent.getItemAtPosition(position);

                        // Get the ID of the selected card
                        selectedEventTypeId = selectedCard.getId();
                    });

                    eventTypeAutoCompleteTextView.setOnClickListener(v -> {
                        if (!eventTypeAutoCompleteTextView.isPopupShowing()) {
                            eventTypeAutoCompleteTextView.showDropDown();
                        }
                    });

                    Call<UpdateEvent> call2 = ClientUtils.eventService.getEventForUpdate(eventId);
                    call2.enqueue(new Callback<UpdateEvent>() {
                        @Override
                        public void onResponse(Call<UpdateEvent> call, Response<UpdateEvent> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                binding.nameInput.setText(response.body().getName());
                                binding.descriptionInput.setText(response.body().getDescription());
                                binding.maxParticipantsInput.setText(String.valueOf(response.body().getMaxNumberParticipants()));

                                for (EventTypeCard eventTypeCard : eventTypeCards) {
                                    if (eventTypeCard.getId() == response.body().getEventTypeId()) {
                                        binding.eventTypeAutoCompleteTextView.setText(eventTypeCard.getName(), false);
                                        selectedEventTypeId = eventTypeCard.getId();
                                    }
                                }

                                GeoPoint location = new GeoPoint(response.body().getLocation().getLatitude(), response.body().getLocation().getLongitude());
                                mapController.setCenter(location);
                                pinMarker = new Marker(binding.mapview);
                                pinMarker.setPosition(location);
                                pinMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                                pinMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.icon_location_pin));

                                binding.mapview.getOverlays().add(pinMarker);

                                getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
                                latitude = location.getLatitude();
                                longtitude = location.getLongitude();

                                LocalDateTime localDateTime = response.body().getDate();
                                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                                Date date = Date.from(zonedDateTime.toInstant());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String selectedDateString = sdf.format(date);
                                binding.dateInput.setText(selectedDateString);
                                selectedDate = localDateTime;

                                eventAgendaCreation = new EventAgendaCreation((ArrayList<CreateActivity>) response.body().getAgenda());
                                getChildFragmentManager().beginTransaction()
                                        .replace(R.id.agenda_container, eventAgendaCreation)
                                        .commit();
                            } else {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting the event!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateEvent> call, Throwable t) {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting the event!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    });
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting the event types!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<EventTypeCard[]> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting the event types!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        binding.submitButton.setOnClickListener(v -> {
            if (!this.isValid()) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Invalid input values!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();

                return;
            }

            UpdateEvent updateEvent = new UpdateEvent(
                    eventId,
                    binding.nameInput.getText().toString(),
                    binding.descriptionInput.getText().toString(),
                    Integer.valueOf(binding.maxParticipantsInput.getText().toString()),
                    selectedEventTypeId,
                    new CreateLocation(
                            this.binding.mapLocationText.getText().toString().substring(9),
                            this.binding.mapLocationText.getText().toString().substring(9),
                            this.latitude,
                            this.longtitude
                    ),
                    selectedDate,
                    eventAgendaCreation.getAgenda()
            );
            Call<Event> call2 = ClientUtils.eventService.edit(updateEvent);
            call2.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Successful edit")
                                .setMessage("Event is now edited.")
                                .setIcon(R.drawable.icon_success_png)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Bundle args = new Bundle();
                                        args.putLong("EventID", eventId);
                                        NavController navController = Navigation.findNavController(v);

                                        navController.popBackStack();

                                        navController.navigate(R.id.nav_event_details, args);
                                    }})
                                .show();
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while editing the event!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while editing the event!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        });

        binding.backButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("EventID", eventId);
            NavController navController = Navigation.findNavController(v);

            navController.popBackStack();

            navController.navigate(R.id.nav_event_details, args);
        });

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

    public void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Prefill with today
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selected date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(new Date(selection));
            binding.dateInput.setText(selectedDate);
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

    public String getName() {
        return this.binding.nameInput.getText().toString();
    }

    public String getDescription() {
        return this.binding.descriptionInput.getText().toString();
    }

    public int getMaxNumberParticipants() {
        return Integer.valueOf(this.binding.maxParticipantsInput.getText().toString());
    }

    public Long getEventTypeId() {
        return this.selectedEventTypeId;
    }

    public CreateLocation getLocation() {
        return new CreateLocation(
                this.binding.mapLocationText.getText().toString().substring(9),
                this.binding.mapLocationText.getText().toString().substring(9),
                this.latitude,
                this.longtitude
        );
    }

    public LocalDateTime getDate() {
        if(this.binding.dateInputLayout.getError() == null) {
            return this.selectedDate;
        }

        return null;
    }

    public boolean isValid() {
        boolean isValid = true;

        if (this.binding.nameInputLayout.getError() != null) {
            Log.e("Validation", "Name input has an error: " + this.binding.nameInputLayout.getError());
            isValid = false;
        }

        if (this.binding.descriptionInputLayout.getError() != null) {
            Log.e("Validation", "Description input has an error: " + this.binding.descriptionInputLayout.getError());
            isValid = false;
        }

        if (this.binding.maxParticipantsInputLayout.getError() != null) {
            Log.e("Validation", "Max participants input has an error: " + this.binding.maxParticipantsInputLayout.getError());
            isValid = false;
        }

        if (this.selectedEventTypeId == -1) {
            Log.e("Validation", "Selected event type ID is invalid.");
            isValid = false;
        }

        if (this.selectedDate == null) {
            Log.e("Validation", "Selected date is null.");
            isValid = false;
        }

        return isValid;
    }
}