package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.example.eventy.R;
import com.example.eventy.reviews.model.Review;

public class ReviewDetailsDialog extends Dialog implements View.OnClickListener {
    public ImageView closeButton;
    private String title;
    private Review review;

    public ReviewDetailsDialog(Activity a, String title, Review review) {
        super(a);
        this.title = title;
        this.review = review;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_review_details);
        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);

        setupDialogDetails();
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView fromTextView = findViewById(R.id.from_email);
        fromTextView.setText(review.getSenderEmail());

        TextView forTextView = findViewById(R.id.for_email);
        forTextView.setText(review.getRecipientEmail());

        int grade = review.getGrade();

        AppCompatButton star1 = findViewById(R.id.star1);
        AppCompatButton star2 = findViewById(R.id.star2);
        AppCompatButton star3 = findViewById(R.id.star3);
        AppCompatButton star4 = findViewById(R.id.star4);
        AppCompatButton star5 = findViewById(R.id.star5);

        star1.setBackgroundResource(R.drawable.icon_star_gold);
        star2.setBackgroundResource(grade >= 2 ? R.drawable.icon_star_gold : R.drawable.icon_star_grey);
        star3.setBackgroundResource(grade >= 3 ? R.drawable.icon_star_gold : R.drawable.icon_star_grey);
        star4.setBackgroundResource(grade >= 4 ? R.drawable.icon_star_gold : R.drawable.icon_star_grey);
        star5.setBackgroundResource(grade >= 5 ? R.drawable.icon_star_gold : R.drawable.icon_star_grey);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
