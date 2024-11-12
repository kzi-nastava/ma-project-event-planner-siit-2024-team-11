package com.example.eventy.adapters.events;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.text.SimpleDateFormat;

import com.example.eventy.R;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.event.Event;

public class FeaturedEventsAdapter extends RecyclerView.Adapter<FeaturedEventsAdapter.EventViewHolder> {
    private ArrayList<Event> featuredEvents;
    private LayoutInflater layoutInflater;

    public FeaturedEventsAdapter(Context context, ArrayList<Event> featuredEvents) {
        this.featuredEvents = featuredEvents;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType % 2 == 0) {
            view = layoutInflater.inflate(R.layout.fragment_home_featured_event_left, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.fragment_home_featured_event_right, parent, false);
        }

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = featuredEvents.get(position);
        if (event != null) {
            holder.eventName.setText(event.getName());

            if (position % 2 == 0) {
                View eventCardLeft = holder.itemView.findViewById(R.id.event_card_left);
                eventCardLeft.post(() -> {
                    int height = eventCardLeft.getHeight();

                    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                    layoutParams.height = height + convertDpToPx(15, holder.itemView);

                    holder.itemView.setLayoutParams(layoutParams);
                });
            } else {
                View eventCardRight = holder.itemView.findViewById(R.id.event_card_right);
                eventCardRight.post(() -> {
                    int height = eventCardRight.getHeight();

                    ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                    layoutParams.height = height + convertDpToPx(15, holder.itemView);

                    holder.itemView.setLayoutParams(layoutParams);
                });
            }

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
                Toast.makeText(holder.itemView.getContext(), "See more: " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Favorite: " + event.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private int convertDpToPx(int dp, View itemView) {
        return (int) (dp * itemView.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return featuredEvents.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Return 0 or 1 based on position to alternate the layout
        return position % 2;
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
