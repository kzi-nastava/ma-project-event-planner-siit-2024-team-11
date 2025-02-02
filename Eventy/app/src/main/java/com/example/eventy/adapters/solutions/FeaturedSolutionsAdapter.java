package com.example.eventy.adapters.solutions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.solutions.SolutionDetailsFragment;
import com.example.eventy.solutions.enums.SolutionType;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturedSolutionsAdapter extends RecyclerView.Adapter<FeaturedSolutionsAdapter.SolutionViewHolder> {
    private ArrayList<SolutionCard> featuredSolutions;
    private LayoutInflater layoutInflater;
    private Context context;

    public FeaturedSolutionsAdapter(Context context, ArrayList<SolutionCard> featuredSolutions) {
        this.context = context;
        this.featuredSolutions = featuredSolutions;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (featuredSolutions.get(viewType).getType().equals(SolutionType.SERVICE)) {
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

        return new SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionViewHolder holder, int position) {
        SolutionCard solutionCard = featuredSolutions.get(position);

        if (solutionCard != null) {
            if (position % 2 == 0) {
                if (solutionCard.getType().equals(SolutionType.PRODUCT)) {
                    View productCardLeft = holder.itemView.findViewById(R.id.product_card_left);
                    productCardLeft.post(() -> {
                        int height = productCardLeft.getHeight();

                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.height = height + convertDpToPx(15, holder.itemView);

                        holder.itemView.setLayoutParams(layoutParams);
                    });
                } else {
                    View serviceCardLeft = holder.itemView.findViewById(R.id.service_card_left);
                    serviceCardLeft.post(() -> {
                        int height = serviceCardLeft.getHeight();

                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.height = height + convertDpToPx(15, holder.itemView);

                        holder.itemView.setLayoutParams(layoutParams);
                    });
                }
            } else {
                if (solutionCard.getType().equals(SolutionType.PRODUCT)) {
                    View productCardRight = holder.itemView.findViewById(R.id.product_card_right);
                    productCardRight.post(() -> {
                        int height = productCardRight.getHeight();

                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.height = height + convertDpToPx(15, holder.itemView);

                        holder.itemView.setLayoutParams(layoutParams);
                    });
                } else {
                    View serviceCardRight = holder.itemView.findViewById(R.id.service_card_right);
                    serviceCardRight.post(() -> {
                        int height = serviceCardRight.getHeight();

                        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                        layoutParams.height = height + convertDpToPx(15, holder.itemView);

                        holder.itemView.setLayoutParams(layoutParams);
                    });
                }
            }

            holder.name.setText('"' + solutionCard.getName() + '"');

            String categoryString = "Type: " + solutionCard.getCategoryName();
            holder.category.setText(categoryString);

            if (solutionCard.getType().equals(SolutionType.PRODUCT)) {
                holder.description.setText(solutionCard.getDescription());
            } else {
                int d1 = solutionCard.getMinReservationTime();
                int d2 = solutionCard.getMaxReservationTime();
                String durationText = "Duration: " + ((d1 == d2) ? d1 : d1 + "-" + d2) + "min";
                holder.duration.setText(durationText);

                String reservationType = solutionCard.getReservationType().equals(ReservationConfirmationType.MANUAL) ? "manual" : "auto";
                holder.reservationType.setText("Reservation: " + reservationType);
            }

            ArrayList<String> eventTypeNames = solutionCard.getEventTypeNames();
            int size = eventTypeNames.size();

            String eventType1 = size >= 1 ? eventTypeNames.get(0) : "";
            holder.eventType1.setText(eventType1);
            holder.eventType2Container.setVisibility(View.GONE);
            holder.dots.setVisibility(View.GONE);

            String eventType2 = "";
            if (size >= 2) {
                eventType2 = eventTypeNames.get(1);
                holder.eventType2Container.setVisibility(View.VISIBLE);
                holder.eventType2.setText(eventType2);
                holder.dots.setVisibility(size > 2 ? View.VISIBLE : View.GONE);
            }

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

            Drawable picture = PictureHelperService.getPicture(solutionCard.getFirstImageUrl(), context);
            if (picture != null) {
                holder.image.setBackground(picture);
            }
            //holder.image.setImageBitmap(PictureHelperService.getPicture(solutionCard.getFirstImageUrl()));

            Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putLong("solutionId", solutionCard.getSolutionId());

                NavController navController = Navigation.findNavController(v);
                navController.popBackStack();
                navController.navigate(R.id.nav_solution_details, args);
            });

            ImageButton favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            if(solutionCard.getIsFavorite()) {
                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller_white));
            }

            favoriteButton.setOnClickListener(v -> {
                Call<Boolean> call = ClientUtils.solutionService.toggleFavorite(solutionCard.getSolutionId());
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            solutionCard.setIsFavorite(!solutionCard.getIsFavorite());

                            if (solutionCard.getIsFavorite()) {
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller_white));
                            } else {
                                favoriteButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.icon_favorite_smaller));
                            }

                            Toast.makeText(holder.itemView.getContext(), (solutionCard.getIsFavorite() ? "Favorite: " : "Removed Favorite: ") + solutionCard.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error", "Please log in to make this your favorite solution.");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog((Activity) holder.itemView.getContext(), "Error", "Please log in to make this your favorite solution.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });
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
        TextView name, category, price, crossedOutPrice, discount, description,
                 duration, reservationType, eventType1, eventType2, dots;
        LinearLayout eventType2Container;
        ShapeableImageView image;

        public SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.current_price);
            crossedOutPrice = itemView.findViewById(R.id.crossed_out_price);
            discount = itemView.findViewById(R.id.before_price);
            description = itemView.findViewById(R.id.description);
            duration = itemView.findViewById(R.id.duration);
            reservationType = itemView.findViewById(R.id.reservation_type);
            eventType1 = itemView.findViewById(R.id.event_type1);
            eventType2 = itemView.findViewById(R.id.event_type2);
            eventType2Container = itemView.findViewById(R.id.event_type_container2);
            image = itemView.findViewById(R.id.image);
            dots = itemView.findViewById(R.id.three_dots);
        }
    }
}
