package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventy.R;

public class ErrorOkDialog extends Dialog implements
        android.view.View.OnClickListener {
    public ImageView closeButton;
    private String title;
    private String message;

    public ErrorOkDialog(Activity a, String title, String message) {
        super(a);
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_error_ok);
        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);

        setupDialogDetails();
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView messageTextView = findViewById(R.id.message);
        messageTextView.setText(message);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
