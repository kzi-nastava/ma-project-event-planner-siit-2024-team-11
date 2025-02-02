package com.example.eventy.events.eventstats;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventStats;
import com.example.eventy.home.events.event_card.EventCardFragment;
import com.example.eventy.utils.ClientUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventStatsAdapter extends RecyclerView.Adapter<EventStatsAdapter.EventViewHolder> {
    private ArrayList<EventStats> eventStats;
    private LayoutInflater layoutInflater;

    public EventStatsAdapter(Context context, ArrayList<EventStats> eventStats) {
        this.eventStats = eventStats;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_event_stats_card, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventStats eventStats = this.eventStats.get(position);
        EventCard eventCard = eventStats.getEventCard();

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

            holder.setupBarChart(eventStats.getGradeDistribution());
            holder.setupPieChart(eventStats.getVisitors(), eventCard.getMaxNumberParticipants(), "Visitors", holder.numberChart);
            holder.setupPieChart((float) eventStats.getAverageGrade(), 5, "Average Grade", holder.averageGradeChart);
        }
    }

    @Override
    public int getItemCount() {
        return eventStats.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventType, maxParticipants, eventDate, eventLocation, openOrFull, description;
        private BarChart barChart;
        private PieChart averageGradeChart;
        private PieChart numberChart;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.event_name);
            eventType = itemView.findViewById(R.id.event_type);
            maxParticipants = itemView.findViewById(R.id.max_participants);
            eventDate = itemView.findViewById(R.id.event_date);
            eventLocation = itemView.findViewById(R.id.event_location);
            openOrFull = itemView.findViewById(R.id.open_or_full);
            description = itemView.findViewById(R.id.description);
            barChart = itemView.findViewById(R.id.barChart);
            averageGradeChart = itemView.findViewById(R.id.averageGradeChart);
            numberChart = itemView.findViewById(R.id.numberChart);
        }

        public void setupBarChart(int[] gradeDistribution) {
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(1, gradeDistribution[0]));
            entries.add(new BarEntry(2, gradeDistribution[1]));
            entries.add(new BarEntry(3, gradeDistribution[2]));
            entries.add(new BarEntry(4, gradeDistribution[3]));
            entries.add(new BarEntry(5, gradeDistribution[4]));

            BarDataSet dataSet = new BarDataSet(entries, "Grade Distribution");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData barData = new BarData(dataSet);
            barChart.setData(barData);
            barChart.invalidate();
        }

        public void setupPieChart(float num, float max, String title, PieChart pieChart) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(num, title));

            PieDataSet dataSet = new PieDataSet(entries, title);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();
        }
    }
}
