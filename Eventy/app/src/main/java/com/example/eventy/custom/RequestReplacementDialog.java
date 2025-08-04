package com.example.eventy.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.eventy.R;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestReplacementDialog extends Dialog implements View.OnClickListener {

    public interface RequestReplacementDataListener {
        void onDataReceived(Long replacementId);
    }

    private RequestReplacementDataListener callback;
    private List<CategoryWithID> categories;
    private MaterialButton confirmButton;
    private MaterialButton cancelButton;
    private TextInputLayout replacementLayout;
    private MaterialAutoCompleteTextView replacementSpinner;
    private CategoryWithID selectedCategory;

    public RequestReplacementDialog(@NonNull Context context, RequestReplacementDataListener callback) {
        super(context);
        this.callback = callback;
        this.categories = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request_replacement);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        replacementSpinner = findViewById(R.id.replacement_category_spinner);
        replacementLayout = findViewById(R.id.replacement_category_spinner_layout);

        Call<List<CategoryWithID>> call = ClientUtils.categoryService.getActiveCategories();
        call.enqueue(new Callback<List<CategoryWithID>>() {
            @Override
            public void onResponse(Call<List<CategoryWithID>> call, Response<List<CategoryWithID>> response) {
                if (response.isSuccessful()) {
                    categories.clear();
                    categories.addAll(response.body());

                    ArrayAdapter<CategoryWithID> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
                    replacementSpinner.setAdapter(adapter);
                    replacementSpinner.setOnItemClickListener((parent, view, position, id) -> {
                        selectedCategory = (CategoryWithID) parent.getItemAtPosition(position);
                    });
                    replacementSpinner.setOnClickListener(v -> {
                        if (!replacementSpinner.isPopupShowing()) {
                            replacementSpinner.showDropDown();
                        }
                    });
                }
                else {
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
        });

        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            if (isValid()) {
                callback.onDataReceived(selectedCategory.getId());
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
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

    }

    private void setupValidation() {
        replacementSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateCategory();
            }
        });
    }

    private boolean validateCategory() {
        if (selectedCategory == null) {
            replacementLayout.setError("Category is required");
            replacementLayout.setErrorEnabled(true);
            return false;
        } else {
            replacementLayout.setError(null);
            replacementLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isValid() {
        return validateCategory();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
