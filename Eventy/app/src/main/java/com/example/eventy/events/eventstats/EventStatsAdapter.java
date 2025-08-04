package com.example.eventy.events.eventstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.eventy.events.model.EventStats;
import com.example.eventy.utils.ClientUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
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

            String openOrPrivateString = (eventCard.getIsOpen() ? "PUBLIC" : "PRIVATE") + "!";
            holder.openOrFull.setText(openOrPrivateString);
            holder.openOrFull.setTextColor(eventCard.getIsOpen() ? Color.parseColor("#3ED34F") : Color.parseColor("#4ea0e7"));

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
            holder.numberChart.setProgressCompat(eventStats.getVisitors() / eventCard.getMaxNumberParticipants(), true);
            holder.numberChartText.setText("Number of participants: " + eventStats.getVisitors() + "/" + eventCard.getMaxNumberParticipants());
            holder.averageGradeChart.setProgressCompat((int) ((float) eventStats.getAverageGrade() / 5 * 100), true);
            holder.averageGradeChartText.setText("Average grade: " + eventStats.getAverageGrade() + "/5");

            holder.itemView.findViewById(R.id.download_event_stats_button).setOnClickListener(v -> {
                Call<ResponseBody> call = ClientUtils.eventService.triggerEventStatsPDFDownload(eventCard.getEventId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null && savePDFToDownloads(response.body(), eventCard.getName() + " Event Stats.pdf", holder.itemView.getContext())) {
                            new AlertDialog.Builder(holder.itemView.getContext())
                                    .setTitle("Event Stats PDF is downloading")
                                    .setMessage("Please check your downloads folder!")
                                    .setIcon(R.drawable.icon_success_png)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }})
                                    .show();
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error while downloading", "Error while downloading event stats. Please try again later.");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error while downloading", "Error while downloading event stats. Please try again later.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventStats.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventType, maxParticipants, eventDate, eventLocation, openOrFull, description, averageGradeChartText, numberChartText;
        private BarChart barChart;
        private CircularProgressIndicator averageGradeChart;
        private CircularProgressIndicator numberChart;

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
            averageGradeChartText = itemView.findViewById(R.id.averageGradeChartText);
            numberChart = itemView.findViewById(R.id.numberChart);
            numberChartText = itemView.findViewById(R.id.numberChartText);
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
            Description description = new Description();
            description.setText("Grade Distribution");
            barChart.setDescription(description);
            barChart.setData(barData);
            barChart.invalidate();
        }

        public void setupPieChart(float num, float max, String title, PieChart pieChart) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(num));
            entries.add(new PieEntry(max - num));

            PieDataSet dataSet = new PieDataSet(entries, title);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);
            Description description = new Description();
            description.setText(title);
            pieChart.setDescription(description);
            pieChart.invalidate();
        }
    }

    private boolean savePDFToDownloads(ResponseBody body, String fileName, Context context) {
        try {
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                outputStream = context.getContentResolver().openOutputStream(
                        context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                );
            } else {
                // For Android 9 and below, use External Storage (Requires Permission)
                File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                outputStream = new FileOutputStream(pdfFile);
            }

            if (outputStream != null) {
                outputStream.write(body.bytes());
                outputStream.close();
                Log.d("PDF", "File saved successfully!");
                return true;
            }
        } catch (Exception e) {
            Log.e("PDF", "Error saving PDF: " + e.getMessage());
        }
        return false;
    }
}
