package com.example.eventy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.event.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private ArrayList<Event> events;
    private LayoutInflater layoutInflater;

    public EventsAdapter(Context context, ArrayList<Event> events) {
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_event_card, parent, false);

        return new EventsAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        Event event = events.get(position);
        if (event != null) {
            holder.eventName.setText(event.getName());

            String eventTypeString = "Type: " + event.getEventType().getName();
            holder.eventType.setText(eventTypeString);

            String maxParticipantsString = "Max people: " + event.getMaxParticipants();
            holder.maxParticipants.setText(maxParticipantsString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
            String formattedDate = dateFormat.format(event.getDate());
            holder.eventDate.setText(formattedDate);

            holder.eventLocation.setText(event.getLocation().getName());

            String openOrFullString = (event.getPrivacyType() == PrivacyType.PRIVATE ? "FULL EVENT" : "OPEN EVENT") + "!";
            holder.openOrFull.setText(openOrFullString);
            holder.openOrFull.setTextColor(event.getPrivacyType() == PrivacyType.PRIVATE ? Color.parseColor("#E91A1A") : Color.parseColor("#3ED34F"));

            holder.description.setText(event.getDescription());

            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                // Show a Toast with the event name
                Toast.makeText(holder.itemView.getContext(), "See more: Event: " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                // Show a Toast with the event name
                Toast.makeText(holder.itemView.getContext(), "Favorite: Event: " + event.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventType, maxParticipants, eventDate, eventLocation, openOrFull, description;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventType = itemView.findViewById(R.id.event_type);
            maxParticipants = itemView.findViewById(R.id.max_participants);
            eventDate = itemView.findViewById(R.id.event_date);
            eventLocation = itemView.findViewById(R.id.event_location);
            openOrFull = itemView.findViewById(R.id.open_or_full);
            description = itemView.findViewById(R.id.description);
        }
    }
}