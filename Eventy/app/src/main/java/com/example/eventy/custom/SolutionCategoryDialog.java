package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.eventy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SolutionCategoryDialog extends Dialog implements android.view.View.OnClickListener{

    private Long id;
    private String name;
    private String description;
    private TextInputLayout nameLayout;
    private TextInputLayout descriptionLayout;
    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private MaterialButton confirmButton;
    private MaterialButton cancelButton;

    public interface SolutionCategoryDataListener {
        void onCategoryDataReceived(Long id, String name, String description);
    }

    private SolutionCategoryDataListener callback;

    public SolutionCategoryDialog(@NonNull Context context, Long id, String name, String description, SolutionCategoryDataListener callback) {
        super(context);
        this.id = id != null ? id : -1L;
        this.name = name != null? name : "";
        this.description = description != null ? description : "";
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_solution_category);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        nameLayout = findViewById(R.id.nameLayout);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        editTextName = findViewById(R.id.nameTextBox);
        editTextDescription = findViewById(R.id.descriptionTextBox);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        editTextName.setText(name);
        editTextDescription.setText(description);

        if (name.isEmpty()) {
            confirmButton.setText("Create");
        } else {
            confirmButton.setText("Save changes");
        }

        confirmButton.setOnClickListener(v -> {
            if (isValid()) {
                Long idValue = this.id;
                String nameValue = editTextName.getText().toString();
                String descriptionValue = editTextDescription.getText().toString();
                if (callback != null) {
                    callback.onCategoryDataReceived(idValue, nameValue, descriptionValue);
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

        setupValidation();
    }

    private boolean validate(TextInputLayout layout, TextInputEditText editText) {
        if (String.valueOf(editText.getText()).isEmpty()) {
            layout.setError("Name is required");
            layout.setErrorEnabled(true);
            return false;
        } else {
            layout.setError(null);
            layout.setErrorEnabled(false);
            return true;
        }
    }

    private void setupValidation() {
        editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validate(nameLayout, editTextName);
            }
        });

        editTextDescription.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validate(descriptionLayout, editTextDescription);
            }
        });
    }

    private boolean isValid() {
        return validate(nameLayout, editTextName) && validate(descriptionLayout, editTextDescription);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
