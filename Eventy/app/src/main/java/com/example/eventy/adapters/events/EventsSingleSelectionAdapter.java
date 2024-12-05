package com.example.eventy.adapters.events;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.event.Event;
import com.example.eventy.services.ReservationSelectEventFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventsSingleSelectionAdapter extends RecyclerView.Adapter<EventsSingleSelectionAdapter.EventSingleSelectionViewHolder> {
    private ArrayList<Event> events;
    private ReservationSelectEventFragment reservationSelectEventFragment;
    private LayoutInflater layoutInflater;
    LinearLayout previousEventCardBorder = null;

    public EventsSingleSelectionAdapter(Context context, ArrayList<Event> events, ReservationSelectEventFragment reservationSelectEventFragment) {
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
        this.reservationSelectEventFragment = reservationSelectEventFragment;
    }

    @NonNull
    @Override
    public EventsSingleSelectionAdapter.EventSingleSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_event_card, parent, false);

        return new EventsSingleSelectionAdapter.EventSingleSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsSingleSelectionAdapter.EventSingleSelectionViewHolder holder, int position) {
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

            /*
            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "See more: " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Favorite: " + event.getName(), Toast.LENGTH_SHORT).show();
            });*/

            LinearLayout borderContainer = holder.itemView.findViewById(R.id.border_container);
            borderContainer.setOnClickListener(v -> {
                if (previousEventCardBorder != null) {
                    previousEventCardBorder.setBackground(null);
                }

                previousEventCardBorder = holder.itemView.findViewById(R.id.border_container);
                previousEventCardBorder.setBackgroundResource(R.drawable.selected_event_card);
                Toast.makeText(holder.itemView.getContext(), "Selected: " + event.getName(), Toast.LENGTH_SHORT).show();
                reservationSelectEventFragment.setSelectedEvent(event);
            });
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventSingleSelectionViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventType, maxParticipants, eventDate, eventLocation, openOrFull, description;
        LinearLayout cardContainer;

        public EventSingleSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventType = itemView.findViewById(R.id.event_type);
            maxParticipants = itemView.findViewById(R.id.max_participants);
            eventDate = itemView.findViewById(R.id.event_date);
            eventLocation = itemView.findViewById(R.id.event_location);
            openOrFull = itemView.findViewById(R.id.open_or_full);
            description = itemView.findViewById(R.id.description);
            cardContainer = itemView.findViewById(R.id.card_container);
        }
    }

    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
