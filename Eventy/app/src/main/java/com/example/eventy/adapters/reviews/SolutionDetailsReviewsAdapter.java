package com.example.eventy.adapters.reviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.reviews.model.Review;

import java.util.List;

public class SolutionDetailsReviewsAdapter extends RecyclerView.Adapter<SolutionDetailsReviewsAdapter.SolutionDetailsReviewsViewHolder> {

    private List<Review> reviewList;
    private LayoutInflater layoutInflater;
    private Context context;

    public SolutionDetailsReviewsAdapter(@NonNull Context context, List<Review> reviewList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public SolutionDetailsReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_holder_solution_details_review, parent, false);
        return new SolutionDetailsReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionDetailsReviewsViewHolder holder, int position) {
        Review review = reviewList.get(position);

        Drawable picture = PictureHelperService.getPicture(review.getSenderAvatar(), context);
        if (picture != null) {
            holder.senderImage.setBackground(picture);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(review.getSenderName());
        builder.append(" gave this solution ");
        int i = 1;
        for (; i <= review.getGrade(); i++) {
            builder.append("★");
        }
        for (; i <= 5; i++) {
            builder.append("☆");
        }
        holder.senderName.setText(builder.toString());
        holder.comment.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class SolutionDetailsReviewsViewHolder extends RecyclerView.ViewHolder {

        ImageView senderImage;
        TextView senderName;
        TextView comment;

        public SolutionDetailsReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.view_holder_review_provider_image);
            senderName = itemView.findViewById(R.id.view_holder_review_provider_name);
            comment = itemView.findViewById(R.id.view_holder_review_comment);
        }
    }
}
