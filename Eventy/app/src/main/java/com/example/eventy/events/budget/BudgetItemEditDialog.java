package com.example.eventy.events.budget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.eventy.R;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetItemEditDialog extends Dialog implements View.OnClickListener {
    public interface BudgetItemEditDataListener {
        void onDataReceived(Double allocatedFunds);
    }

    private BudgetItemEditDataListener callback;
    private Double currentAllocated;
    private Button cancelButton;
    private Button confirmButton;
    private TextInputEditText inputAllocatedFunds;

    public BudgetItemEditDialog(@NonNull Context context, Double currentAllocated, BudgetItemEditDataListener callback) {
        super(context);
        this.callback = callback;
        this.currentAllocated = currentAllocated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_budget_item_edit);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmButton = findViewById(R.id.dialog_budget_item_edit_confirm_button);
        cancelButton = findViewById(R.id.dialog_budget_item_edit_cancel_button);
        inputAllocatedFunds = findViewById(R.id.dialog_budget_item_edit_allocated_funds);

        inputAllocatedFunds.setText(String.valueOf(currentAllocated));

        confirmButton.findViewById(R.id.dialog_budget_item_confirm_button);
        confirmButton.setOnClickListener(v -> {
            if (inputAllocatedFunds.getText().toString().isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Allocated funds are required!")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                        .show();
            } else {
                try {
                    Double allocatedFunds = Double.parseDouble(inputAllocatedFunds.getText().toString());
                    if (allocatedFunds <= 0) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Allocated funds must be greater than 0!")
                                .setCancelable(true)
                                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                .show();
                    } else {
                        if (callback != null) {
                            callback.onDataReceived(allocatedFunds);
                        }
                        dismiss();
                    }
                } catch (Exception e) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Allocated funds must be a number!")
                            .setCancelable(true)
                            .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                            .show();
                }
            }
        });

        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
