package com.example.eventy.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.events.model.CreateActivity;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<CreateActivity> tableRows;

    public TableAdapter(List<CreateActivity> tableRows) {
        this.tableRows = tableRows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.agenda_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreateActivity row = tableRows.get(position);
        holder.nameText.setText("Name: " + row.getName());
        holder.descriptionText.setText("Description: " + row.getDescription());
        holder.locationText.setText("Location: " + row.getLocation());
        holder.startTimeText.setText("Start Time: " + row.getStartTime());
        holder.endTimeText.setText("End Time: " + row.getEndTime());
    }

    @Override
    public int getItemCount() {
        return tableRows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, locationText, startTimeText, endTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            locationText = itemView.findViewById(R.id.locationText);
            startTimeText = itemView.findViewById(R.id.startTimeText);
            endTimeText = itemView.findViewById(R.id.endTimeText);
        }
    }
}
