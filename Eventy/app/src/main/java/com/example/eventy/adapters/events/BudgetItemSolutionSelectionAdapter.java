package com.example.eventy.adapters.events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.events.budget.BudgetItemSolutionSelectionDialog;
import com.example.eventy.solutions.enums.SolutionType;
import com.example.eventy.solutions.model.SolutionCard;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BudgetItemSolutionSelectionAdapter extends RecyclerView.Adapter<BudgetItemSolutionSelectionAdapter.BudgetItemSolutionSelectionViewHolder> {

    private LayoutInflater layoutInflater;
    private List<SolutionCard> solutions;
    private NavController navController;
    private BudgetItemSolutionSelectionDialog parent;
    public BudgetItemSolutionSelectionAdapter(Context context, List<SolutionCard> solutions, NavController navController, BudgetItemSolutionSelectionDialog parent) {
        layoutInflater = LayoutInflater.from(context);
        this.solutions = solutions;
        this.navController = navController;
        this.parent = parent;
    }

    @NonNull
    @Override
    public BudgetItemSolutionSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_holder_budget_item_solution_selection, parent, false);
        return new BudgetItemSolutionSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemSolutionSelectionViewHolder holder, int position) {
        SolutionCard solutionCard = solutions.get(position);

        holder.name.setText(solutionCard.getName());
        holder.providerName.setText(solutionCard.getProviderName());

        String currentPriceString = String.valueOf(solutionCard.getPrice());
        holder.discount.setText(currentPriceString);
        holder.crossedOutPrice.setText(currentPriceString);

        double discountedPrice = solutionCard.getPrice() - solutionCard.getPrice() * solutionCard.getDiscount() / 100;
        String discountedPriceString = String.format("%.2f", discountedPrice);
        holder.price.setText(discountedPriceString);

        View discountContainer = holder.itemView.findViewById(R.id.discount_container);
        if (solutionCard.getDiscount() == 0) {
            discountContainer.setVisibility(View.GONE);
        } else {
            discountContainer.setVisibility(View.VISIBLE);
        }

        holder.getButton.setOnClickListener(v -> {
            if (solutionCard.getType() == SolutionType.SERVICE) {
                Bundle args = new Bundle();
                args.putLong("serviceId", solutionCard.getSolutionId());
                parent.dismiss();
                navController.navigate(R.id.service_reservation, args);
            } else {
                Bundle args = new Bundle();
                args.putLong("productId", solutionCard.getSolutionId());
                args.putString("productName", solutionCard.getName());
                parent.dismiss();
                navController.navigate(R.id.nav_purchase, args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    public static class BudgetItemSolutionSelectionViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView crossedOutPrice;
        TextView discount;
        TextView price;
        TextView providerName;
        MaterialButton getButton;
        public BudgetItemSolutionSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.view_holder_budget_item_solution_name);
            crossedOutPrice = itemView.findViewById(R.id.crossed_out_price);
            discount = itemView.findViewById(R.id.before_price);
            price = itemView.findViewById(R.id.current_price);
            providerName = itemView.findViewById(R.id.view_holder_budget_item_solution_provider_name);
            getButton = itemView.findViewById(R.id.view_holder_budget_item_solution_get_button);
        }
    }
}
