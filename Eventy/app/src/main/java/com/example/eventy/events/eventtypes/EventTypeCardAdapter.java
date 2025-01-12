package com.example.eventy.events.eventtypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.eventy.R;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.events.model.EventTypeWithActivity;

public class EventTypeCardAdapter extends RecyclerView.Adapter<EventTypeCardAdapter.NameViewHolder> {

    private final List<EventTypeCard> namesList;

    public EventTypeCardAdapter(List<EventTypeCard> namesList) {
        this.namesList = namesList;
    }

    @NonNull
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_type_card, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.tvName.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("EventTypeID", namesList.get(position).getId());
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_type_details, args);
        });

        holder.tvName.setText(namesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return namesList.size();
    }

    static class NameViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.type_name);
        }
    }
}
