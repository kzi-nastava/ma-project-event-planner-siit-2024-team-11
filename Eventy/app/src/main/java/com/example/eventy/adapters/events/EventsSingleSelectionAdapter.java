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
import com.example.eventy.events.model.EventCard;
import com.example.eventy.services.ReservationSelectEventFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventsSingleSelectionAdapter extends RecyclerView.Adapter<EventsSingleSelectionAdapter.EventSingleSelectionViewHolder> {
    private ArrayList<EventCard> eventCards;
    private ReservationSelectEventFragment reservationSelectEventFragment;
    private LayoutInflater layoutInflater;
    LinearLayout previousEventCardBorder = null;

    public EventsSingleSelectionAdapter(Context context, ArrayList<EventCard> eventCards, ReservationSelectEventFragment reservationSelectEventFragment) {
        this.eventCards = eventCards;
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
        EventCard eventCard = eventCards.get(position);
        if (eventCard != null) {
            holder.eventName.setText('"' + eventCard.getName() + '"');

            String eventTypeString = "Type: " + eventCard.getEventType().getName();
            holder.eventType.setText(eventTypeString);

            String maxParticipantsString = "Max people: " + eventCard.getMaxParticipants();
            holder.maxParticipants.setText(maxParticipantsString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
            String formattedDate = dateFormat.format(eventCard.getDate());
            holder.eventDate.setText(formattedDate);

            holder.eventLocation.setText(eventCard.getLocation().getName());

            String openOrFullString = (eventCard.getPrivacyType() == PrivacyType.PRIVATE ? "FULL EVENT" : "OPEN EVENT") + "!";
            holder.openOrFull.setText(openOrFullString);
            holder.openOrFull.setTextColor(eventCard.getPrivacyType() == PrivacyType.PRIVATE ? Color.parseColor("#E91A1A") : Color.parseColor("#3ED34F"));

            holder.description.setText(eventCard.getDescription());

            /*
            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "See more: " + eventCard.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Favorite: " + eventCard.getName(), Toast.LENGTH_SHORT).show();
            });*/

            LinearLayout borderContainer = holder.itemView.findViewById(R.id.border_container);
            borderContainer.setOnClickListener(v -> {
                if (previousEventCardBorder != null) {
                    previousEventCardBorder.setBackground(null);
                }

                previousEventCardBorder = holder.itemView.findViewById(R.id.border_container);
                previousEventCardBorder.setBackgroundResource(R.drawable.selected_event_card);
                Toast.makeText(holder.itemView.getContext(), "Selected: " + eventCard.getName(), Toast.LENGTH_SHORT).show();
                reservationSelectEventFragment.setSelectedEvent(eventCard);
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

    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
