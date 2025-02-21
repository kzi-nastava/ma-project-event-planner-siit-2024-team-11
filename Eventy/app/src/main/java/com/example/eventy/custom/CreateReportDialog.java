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
import com.example.eventy.users.model.CreateReport;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReportDialog extends Dialog implements View.OnClickListener {
    public AppCompatButton closeButton;
    private String title;
    private String message;
    private CreateReport createReport;

    public CreateReportDialog(Activity a, String title, String message, CreateReport createReport) {
        super(a);
        this.title = title;
        this.message = message;
        this.createReport = createReport;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_report);
        closeButton = (AppCompatButton) findViewById(R.id.confirm_button);
        closeButton.setOnClickListener(this);

        addValidation(findViewById(R.id.report_message_container), findViewById(R.id.report_message), this::validateRequired);
        setupDialogDetails();
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
        TextInputLayout reportMessageInputLayout = findViewById(R.id.report_message_container);
        TextInputEditText reportMessageInputEditText = findViewById(R.id.report_message);

        if (reportMessageInputLayout.getError() == null) {
            this.createReport.setReason(String.valueOf(reportMessageInputEditText.getText()));

            Call<CreateReport> call = ClientUtils.reportService.createReport(this.createReport);
            call.enqueue(new Callback<CreateReport>() {
                @Override
                public void onResponse(Call<CreateReport> call, Response<CreateReport> response) {
                    if (response.isSuccessful() && response.body() != null) {
                       dismiss();

                    } else {
                        showErrorDialog("Error while creating a report!");
                        showErrorDialog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<CreateReport> call, Throwable t) {
                    showErrorDialog("Error while creating a report!");
                    showErrorDialog(t.getMessage());
                }
            });
        }
    }

    private void showErrorDialog(String message) {
        Activity activity = getOwnerActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(() -> {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(activity, "Error", message);
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            });
        }
    }
}
