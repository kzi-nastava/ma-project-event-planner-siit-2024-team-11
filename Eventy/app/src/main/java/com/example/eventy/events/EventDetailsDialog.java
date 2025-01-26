package com.example.eventy.events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventy.R;
import com.example.eventy.events.model.EventCard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDetailsDialog extends Dialog implements android.view.View.OnClickListener {
    public ImageView closeButton;
    private EventCard selectedEventCard;

    public EventDetailsDialog(Activity a, EventCard selectedEventCard) {
        super(a);
        this.selectedEventCard = selectedEventCard;
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
        eventNameTextView.setText('"' + selectedEventCard.getName() + '"');

        TextView eventTypeTextView = findViewById(R.id.event_type);
        eventTypeTextView.setText("Type: " + selectedEventCard.getEventTypeName());

        TextView maxParticipantsTextView = findViewById(R.id.max_participants);
        maxParticipantsTextView.setText("Max participants: " + selectedEventCard.getMaxNumberParticipants());

        TextView eventDateTextView = findViewById(R.id.event_date);
        LocalDateTime dateTime = selectedEventCard.getStartDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        String formattedDate = dateTime.format(formatter);
        eventDateTextView.setText(formattedDate);

        TextView eventLocationTextView = findViewById(R.id.event_location);
        eventLocationTextView.setText(selectedEventCard.getLocationName());

        TextView openOrFullTextView = findViewById(R.id.open_or_full);
        String openOrFullString = (selectedEventCard.isOpen() ? "OPEN EVENT" : "FULL EVENT") + "!";
        openOrFullTextView.setText(openOrFullString);
        openOrFullTextView.setTextColor(selectedEventCard.isOpen() ? Color.parseColor("#3ED34F") : Color.parseColor("#E91A1A"));

        TextView descriptionTextView = findViewById(R.id.description);
        descriptionTextView.setText(selectedEventCard.getDescription());
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
