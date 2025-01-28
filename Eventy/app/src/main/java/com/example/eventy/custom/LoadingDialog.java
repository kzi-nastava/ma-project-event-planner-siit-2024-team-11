package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eventy.R;

public class LoadingDialog extends Dialog {
    private String title;
    private String message;

    public LoadingDialog(Activity activity, String title, String message) {
        super(activity);
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        setupDialogDetails();
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView messageTextView = findViewById(R.id.message);
        messageTextView.setText(message);

        // If using a ProgressBar, it will animate automatically.
        ProgressBar loadingSpinner = findViewById(R.id.loading_spinner);
        loadingSpinner.setIndeterminate(true);
    }
}
