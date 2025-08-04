package com.example.eventy.adapters.events;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.events.model.BudgetItem;
import com.example.eventy.solutions.model.SolutionHistory;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetItemSolutionsAdapter extends RecyclerView.Adapter<BudgetItemSolutionsAdapter.BudgetItemSolutionsViewHolder> {

    private List<SolutionHistory> reservedSolutions;
    private Context context;
    private LayoutInflater layoutInflater;
    private BudgetItem parentHolder;


    public BudgetItemSolutionsAdapter(Context context, List<SolutionHistory> reservedSolutions, BudgetItem parentHolder) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.reservedSolutions = reservedSolutions;
        this.parentHolder = parentHolder;
    }

    @NonNull
    @Override
    public BudgetItemSolutionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_holder_budget_item_solution, parent, false);
        return new BudgetItemSolutionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemSolutionsViewHolder holder, int position) {
        SolutionHistory solutionHistory = reservedSolutions.get(position);

        holder.name.setText(solutionHistory.getName());
        holder.providerName.setText(solutionHistory.getProviderName());

        String currentPriceString = String.valueOf(solutionHistory.getPrice());
        holder.discount.setText(currentPriceString);
        holder.crossedOutPrice.setText(currentPriceString);

        double discountedPrice = solutionHistory.getPrice() - solutionHistory.getPrice() * solutionHistory.getDiscount() / 100;
        String discountedPriceString = String.format("%.2f", discountedPrice);
        holder.price.setText(discountedPriceString);

        View discountContainer = holder.itemView.findViewById(R.id.discount_container);
        if (solutionHistory.getDiscount() == 0) {
            discountContainer.setVisibility(View.GONE);
        } else {
            discountContainer.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to remove " + solutionHistory.getName() + " from the list?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<Boolean> call = ClientUtils.budgetService.removeBudgetItemSolution(parentHolder.getId(), solutionHistory.getId());
                            call.enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    if (response.isSuccessful()) {
                                        reservedSolutions.remove(solutionHistory);
                                        notifyDataSetChanged();
                                    } else {
                                        new AlertDialog.Builder(context)
                                                .setMessage("Error removing solution from the list.")
                                                .setCancelable(true)
                                                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                                .show();
                                        Log.wtf("what", response.toString());
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
        });

    }

    @Override
    public int getItemCount() {
        return reservedSolutions.size();
    }

    public static class BudgetItemSolutionsViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView crossedOutPrice;
        TextView discount;
        TextView price;
        TextView providerName;
        MaterialButton deleteButton;

        public BudgetItemSolutionsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.view_holder_budget_item_solution_name);
            providerName = itemView.findViewById(R.id.view_holder_budget_item_solution_provider_name);
            crossedOutPrice = itemView.findViewById(R.id.crossed_out_price);
            discount = itemView.findViewById(R.id.before_price);
            price = itemView.findViewById(R.id.current_price);
            deleteButton = itemView.findViewById(R.id.view_holder_budget_item_solution_delete_button);
        }
    }
}
