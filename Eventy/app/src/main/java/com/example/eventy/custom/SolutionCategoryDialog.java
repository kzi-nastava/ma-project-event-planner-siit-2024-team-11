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

public class SolutionCategoryDialog extends Dialog implements android.view.View.OnClickListener{

    private Long id;
    private String name;
    private String description;
    private EditText editTextName;
    private EditText editTextDescription;
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
            Long idValue = this.id;
            String nameValue = editTextName.getText().toString();
            String descriptionValue = editTextDescription.getText().toString();

            if (!nameValue.isEmpty() && !descriptionValue.isEmpty()) {
                if (callback != null) {
                    callback.onCategoryDataReceived(idValue, nameValue, descriptionValue);
                }
                dismiss();
            } else {
                if (getContext() instanceof Activity) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Both name and description must be written!")
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
