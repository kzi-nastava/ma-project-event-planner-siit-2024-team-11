package com.example.eventy.adapters.solutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.SolutionCategoryDialog;
import com.example.eventy.model.enums.Status;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolutionCategoryAdapter extends RecyclerView.Adapter<SolutionCategoryAdapter.SolutionCategoryViewHolder>{

    private List<CategoryWithID> categories = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public SolutionCategoryAdapter(Context context, List<CategoryWithID> categories) {
        if (categories != null) {
            this.categories = categories;
        }
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
        CategoryWithID category = categories.get(position);
        if (category != null) {
            holder.id = category.getId();
            holder.name.setText(category.getName());
            holder.description = category.getDescription();
            holder.status = category.getStatus();

            Button editButton = holder.itemView.findViewById(R.id.category_card_edit_button);
            editButton.setOnClickListener(v -> {
                SolutionCategoryDialog dialog = new SolutionCategoryDialog(holder.itemView.getContext(), holder.id, holder.name.getText().toString(), holder.description, (idValue, nameValue, descriptionValue) -> {
                    Call<CategoryWithID> call = ClientUtils.categoryService.updateCategory(new CategoryWithID(holder.id, nameValue, descriptionValue, holder.status));
                    call.enqueue(new Callback<CategoryWithID>() {
                        @Override
                        public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                            if (response.isSuccessful()) {
                                holder.name.setText(nameValue);
                                holder.description = descriptionValue;
                                Toast.makeText(holder.itemView.getContext(), "Category updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Error with updating category!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryWithID> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "Error with updating category!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                dialog.show();
                Toast.makeText(holder.itemView.getContext(), "EDIT DIALOG OPENED", Toast.LENGTH_SHORT).show();
            });
            // TODO

            Button deleteButton = holder.itemView.findViewById(R.id.category_card_delete_button);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Are you sure you want to delete this item?")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", (dialog, id) -> {
                            Call<Void> call = ClientUtils.categoryService.deleteCategory(category.getId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(holder.itemView.getContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to delete category", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(holder.itemView.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> {})
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public static class SolutionCategoryViewHolder extends RecyclerView.ViewHolder {

        Long id;
        TextView name;
        String description;
        Status status;

        public SolutionCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_card_category_name);
        }
    }
}
