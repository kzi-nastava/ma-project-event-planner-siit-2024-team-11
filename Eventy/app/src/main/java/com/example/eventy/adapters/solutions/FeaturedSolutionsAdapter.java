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

public class FeaturedSolutionsAdapter extends RecyclerView.Adapter<FeaturedSolutionsAdapter.SolutionViewHolder> {
    private ArrayList<Solution> featuredSolutions;
    private LayoutInflater layoutInflater;

    public FeaturedSolutionsAdapter(Context context, ArrayList<Solution> featuredSolutions) {
        this.featuredSolutions = featuredSolutions;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FeaturedSolutionsAdapter.SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Log.wtf("Ovo je inflate: ", featuredSolutions.get(viewType).toString());

        // Choose layout based on Solution type (Product or Service) and position
        if (featuredSolutions.get(viewType) instanceof Service) {
            if (viewType % 2 == 0) {
                view = layoutInflater.inflate(R.layout.fragment_home_featured_service_left, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.fragment_home_featured_service_right, parent, false);
            }
        } else {
            if (viewType % 2 == 0) {
                view = layoutInflater.inflate(R.layout.fragment_home_featured_product_left, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.fragment_home_featured_product_right, parent, false);
            }
        }

        return new FeaturedSolutionsAdapter.SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedSolutionsAdapter.SolutionViewHolder holder, int position) {
        Solution solution = featuredSolutions.get(position);

        if (solution != null) {
            holder.name.setText(solution.getName());

            // Calculate height based on text length
            holder.name.post(() -> {
                int lineCount = holder.name.getLineCount();
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

                if (solution instanceof Service) {
                    if (lineCount > 1) {
                        layoutParams.height = convertDpToPx(220, holder.itemView); // Height for 2 lines

                    } else {
                        layoutParams.height = convertDpToPx(205, holder.itemView); // Default height
                    }
                } else {
                    if (lineCount > 1) {
                        layoutParams.height = convertDpToPx(222, holder.itemView); // Height for 2 lines
                    } else {
                        layoutParams.height = convertDpToPx(207, holder.itemView); // Default height
                    }
                }


                holder.itemView.setLayoutParams(layoutParams);
            });

            //Log.wtf("ovde", solution.toString());

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
                // Show a Toast with the event name
                Toast.makeText(holder.itemView.getContext(), "See more: Solution: " + solution.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                // Show a Toast with the event name
                Toast.makeText(holder.itemView.getContext(), "Favorite: Solution: " + solution.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private int convertDpToPx(int dp, View itemView) {
        return (int) (dp * itemView.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return featuredSolutions.size();
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
