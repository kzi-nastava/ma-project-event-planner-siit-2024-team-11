package com.example.eventy.adapters.events;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturedEventsAdapter extends RecyclerView.Adapter<FeaturedEventsAdapter.EventViewHolder> {
    private ArrayList<EventCard> featuredEventCards;
    private LayoutInflater layoutInflater;

    public FeaturedEventsAdapter(Context context, ArrayList<EventCard> featuredEventCards) {
        this.featuredEventCards = featuredEventCards;
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
        EventCard eventCard = featuredEventCards.get(position);
        if (eventCard != null) {
            holder.eventName.setText('"' + eventCard.getName() + '"');

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

            String eventTypeString = "Type: " + eventCard.getEventTypeName();
            holder.eventType.setText(eventTypeString);

            String maxParticipantsString = "Max people: " + eventCard.getMaxNumberParticipants();
            holder.maxParticipants.setText(maxParticipantsString);

            LocalDateTime dateTime = eventCard.getStartDate(); // Parse ISO 8601 string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            String formattedDate = dateTime.format(formatter); // Format to desired output
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

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Call<Void> call = ClientUtils.eventService.toggleFavoriteEvent(eventCard.getEventId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            eventCard.setFavorite(!eventCard.isFavorite());

                            if (eventCard.isFavorite()) {
                                favoriteButton.setBackgroundColor(Color.parseColor("#929AB7"));
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller_white));
                            }
                            else {
                                favoriteButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller));
                            }

                            Toast.makeText(holder.itemView.getContext(), (eventCard.isFavorite() ? "Favorite: " : "Remove Favorite: ") + eventCard.getName(), Toast.LENGTH_SHORT).show();
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

    private int convertDpToPx(int dp, View itemView) {
        return (int) (dp * itemView.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return featuredEventCards.size();
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
