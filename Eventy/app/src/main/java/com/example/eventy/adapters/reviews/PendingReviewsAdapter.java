package com.example.eventy.adapters.reviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.ReviewDetailsDialog;
import com.example.eventy.reviews.PendingReviewsFragment;
import com.example.eventy.reviews.model.Review;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReviewsAdapter extends RecyclerView.Adapter<PendingReviewsAdapter.NotificationViewHolder> {
    private ArrayList<Review> pendingReviews;
    private LayoutInflater layoutInflater;
    private PendingReviewsFragment pendingReviewsFragment;

    public PendingReviewsAdapter(Context context, ArrayList<Review> pendingReviews, PendingReviewsFragment pendingReviewsFragment) {
        this.pendingReviews = pendingReviews;
        this.layoutInflater = LayoutInflater.from(context);
        this.pendingReviewsFragment = pendingReviewsFragment;
    }

    @NonNull
    @Override
    public PendingReviewsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_pending_review, parent, false);

        return new PendingReviewsAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Review review = pendingReviews.get(position);

        if (review != null) {
            holder.comment.setText(review.getComment());

            holder.seeDetails.setOnClickListener(view -> {
                ReviewDetailsDialog reviewDetailsDialog = new ReviewDetailsDialog(
                    (Activity) holder.itemView.getContext(),
                    "REVIEW DETAILS",
                    review);
                reviewDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reviewDetailsDialog.show();
            });

            holder.acceptReview.setOnClickListener(view -> {
                Call<Review> call = ClientUtils.reviewService.acceptReview(review.getId());
                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            pendingReviewsFragment.fetchPendingReviews(0, pendingReviewsFragment.getPageSize());
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {}
                });
            });

            holder.declineReview.setOnClickListener(view -> {
                Call<Review> call = ClientUtils.reviewService.declineReview(review.getId());
                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            pendingReviewsFragment.fetchPendingReviews(0, pendingReviewsFragment.getPageSize());
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {}
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return pendingReviews.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView comment;
        AppCompatButton seeDetails, acceptReview, declineReview;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment_value);
            seeDetails = itemView.findViewById(R.id.see_details_button);
            acceptReview = itemView.findViewById(R.id.accept_review_button);
            declineReview = itemView.findViewById(R.id.decline_review_button);
        }
    }

    public interface OnReviewActionListener {
        void onReviewAction();
    }
}