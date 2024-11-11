package com.example.eventy.adapters;

import android.content.Context;
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

        // Choose layout based on position
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

            // Calculate height based on text length
            holder.eventName.post(() -> {
                int lineCount = holder.eventName.getLineCount();
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

                if (lineCount > 1) {
                    layoutParams.height = convertDpToPx(200, holder.itemView); // Height for 2 lines
                } else {
                    layoutParams.height = convertDpToPx(185, holder.itemView); // Default height
                }

                holder.itemView.setLayoutParams(layoutParams);
            });

            String eventTypeString = "Type: " + event.getEventType().getName();
            holder.eventType.setText(eventTypeString);

            String maxParticipantsString = "Max people: " + event.getMaxParticipants();
            holder.maxParticipants.setText(maxParticipantsString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
            String formattedDate = dateFormat.format(event.getDate());
            holder.eventDate.setText(formattedDate);

            holder.eventLocation.setText(event.getLocation().getName());

            String openOrFullString = event.getPrivacyType() == PrivacyType.PRIVATE ? "PRIVATE EVENT" : "OPEN EVENT";
            openOrFullString += "!";
            holder.openOrFull.setText(openOrFullString);

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
