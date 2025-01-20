package com.example.eventy.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.eventy.R;

import java.util.List;

public class SingleSpinner extends androidx.appcompat.widget.AppCompatSpinner implements DialogInterface.OnCancelListener {
    private List<String> items;
    private String selected; // Default: no selection
    private String defaultText;
    private String filterNameText;
    private ArrayAdapter<String> adapter;
    private ListView itemsListView;
    private boolean isDialogOpen = false;

    public SingleSpinner(Context context) {
        super(context);
    }

    public SingleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        updateAdapter(selected);
    }

    private void updateAdapter(String value) {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{value});
        setAdapter(adapter);
    }

    @Override
    public boolean performClick() {
        if (isDialogOpen) {
            return true; // Block the click
        }
        isDialogOpen = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflate custom layout
        View dialogView = View.inflate(getContext(), R.layout.custom_single_spinner_dialog, null);
        builder.setView(dialogView);

        // Initialize ListView and adapter
        itemsListView = dialogView.findViewById(R.id.single_items_list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, items);
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // pre-select the current selection if any
        if (!selected.equals(defaultText)) {
            int selectedIndex = adapter.getPosition(selected);
            itemsListView.setItemChecked(selectedIndex, true);
        }

        // add search functionality
        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                for (int i = 0; i < itemsListView.getCount(); i++) {
                    if (itemsListView.isItemChecked(i)) {
                        selected = adapter.getItem(i);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s, cnt -> {
                    // Update checked states after filtering
                    for (int i = 0; i < itemsListView.getCount(); i++) {
                        String item = adapter.getItem(i);
                        boolean isSelectedIndex = selected.equals(item);
                        itemsListView.setItemChecked(i, isSelectedIndex);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle OK button click
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            for (int i = 0; i < itemsListView.getCount(); i++) {
                if (itemsListView.isItemChecked(i)) {
                    selected = adapter.getItem(i);
                    break;
                }
            }
            isDialogOpen = false;
            onCancel(dialog);
            dialog.cancel();
        });
        builder.setOnCancelListener(dialog -> {
            isDialogOpen = false;
            onCancel(dialog);
        });
        builder.setOnDismissListener(dialog -> {
            isDialogOpen = false;
            onCancel(dialog);
        });

        // Add functionality for the reset button
        TextView filterName = dialogView.findViewById(R.id.filter_name);
        filterName.setText(filterNameText);

        // Add the reset button functionality
        AppCompatButton resetButton = dialogView.findViewById(R.id.reset_all_filter);
        resetButton.setOnClickListener(v -> {
            selected = defaultText; // Reset selection
            itemsListView.clearChoices(); // Clear any ListView selections
            adapter.notifyDataSetChanged(); // Refresh ListView
        });

        builder.show();
        return true;
    }

    public void setItems(List<String> items, String defaultText, String filterName) {
        this.items = items;
        this.defaultText = defaultText;
        this.filterNameText = filterName;

        selected = defaultText;
        updateAdapter(defaultText);
    }

    public String getSelectedItem() {
        return selected;
    }

    public void restoreSelectedItem(String selected) {
        if (!selected.equals(defaultText)) {
            this.selected = selected;
            updateAdapter(selected);
        }
    }
}
