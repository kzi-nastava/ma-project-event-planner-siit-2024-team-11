package com.example.eventy.home.events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.event.Event;

import java.text.SimpleDateFormat;

public class EventDetailsDialog extends Dialog implements
        android.view.View.OnClickListener {
    public ImageView closeButton;
    private Event selectedEvent;

    public EventDetailsDialog(Activity a, Event selectedEvent) {
        super(a);
        this.selectedEvent = selectedEvent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_event_details_for_reservation);
        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);

        setupEventDetails();
    }

    @SuppressLint("SetTextI18n")
    private void setupEventDetails() {
        TextView eventNameTextView = findViewById(R.id.event_name);
        eventNameTextView.setText('"' + selectedEvent.getName() + '"');

        TextView eventTypeTextView = findViewById(R.id.event_type);
        eventTypeTextView.setText("Type: " + selectedEvent.getEventType().getName());

        TextView maxParticipantsTextView = findViewById(R.id.max_participants);
        maxParticipantsTextView.setText("Max participants: " + selectedEvent.getMaxParticipants());

        TextView eventDateTextView = findViewById(R.id.event_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
        String formattedDate = dateFormat.format(selectedEvent.getDate());
        eventDateTextView.setText(formattedDate);

        TextView eventLocationTextView = findViewById(R.id.event_location);
        eventLocationTextView.setText(selectedEvent.getLocation().getName());

        TextView openOrFullTextView = findViewById(R.id.open_or_full);
        String openOrFullString = (selectedEvent.getPrivacyType() == PrivacyType.PRIVATE ? "FULL EVENT" : "OPEN EVENT") + "!";
        openOrFullTextView.setText(openOrFullString);
        openOrFullTextView.setTextColor(selectedEvent.getPrivacyType() == PrivacyType.PRIVATE ? Color.parseColor("#E91A1A") : Color.parseColor("#3ED34F"));

        TextView descriptionTextView = findViewById(R.id.description);
        descriptionTextView.setText(selectedEvent.getDescription());
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
