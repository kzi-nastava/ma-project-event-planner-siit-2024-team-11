package com.example.eventy.adapters.events;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.events.budget.BudgetItemEditDialog;
import com.example.eventy.events.budget.BudgetItemSolutionSelectionDialog;
import com.example.eventy.events.model.BudgetItem;
import com.example.eventy.solutions.model.SolutionHistory;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetItemAdapter extends RecyclerView.Adapter<BudgetItemAdapter.BudgetItemViewHolder> {

    private List<BudgetItem> budgetItems;
    private List<SolutionHistory> reservedSolutions;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final Long eventId;
    private final NavController navController;

    public BudgetItemAdapter(Context context, List<BudgetItem> budgetItems, Long eventId, NavController navController) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.budgetItems = budgetItems;
        this.eventId = eventId;
        this.navController = navController;
    }

    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_holder_budget_item, parent, false);
        return new BudgetItemAdapter.BudgetItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        BudgetItem budgetItem = budgetItems.get(position);
        holder.categoryName.setText(budgetItem.getCategory());
        holder.allocatedFunds.setText(String.format("%.2f", budgetItem.getPlannedFunds()));

        reservedSolutions = budgetItem.getBudgetedEntries();
        calculateUsedFunds(holder);

        holder.editButton.setOnClickListener(v -> {
            BudgetItemEditDialog dialog = new BudgetItemEditDialog(context, budgetItem.getPlannedFunds(), newAllocatedFunds -> {
                Call<BudgetItem> editCall = ClientUtils.budgetService.updateBudgetItemFunds(budgetItem.getId(), newAllocatedFunds);
                editCall.enqueue(new Callback<BudgetItem>() {
                    @Override
                    public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            budgetItem.setPlannedFunds(response.body().getPlannedFunds());
                            holder.allocatedFunds.setText(String.format("%.2f", response.body().getPlannedFunds()));
                            calculateUsedFunds(holder);
                        }
                    }

                    @Override
                    public void onFailure(Call<BudgetItem> call, Throwable t) {

                    }
                });
            });
            dialog.show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (budgetItem.getBudgetedEntries().isEmpty()) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to delete the " + budgetItem.getCategory() + " budget item?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Boolean> deleteCall = ClientUtils.budgetService.removeBudgetItem(eventId, budgetItem.getId());
                                deleteCall.enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        if (response.isSuccessful()) {
                                            budgetItems.remove(budgetItem);
                                            notifyDataSetChanged();
                                        } else {
                                            new AlertDialog.Builder(context)
                                                    .setMessage("Error deleting budget item.")
                                                    .setCancelable(true)
                                                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        new AlertDialog.Builder(context)
                                                .setMessage("Connection error, try again!")
                                                .setCancelable(true)
                                                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                                .show();
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
            } else {
                new AlertDialog.Builder(context)
                        .setMessage("Cannot delete a budget item that has reserved items. Delete the reserved items first!")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                        .show();
            }
        });

        BudgetItemSolutionsAdapter subAdapter = new BudgetItemSolutionsAdapter(context, reservedSolutions, budgetItem);
        holder.reservedSolutions.setAdapter(subAdapter);
        holder.reservedSolutions.setLayoutManager(new LinearLayoutManager(context));
        if (reservedSolutions.isEmpty()) {
            holder.reservedSolutions.setVisibility(View.GONE);
        }

        holder.addSolutionButton.setOnClickListener(v -> {
            BudgetItemSolutionSelectionDialog dialog = new BudgetItemSolutionSelectionDialog(context, budgetItem.getCategory(), navController);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return budgetItems.size();
    }

    public void calculateUsedFunds(BudgetItemViewHolder holder) {
        Double sum = reservedSolutions.stream().mapToDouble(v -> v.getPrice() - (v.getPrice() * v.getDiscount() / 100)).sum();
        holder.usedFunds.setText(String.format("%.2f", sum));
        Double remainder = Double.parseDouble(holder.allocatedFunds.getText().toString()) - sum;
        if (remainder < 0) {
            holder.remainingFunds.setText("OVERBUDGET");
        } else {
            holder.remainingFunds.setText(String.format("%.2f", remainder));
        }
        if (reservedSolutions.isEmpty()) {
            holder.reservedSolutions.setVisibility(View.GONE);
        } else {
            holder.reservedSolutions.setVisibility(View.VISIBLE);
        }
    }

    public static class BudgetItemViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        TextView allocatedFunds;
        TextView usedFunds;
        TextView remainingFunds;
        MaterialButton editButton;
        MaterialButton deleteButton;
        RecyclerView reservedSolutions;
        Button addSolutionButton;
        public BudgetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.view_holder_budget_item_category_name);
            allocatedFunds = itemView.findViewById(R.id.view_holder_budget_item_allocated_funds);
            usedFunds = itemView.findViewById(R.id.view_holder_budget_item_used_funds);
            remainingFunds = itemView.findViewById(R.id.view_holder_budget_item_remainder);
            editButton = itemView.findViewById(R.id.view_holder_budget_item_edit_button);
            deleteButton = itemView.findViewById(R.id.view_holder_budget_item_delete_button);
            reservedSolutions = itemView.findViewById(R.id.view_holder_budget_item_recycler);
            addSolutionButton = itemView.findViewById(R.id.view_holder_budget_item_add_solution_button);
        }
    }
}
