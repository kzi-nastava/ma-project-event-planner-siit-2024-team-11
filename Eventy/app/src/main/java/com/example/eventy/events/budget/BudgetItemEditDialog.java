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
    private TextInputLayout allocatedFundsLayout;

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

        allocatedFundsLayout = findViewById(R.id.dialog_budget_item_edit_allocated_funds_layout);
        confirmButton = findViewById(R.id.dialog_budget_item_edit_confirm_button);
        cancelButton = findViewById(R.id.dialog_budget_item_edit_cancel_button);
        inputAllocatedFunds = findViewById(R.id.dialog_budget_item_edit_allocated_funds);

        inputAllocatedFunds.setText(String.valueOf(currentAllocated));

        confirmButton.findViewById(R.id.dialog_budget_item_confirm_button);
        confirmButton.setOnClickListener(v -> {
            if (isValid()) {
                Double allocatedFunds = Double.parseDouble(inputAllocatedFunds.getText().toString());
                if (callback != null) {
                    callback.onDataReceived(allocatedFunds);
                }
                dismiss();
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage("Not all fields are valid!")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                        .show();
            }
        });

        cancelButton.setOnClickListener(this);
    }

    private boolean validateAllocatedFunds() {
        if (String.valueOf(inputAllocatedFunds.getText()).isEmpty()) {
            allocatedFundsLayout.setError("Allocated funds is required");
            allocatedFundsLayout.setErrorEnabled(true);
            return false;
        }

        try {
            Double priceValue = Double.parseDouble(inputAllocatedFunds.getText().toString());
            if (priceValue <= 0) {
                allocatedFundsLayout.setError("Allocated funds must be greater than 0");
                allocatedFundsLayout.setErrorEnabled(true);
                return false;
            } else {
                allocatedFundsLayout.setError(null);
                allocatedFundsLayout.setErrorEnabled(false);
                return true;
            }
        } catch (Exception e) {
            allocatedFundsLayout.setError("Allocated funds must be a number");
            allocatedFundsLayout.setErrorEnabled(true);
            return false;
        }
    }

    private boolean isValid() {
        return validateAllocatedFunds();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
