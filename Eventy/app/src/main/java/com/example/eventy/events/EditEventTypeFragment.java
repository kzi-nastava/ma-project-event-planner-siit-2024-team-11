package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentAddEventTypeBinding;
import com.example.eventy.databinding.FragmentEditEventTypeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class EditEventTypeFragment extends Fragment {
    private FragmentEditEventTypeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditEventTypeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_type_details);
        });

        TextInputEditText multiSelectEditText = binding.selectCategoriesInput;
        String[] options = {"Option 1", "Option 2", "Option 3", "Option 4"};
        boolean[] selectedOptions = new boolean[options.length];

        multiSelectEditText.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select Recommended Categories")
                    .setMultiChoiceItems(options, selectedOptions, (dialog, which, isChecked) -> {
                        selectedOptions[which] = isChecked; // Update selected options
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Collect selected options
                        StringBuilder selected = new StringBuilder();
                        for (int i = 0; i < options.length; i++) {
                            if (selectedOptions[i]) {
                                if (selected.length() > 0) {
                                    selected.append(", ");
                                }
                                selected.append(options[i]);
                            }
                        }
                        // Display selected options
                        multiSelectEditText.setText(selected.toString());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);

        binding.addTypeButton.setOnClickListener(v -> {
            binding.nameInput.setText(binding.nameInput.getText());
            binding.descriptionInput.setText(binding.descriptionInput.getText());

            if(binding.nameInputLayout.getError() == null && binding.descriptionInputLayout.getError() == null &&
                    binding.selectCategoriesInputLayout.getError() == null) {
                // do add
                NavController navController = Navigation.findNavController(v);

                navController.popBackStack();

                navController.navigate(R.id.nav_event_types);
            } else {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Invalid input")
                        .setMessage("Name and description are required are required!")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setIcon(R.drawable.icon_error)
                        .show();
            }
        });

        // prefill with data, what about the multiple select
        binding.nameInput.setText("Wedding");
        binding.descriptionInput.setText("This amazing event type is happening when two people are getting married! Celebrate their new life!");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        // Real-time field validation
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate input as the user types
                action.accept(s.toString(), textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else {
            textInputLayout.setError(null);
        }
    }
}