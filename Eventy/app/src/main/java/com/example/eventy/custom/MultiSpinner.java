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
import android.widget.Button;

import com.example.eventy.R;

import java.util.ArrayList;
import java.util.List;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {
    private List<String> items;
    private List<String> selected;
    private String defaultText;
    private String filterNameText;
    private ArrayAdapter<String> adapter;
    private ListView itemsListView;
    private boolean isDialogOpen = false;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        String selectedItem = adapter.getItem(which);
        if (isChecked) {
            if (!selected.contains(selectedItem)) {
                selected.add(selectedItem);
            }
        } else {
            selected.remove(selectedItem);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        updateAdapter(selected);
    }

    private void updateAdapter(List<String> values) {
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someSelected = false;
        if (values != null) {
            for (String selectedItem : values) {
                spinnerBuffer.append(selectedItem);
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }

        String spinnerText;
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2); // Remove trailing comma
        } else {
            spinnerText = defaultText;
        }

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{spinnerText});
        setAdapter(adapter);
    }

    @Override
    public boolean performClick() {
        if (isDialogOpen) {
            return true; // Block the click
        }
        isDialogOpen = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = View.inflate(getContext(), R.layout.custom_multi_spinner_dialog, null);
        builder.setView(dialogView);

        itemsListView = dialogView.findViewById(R.id.multi_items_list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, items);
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set initial selection state
        for (int i = 0; i < items.size(); i++) {
            itemsListView.setItemChecked(i, selected.contains(items.get(i)));
        }

        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                for (int i = 0; i < itemsListView.getCount(); i++) {
                    String item = adapter.getItem(i);
                    if (itemsListView.isItemChecked(i)) {
                        if (!selected.contains(item)) {
                            selected.add(item);
                        }
                    } else {
                        selected.remove(item);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s, cnt -> {
                    // Update checked states after filtering
                    for (int i = 0; i < itemsListView.getCount(); i++) {
                        String item = adapter.getItem(i);
                        itemsListView.setItemChecked(i, selected.contains(item));
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do something
            }
        });

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            for (int i = 0; i < itemsListView.getCount(); i++) {
                if (itemsListView.isItemChecked(i)) { // check if the item is selected
                    String value = adapter.getItem(i); // Get the string value from the adapter
                    if (!selected.contains(value)) {
                        selected.add(value);
                    }
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

        TextView filterName = dialogView.findViewById(R.id.filter_name);
        filterName.setText(filterNameText);

        Button resetAllFilterButton = dialogView.findViewById(R.id.reset_all_filter);
        resetAllFilterButton.setOnClickListener(v -> {
            selected.clear();
            for (int i = 0; i < items.size(); i++) {
                itemsListView.setItemChecked(i, false); // Uncheck all items
            }
        });

        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, String defaultText, String filterName) {
        this.items = items;
        this.defaultText = defaultText;
        this.filterNameText = filterName;

        selected = new ArrayList<>();
        updateAdapter(new ArrayList<>());
    }

    public List<String> getSelectedItems() {
        return selected;
    }

    public void restoreSelectedItem(ArrayList<String> selected) {
        this.selected = selected;
        updateAdapter(selected);
    }
}