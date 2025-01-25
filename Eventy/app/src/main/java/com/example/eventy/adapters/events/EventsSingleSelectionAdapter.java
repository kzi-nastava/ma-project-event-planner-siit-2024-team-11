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
import com.example.eventy.events.model.EventCard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventsSingleSelectionAdapter extends RecyclerView.Adapter<EventsSingleSelectionAdapter.EventSingleSelectionViewHolder> {
    private ArrayList<EventCard> eventCards;
    private LayoutInflater layoutInflater;

    private OnEventSelectedListener onEventSelectedListener;
    LinearLayout previousEventCardBorder = null;

    public EventsSingleSelectionAdapter(Context context, ArrayList<EventCard> eventCards) {
        this.eventCards = eventCards;
        this.layoutInflater = LayoutInflater.from(context);
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
        EventCard eventCard = eventCards.get(position);
        if (eventCard != null) {
            holder.eventName.setText('"' + eventCard.getName() + '"');

            String eventTypeString = "Type: " + eventCard.getEventTypeName();
            holder.eventType.setText(eventTypeString);

            String maxParticipantsString = "Max people: " + eventCard.getMaxNumberParticipants();
            holder.maxParticipants.setText(maxParticipantsString);

            LocalDateTime dateTime = eventCard.getStartDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            String formattedDate = dateTime.format(formatter);
            holder.eventDate.setText(formattedDate);

            holder.eventLocation.setText(eventCard.getLocationName());

            String openOrFullString = (eventCard.isOpen() ? "OPEN EVENT" : "FULL EVENT") + "!";
            holder.openOrFull.setText(openOrFullString);
            holder.openOrFull.setTextColor(eventCard.isOpen() ? Color.parseColor("#3ED34F") : Color.parseColor("#E91A1A"));

            holder.description.setText(eventCard.getDescription());

            LinearLayout borderContainer = holder.itemView.findViewById(R.id.border_container);
            borderContainer.setOnClickListener(v -> {
                if (previousEventCardBorder != null) {
                    previousEventCardBorder.setBackground(null);
                }

                previousEventCardBorder = holder.itemView.findViewById(R.id.border_container);
                previousEventCardBorder.setBackgroundResource(R.drawable.selected_event_card);

                if (onEventSelectedListener != null) {
                    Toast.makeText(holder.itemView.getContext(), "Selected: " + eventCard.getName(), Toast.LENGTH_SHORT).show();
                    onEventSelectedListener.onEventSelected(eventCard);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventCards.size();
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

    public void setOnEventSelectedListener(OnEventSelectedListener listener) {
        this.onEventSelectedListener = listener;
    }

    public interface OnEventSelectedListener {
        void onEventSelected(EventCard selectedEvent);
    }
}
