package com.example.eventy.adapters.solutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.RequestReplacementDialog;
import com.example.eventy.custom.SolutionCategoryDialog;
import com.example.eventy.model.enums.Status;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolutionCategoryRequestAdapter extends RecyclerView.Adapter<SolutionCategoryRequestAdapter.SolutionCategoryRequestViewHolder> {
    private List<CategoryWithID> requests = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public SolutionCategoryRequestAdapter(Context context, List<CategoryWithID> requests) {
        if (requests != null) {
            this.requests = requests;
        }
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SolutionCategoryRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.category_request_card, parent, false);

        return new SolutionCategoryRequestViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SolutionCategoryRequestViewHolder holder, int position) {
        CategoryWithID request = requests.get(position);
        if (request != null) {
            holder.id = request.getId();
            holder.name.setText(request.getName());
            holder.description = request.getDescription();
            holder.status = request.getStatus();

            ImageButton acceptButton = holder.itemView.findViewById(R.id.category_card_accept_button);
            acceptButton.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Are you sure you want to accept the category " + holder.name.getText().toString() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Accept", (dialog, id) -> {
                            Call<CategoryWithID> call = ClientUtils.categoryService.acceptRequest(holder.id);
                            call.enqueue(new Callback<CategoryWithID>() {
                                @Override
                                public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(holder.itemView.getContext(), "Request accepted!", Toast.LENGTH_SHORT).show();

                                        int positionToRemove = holder.getAdapterPosition();
                                        requests.remove(positionToRemove);
                                        notifyItemRemoved(positionToRemove);
                                    } else {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to accept request!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CategoryWithID> call, Throwable t) {
                                    Toast.makeText(holder.itemView.getContext(), "Network error!", Toast.LENGTH_SHORT);
                                }
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> {})
                        .show();
            });

            ImageButton editButton = holder.itemView.findViewById(R.id.category_card_edit_button);
            editButton.setOnClickListener(v -> {
                SolutionCategoryDialog dialog = new SolutionCategoryDialog(holder.itemView.getContext(), holder.id, holder.name.getText().toString(), holder.description, (idValue, nameValue, descriptionValue) -> {
                    Call<CategoryWithID> call = ClientUtils.categoryService.changeRequest(new CategoryWithID(holder.id, nameValue, descriptionValue, holder.status));
                    call.enqueue(new Callback<CategoryWithID>() {
                        @Override
                        public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(holder.itemView.getContext(), "Request changed and accepted successfully!", Toast.LENGTH_SHORT).show();

                                int positionToRemove = holder.getAdapterPosition();
                                requests.remove(positionToRemove);
                                notifyItemRemoved(positionToRemove);
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Error with changing request!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryWithID> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "Error with changing request!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                dialog.show();
            });

            ImageButton replaceButton = holder.itemView.findViewById(R.id.category_card_replace_button);
            replaceButton.setOnClickListener(v -> {
                RequestReplacementDialog dialog = new RequestReplacementDialog(holder.itemView.getContext(), idValue -> {
                    Call<Boolean> call = ClientUtils.categoryService.replaceRequest(holder.id, idValue);
                    call.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(holder.itemView.getContext(), "Request replaced successfully!", Toast.LENGTH_SHORT).show();

                                int positionToRemove = holder.getAdapterPosition();
                                requests.remove(positionToRemove);
                                notifyItemRemoved(positionToRemove);
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Failed to replace request!", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                dialog.show();
            });
        }
    }

    @Override
    public int getItemCount() { return requests.size(); }

    public static class SolutionCategoryRequestViewHolder extends RecyclerView.ViewHolder {

        Long id;
        TextView name;
        String description;
        Status status;

        public SolutionCategoryRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_card_category_name);
        }
    }
}
