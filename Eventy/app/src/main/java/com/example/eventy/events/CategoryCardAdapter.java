package com.example.eventy.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.eventy.R;

public class CategoryCardAdapter extends RecyclerView.Adapter<CategoryCardAdapter.NameViewHolder> {

    private final List<String> namesList;

    public CategoryCardAdapter(List<String> namesList) {
        this.namesList = namesList;
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_type_card, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.tvName.setText(namesList.get(position));
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
