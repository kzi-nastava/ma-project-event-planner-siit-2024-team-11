package com.example.eventy.adapters.events;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.utils.ClientUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private ArrayList<EventCard> eventCards;
    private LayoutInflater layoutInflater;

    public EventsAdapter(Context context, ArrayList<EventCard> eventCards) {
        this.eventCards = eventCards;
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

            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putLong("EventID", eventCard.getEventId());
                NavController navController = Navigation.findNavController(v);

                navController.popBackStack();

                navController.navigate(R.id.nav_event_details, args);
            });

            ImageButton favoriteButton = holder.itemView.findViewById(R.id.favorite_button);

            if(eventCard.getIsFavorite()) {
                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller_white));
            }

            favoriteButton.setOnClickListener(v -> {
                Call<Void> call = ClientUtils.eventService.toggleFavoriteEvent(eventCard.getEventId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            eventCard.setIsFavorite(!eventCard.getIsFavorite());

                            if (eventCard.getIsFavorite()) {
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller_white));
                            } else {
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller));
                            }

                            Toast.makeText(holder.itemView.getContext(), (eventCard.getIsFavorite() ? "Favorite: " : "Removed Favorite: ") + eventCard.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error", "Please log in to make this your favorite event.");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error", "Please log in to make this your favorite event.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventCards.size();
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