package com.example.eventy.solutions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.eventy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class PricelistItemEditDialog extends Dialog {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private TextInputLayout newPriceLayout;
    private TextInputLayout newDiscountLayout;
    private TextInputEditText newPriceTextbox;
    private TextInputEditText newDiscountTextbox;
    private MaterialButton confirmButton;
    private MaterialButton cancelButton;
    private PricelistItemEditDataListener callback;

    public interface PricelistItemEditDataListener {
        void onDataReceived(Long id, Double newPrice, Double newDiscount);
    }

    public PricelistItemEditDialog(@NonNull Context context, Long id, String name, Double price, Double discount, PricelistItemEditDataListener callback) {
        super(context);
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pricelist_item_edit);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        newPriceLayout = findViewById(R.id.pricelist_item_price_layout);
        newDiscountLayout = findViewById(R.id.pricelist_item_discount_layout);
        newPriceTextbox = findViewById(R.id.pricelist_item_price_textbox);
        newDiscountTextbox = findViewById(R.id.pricelist_item_discount_textbox);
        confirmButton = findViewById(R.id.pricelist_item_confirm_button);
        cancelButton = findViewById(R.id.pricelist_item_cancel_button);

        newPriceTextbox.setText(this.price.toString());
        newDiscountTextbox.setText(this.discount.toString());

        setupValidation();

        confirmButton.setOnClickListener(v -> {
            if (this.isValid()) {
                Long idValue = this.id;
                Double priceValue = Double.parseDouble(newPriceTextbox.getText().toString());
                Double discountValue = Double.parseDouble(newDiscountTextbox.getText().toString());
                if (callback != null) {
                    callback.onDataReceived(idValue, priceValue, discountValue);
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

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }

    private boolean validatePrice() {
        if (String.valueOf(newPriceTextbox.getText()).isEmpty()) {
            newPriceLayout.setError("New price is required");
            newPriceLayout.setErrorEnabled(true);
            return false;
        }

        try {
            Double priceValue = Double.parseDouble(newPriceTextbox.getText().toString());
            if (priceValue <= 0) {
                newPriceLayout.setError("New price must be greater than 0");
                newPriceLayout.setErrorEnabled(true);
                return false;
            } else {
                newPriceLayout.setError(null);
                newPriceLayout.setErrorEnabled(false);
                return true;
            }
        } catch (Exception e) {
            newPriceLayout.setError("New price must be a number");
            newPriceLayout.setErrorEnabled(true);
            return false;
        }
    }

    private boolean validateDiscount() {
        if (String.valueOf(newDiscountTextbox.getText()).isEmpty()) {
            newDiscountLayout.setError("New discount is required");
            newDiscountLayout.setErrorEnabled(true);
            return false;
        }

        try {
            Double discountValue = Double.parseDouble(newDiscountTextbox.getText().toString());
            if (!(discountValue >= 0 && discountValue <= 100)) {
                newDiscountLayout.setError("New discount must be between 0 and 100");
                newDiscountLayout.setErrorEnabled(true);
                return false;
            } else {
                newDiscountLayout.setError(null);
                newDiscountLayout.setErrorEnabled(false);
                return true;
            }
        } catch (Exception e) {
            newDiscountLayout.setError("New discount must be a number");
            newDiscountLayout.setErrorEnabled(true);
            return false;
        }
    }

    private void setupValidation() {
        newPriceTextbox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePrice();
            }
        });

        newDiscountTextbox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateDiscount();
            }
        });
    }

    private boolean isValid() {
        return validatePrice() && validateDiscount();
    }
}
