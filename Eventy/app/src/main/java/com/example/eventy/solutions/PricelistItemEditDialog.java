package com.example.eventy.solutions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.eventy.R;
import com.google.android.material.button.MaterialButton;

public class PricelistItemEditDialog extends Dialog {
    private Long id;
    private String name;
    private Double price;
    private Double discount;
    private EditText newPriceTextbox;
    private EditText newDiscountTextbox;
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

        newPriceTextbox = findViewById(R.id.pricelist_item_price_textbox);
        newDiscountTextbox = findViewById(R.id.pricelist_item_discount_textbox);
        confirmButton = findViewById(R.id.pricelist_item_confirm_button);
        cancelButton = findViewById(R.id.pricelist_item_cancel_button);

        newPriceTextbox.setText(this.price.toString());
        newDiscountTextbox.setText(this.discount.toString());

        confirmButton.setOnClickListener(v -> {
            if (!newPriceTextbox.getText().toString().isEmpty() && !newDiscountTextbox.getText().toString().isEmpty()) {
                Long idValue = this.id;
                try {
                    Double priceValue = Double.parseDouble(newPriceTextbox.getText().toString());
                    Double discountValue = Double.parseDouble(newDiscountTextbox.getText().toString());

                    if (priceValue < 0) {
                        if (getContext() instanceof Activity) {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("Price can't be less than 0!")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                    .show();
                        }
                    } else if (discountValue < 0 || discountValue > 100) {
                        if (getContext() instanceof Activity) {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("Discount must be between 0 and 100!")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                    .show();
                        }
                    } else {
                        if (callback != null) {
                            callback.onDataReceived(idValue, priceValue, discountValue);
                        }
                        dismiss();
                    }

                } catch (NumberFormatException e) {
                    if (getContext() instanceof Activity) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("The input values must be numbers!")
                                .setCancelable(true)
                                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                                .show();
                    }
                }

            } else {
                if (getContext() instanceof Activity) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("You can't leave the textboxes empty!")
                            .setCancelable(true)
                            .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                            .show();
                }
            }

        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}
