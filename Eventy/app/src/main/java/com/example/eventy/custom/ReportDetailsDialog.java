package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventy.R;
import com.example.eventy.users.model.Report;

public class ReportDetailsDialog extends Dialog implements View.OnClickListener {
    public ImageView closeButton;
    private String title;
    private Report report;

    public ReportDetailsDialog(Activity a, String title, Report report) {
        super(a);
        this.title = title;
        this.report = report;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report_details);
        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);

        setupDialogDetails();
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView fromTextView = findViewById(R.id.sender_email);
        String sender = report.getSenderEmail();
        fromTextView.setText(sender == null ? "Unknown sender" : sender);

        TextView forTextView = findViewById(R.id.reported_user);
        String reported = report.getReportedUserEmail();
        forTextView.setText(reported == null ? "Unknown reported user" : reported);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
