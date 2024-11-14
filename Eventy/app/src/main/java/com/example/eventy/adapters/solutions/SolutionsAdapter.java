package com.example.eventy.adapters.solutions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;

import java.util.ArrayList;

public class SolutionsAdapter extends RecyclerView.Adapter<SolutionsAdapter.SolutionViewHolder> {
    private ArrayList<Solution> solutions;
    private LayoutInflater layoutInflater;

    public SolutionsAdapter(Context context, ArrayList<Solution> solutions) {
        this.solutions = solutions;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SolutionsAdapter.SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (solutions.get(viewType) instanceof Service) {
            view = layoutInflater.inflate(R.layout.fragment_service_card, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.fragment_product_card, parent, false);
        }

        return new SolutionsAdapter.SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionsAdapter.SolutionViewHolder holder, int position) {
        Solution solution = solutions.get(position);

        Log.wtf("OVDEEE", solution.toString());

        if (solution != null) {
            holder.name.setText(solution.getName());

            String categoryString = "Type: " + solution.getCategory().getName();
            holder.category.setText(categoryString);

            String currentPriceString = String.valueOf(solution.getPrice());
            holder.discount.setText(currentPriceString);
            holder.crossedOutPrice.setText(currentPriceString);

            double discountedPrice = solution.getPrice() - solution.getPrice() * solution.getDiscount() / 100;
            String discountedPriceString = String.format("%.2f", discountedPrice);
            holder.price.setText(discountedPriceString);

            View discountContainer = holder.itemView.findViewById(R.id.discount_container);
            if (solution.getDiscount() == 0) {
                discountContainer.setVisibility(View.GONE);
            } else {
                discountContainer.setVisibility(View.VISIBLE);
            }

            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "See more: " + solution.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Favorite: " + solution.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private int convertDpToPx(int dp, View itemView) {
        return (int) (dp * itemView.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class SolutionViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price, crossedOutPrice, discount;

        public SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.current_price);
            crossedOutPrice = itemView.findViewById(R.id.crossed_out_price);
            discount = itemView.findViewById(R.id.before_price);
            //image = itemView.findViewById(R.id.image);
        }
    }
}
