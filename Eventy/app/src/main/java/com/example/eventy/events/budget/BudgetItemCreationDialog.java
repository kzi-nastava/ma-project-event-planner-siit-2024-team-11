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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetItemCreationDialog extends Dialog implements View.OnClickListener {

    public interface BudgetItemCreationDataListener {
        void onDataReceived(Long categoryId, Double allocatedFunds);
    }

    private BudgetItemCreationDataListener callback;
    private Long eventId;
    private Long categoryId;
    private List<CategoryWithID> categories = new ArrayList<>();
    private MaterialAutoCompleteTextView categorySpinner;
    private Button cancelButton;
    private Button confirmButton;
    private TextView title;
    private TextView noContentMessage;
    private TextInputLayout inputLayout;
    private TextInputLayout categoryLayout;
    private LinearLayout buttonLayout;
    private TextInputEditText inputAllocatedFunds;

    public BudgetItemCreationDialog(@NonNull Context context, Long eventId, BudgetItemCreationDataListener callback) {
        super(context);
        this.callback = callback;
        this.eventId = eventId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_budget_item_creation);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryLayout = findViewById(R.id.dialog_budget_item_spinner_layout);
        categorySpinner = findViewById(R.id.dialog_budget_item_spinner);
        title = findViewById(R.id.dialog_budget_item_title);
        confirmButton = findViewById(R.id.dialog_budget_item_confirm_button);
        cancelButton = findViewById(R.id.dialog_budget_item_cancel_button);
        noContentMessage = findViewById(R.id.dialog_budget_item_no_content_message);
        inputLayout = findViewById(R.id.dialog_budget_item_allocated_funds_layout);
        buttonLayout = findViewById(R.id.dialog_budget_item_button_layout);
        inputAllocatedFunds = findViewById(R.id.dialog_budget_item_allocated_funds);

        Call<List<CategoryWithID>> call = ClientUtils.categoryService.getAllRemaining(eventId);
        call.enqueue(
                new Callback<List<CategoryWithID>>() {
                    @Override
                    public void onResponse(Call<List<CategoryWithID>> call, Response<List<CategoryWithID>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categories.clear();
                            categories.addAll(response.body());
                            if (categories.isEmpty()) {
                                handleEmptyList();
                            }
                            ArrayAdapter<CategoryWithID> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
                            categorySpinner.setAdapter(adapter);
                            categorySpinner.setOnItemClickListener((parent, view, position, id) -> {
                                CategoryWithID selectedCategory = (CategoryWithID) parent.getItemAtPosition(position);
                                categoryId = selectedCategory.getId();
                            });
                            categorySpinner.setOnClickListener(v -> {
                                if (!categorySpinner.isPopupShowing()) {
                                    categorySpinner.showDropDown();
                                }
                            });
                        } else {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("Error while loading categories!")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryWithID>> call, Throwable t) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Error while loading categories!")
                                .setCancelable(true)
                                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                .show();
                    }
                }
        );
        confirmButton.findViewById(R.id.dialog_budget_item_confirm_button);
        confirmButton.setOnClickListener(v -> {
            if (isValid()) {
                Double allocatedFunds = Double.parseDouble(inputAllocatedFunds.getText().toString());
                if (callback != null) {
                    callback.onDataReceived(categoryId, allocatedFunds);
                }
                dismiss();
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage("Not every field is valid!")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                        .show();
            }
        });

        setupValidation();
        cancelButton.setOnClickListener(this);
    }

    private void handleEmptyList() {
        categorySpinner.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
        noContentMessage.setVisibility(View.VISIBLE);
    }

    private void setupValidation() {
        inputAllocatedFunds.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateAllocatedFunds();
            }
        });

        categorySpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateCategory();
            }
        });
    }

    private boolean isValid() {
        boolean valid = validateAllocatedFunds();
        valid = validateCategory() && valid;
        return valid;
    }

    private boolean validateAllocatedFunds() {
        if (String.valueOf(inputAllocatedFunds.getText()).isEmpty()) {
            inputLayout.setError("Allocated funds is required");
            inputLayout.setErrorEnabled(true);
            return false;
        }

        try {
            Double priceValue = Double.parseDouble(inputAllocatedFunds.getText().toString());
            if (priceValue <= 0) {
                inputLayout.setError("Allocated funds must be greater than 0");
                inputLayout.setErrorEnabled(true);
                return false;
            } else {
                inputLayout.setError(null);
                inputLayout.setErrorEnabled(false);
                return true;
            }
        } catch (Exception e) {
            inputLayout.setError("Allocated funds must be a number");
            inputLayout.setErrorEnabled(true);
            return false;
        }
    }

    private boolean validateCategory() {
        if (categoryId == null) {
            categoryLayout.setError("Category is required");
            categoryLayout.setErrorEnabled(true);
            return false;
        } else {
            categoryLayout.setError(null);
            categoryLayout.setErrorEnabled(false);
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }
}
