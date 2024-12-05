package com.example.eventy.home.events;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.eventy.R;

public class EventDetailsDialog extends Dialog implements
        android.view.View.OnClickListener {
    public ImageView closeButton;

    public EventDetailsDialog(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_event_details_for_reservation);
        closeButton = (ImageView) findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
