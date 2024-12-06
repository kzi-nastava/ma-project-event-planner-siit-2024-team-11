package com.example.eventy.adapters.solutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.solution.Category;

import java.util.ArrayList;

public class SolutionCategoryAdapter extends RecyclerView.Adapter<SolutionCategoryAdapter.SolutionCategoryViewHolder>{

    private ArrayList<Category> categories;
    private LayoutInflater layoutInflater;

    public SolutionCategoryAdapter(Context context, ArrayList<Category> categories) {
        this.categories = categories;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SolutionCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.category_accepted_card, parent, false);

        return new SolutionCategoryAdapter.SolutionCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionCategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category != null) {
            holder.name.setText(category.getName());
            holder.description = category.getDescription();
            holder.status = category.getStatus();

            Button editButton = holder.itemView.findViewById(R.id.category_card_edit_button);
            editButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "EDIT DIALOG OPENED", Toast.LENGTH_SHORT).show();
            });

            Button deleteButton = holder.itemView.findViewById(R.id.category_card_delete_button);
            deleteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "DELETE DIALOG OPENED", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public static class SolutionCategoryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        String description;
        Status status;

        public SolutionCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_card_category_name);
        }
    }
}
