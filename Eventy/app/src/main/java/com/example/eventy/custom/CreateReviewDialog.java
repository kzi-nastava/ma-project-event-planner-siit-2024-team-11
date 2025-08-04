package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.example.eventy.R;
import com.example.eventy.reviews.model.CreateReview;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReviewDialog extends Dialog implements View.OnClickListener {
    public AppCompatButton closeButton;
    private String title;
    private String message;
    private CreateReview createReview;
    AppCompatButton star1;
    AppCompatButton star2;
    AppCompatButton star3;
    AppCompatButton star4;
    AppCompatButton star5;
    private int grade = 1;

    public CreateReviewDialog(Activity a, String title, String message, CreateReview createReview) {
        super(a);
        this.title = title;
        this.message = message;
        this.createReview = createReview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_review);
        closeButton = (AppCompatButton) findViewById(R.id.confirm_button);
        closeButton.setOnClickListener(this);

        addValidation(findViewById(R.id.review_message_container), findViewById(R.id.review_message), this::validateRequired);
        setupDialogDetails();

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        star1.setOnClickListener(view -> {
            this.grade = 1;
            star1.setBackgroundResource(R.drawable.icon_star_gold);
            star2.setBackgroundResource(R.drawable.icon_star_grey);
            star3.setBackgroundResource(R.drawable.icon_star_grey);
            star4.setBackgroundResource(R.drawable.icon_star_grey);
            star5.setBackgroundResource(R.drawable.icon_star_grey);
        });
        star2.setOnClickListener(view -> {
            this.grade = 2;
            star1.setBackgroundResource(R.drawable.icon_star_gold);
            star2.setBackgroundResource(R.drawable.icon_star_gold);
            star3.setBackgroundResource(R.drawable.icon_star_grey);
            star4.setBackgroundResource(R.drawable.icon_star_grey);
            star5.setBackgroundResource(R.drawable.icon_star_grey);
        });
        star3.setOnClickListener(view -> {
            this.grade = 3;
            star1.setBackgroundResource(R.drawable.icon_star_gold);
            star2.setBackgroundResource(R.drawable.icon_star_gold);
            star3.setBackgroundResource(R.drawable.icon_star_gold);
            star4.setBackgroundResource(R.drawable.icon_star_grey);
            star5.setBackgroundResource(R.drawable.icon_star_grey);
        });
        star4.setOnClickListener(view -> {
            this.grade = 4;
            star1.setBackgroundResource(R.drawable.icon_star_gold);
            star2.setBackgroundResource(R.drawable.icon_star_gold);
            star3.setBackgroundResource(R.drawable.icon_star_gold);
            star4.setBackgroundResource(R.drawable.icon_star_gold);
            star5.setBackgroundResource(R.drawable.icon_star_grey);
        });
        star5.setOnClickListener(view -> {
            this.grade = 5;
            star1.setBackgroundResource(R.drawable.icon_star_gold);
            star2.setBackgroundResource(R.drawable.icon_star_gold);
            star3.setBackgroundResource(R.drawable.icon_star_gold);
            star4.setBackgroundResource(R.drawable.icon_star_gold);
            star5.setBackgroundResource(R.drawable.icon_star_gold);
        });
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView messageTextView = findViewById(R.id.message);
        messageTextView.setText(message);
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input as the user types
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
            textInputLayout.setErrorEnabled(true);
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        TextInputLayout reviewMessageInputLayout = findViewById(R.id.review_message_container);
        TextInputEditText reviewMessageInputEditText = findViewById(R.id.review_message);

        if (reviewMessageInputLayout.getError() == null) {
            this.createReview.setComment(String.valueOf(reviewMessageInputEditText.getText()));
            this.createReview.setGrade(this.grade);

            Call<CreateReview> call = ClientUtils.reviewService.createReview(this.createReview);
            call.enqueue(new Callback<CreateReview>() {
                @Override
                public void onResponse(Call<CreateReview> call, Response<CreateReview> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        dismiss();

                    } else {
                        showErrorDialog("Error while creating a review!");
                        showErrorDialog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<CreateReview> call, Throwable t) {
                    showErrorDialog("Error while creating a review!");
                    showErrorDialog(t.getMessage());
                }
            });
        }
    }

    private void showErrorDialog(String message) {
        Activity activity = getOwnerActivity(); // Get the hosting activity
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(() -> {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(activity, "Error", message);
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            });
        }
    }
}
