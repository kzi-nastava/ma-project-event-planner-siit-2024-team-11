package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentAddEventTypeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AddEventTypeFragment extends Fragment {

    private FragmentAddEventTypeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddEventTypeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_types);
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}