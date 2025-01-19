package com.example.eventy.custom;

import android.app.AlertDialog;
import android.content.Context;
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

public class SingleSpinner extends AppCompatSpinner {
    private List<String> items;
    private int selectedIndex = -1; // Default: no selection
    private String defaultText;
    private SingleSpinnerListener listener;
    private String filterNameText;

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
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflate custom layout
        View dialogView = View.inflate(getContext(), R.layout.custom_single_spinner_dialog, null);
        builder.setView(dialogView);

        // Initialize ListView and adapter
        ListView itemsListView = dialogView.findViewById(R.id.single_items_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_single_choice, items);
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Pre-select the current selection if any
        if (selectedIndex >= 0) {
            itemsListView.setItemChecked(selectedIndex, true);
        }

        // Add search functionality
        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add functionality for the reset button
        TextView filterName = dialogView.findViewById(R.id.filter_name);
        filterName.setText(filterNameText);

        // Add the reset button functionality
        AppCompatButton resetButton = dialogView.findViewById(R.id.reset_all_filter);
        resetButton.setOnClickListener(v -> {
            selectedIndex = -1; // Reset selection
            updateSpinnerText(); // Update spinner with default text
            itemsListView.clearChoices(); // Clear any ListView selections
            adapter.notifyDataSetChanged(); // Refresh ListView
        });

        // Handle OK button click
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            int checkedItemPosition = itemsListView.getCheckedItemPosition();
            if (checkedItemPosition != -1) {
                selectedIndex = checkedItemPosition;
                updateSpinnerText();
                if (listener != null) {
                    listener.onItemSelected(items.get(selectedIndex));
                }
            }
        });

        builder.show();
        return true;
    }

    private void updateSpinnerText() {
        String spinnerText = (selectedIndex >= 0) ? items.get(selectedIndex) : defaultText;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{spinnerText});
        setAdapter(adapter);
    }

    public void setItems(List<String> items, String defaultText,
                         SingleSpinnerListener listener, String filterName) {
        this.items = items;
        this.defaultText = defaultText;
        this.listener = listener;
        this.filterNameText = filterName;

        updateSpinnerText();
    }

    public List<String> getItems() {
        return this.items;
    }

    public interface SingleSpinnerListener {
        void onItemSelected(String selectedItem);
    }
}
