package com.example.eventy.solutions.solutionCategories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.eventy.R;
import com.example.eventy.databinding.FragmentCategoryInputBinding;

public class CategoryInputFragment extends Fragment {

    private EditText categoryNameInput;
    private EditText categoryDescriptionInput;
    private TextView label;
    private Button saveButton;
    private FragmentCategoryInputBinding binding;

    public CategoryInputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        label = binding.newCategoryLabel;
        categoryNameInput = binding.categoryNameInput;
        categoryDescriptionInput = binding.categoryDescriptionInput;
        saveButton = binding.saveButton;

        if (getArguments() != null) {
            String categoryName = getArguments().getString("category_name");
            String categoryDescription = getArguments().getString("category_description");

            if (categoryName != null) {
                categoryNameInput.setText(categoryName);
            }

            if (categoryDescription != null) {
                categoryDescriptionInput.setText(categoryDescription);
            }

            if (categoryName != null && categoryDescription != null) {
                label.setText(getString(R.string.edit_category_label));
                saveButton.setText(getString(R.string.edit_category_button));
            } else {
                label.setText(getString(R.string.add_category_label));
                saveButton.setText(getString(R.string.add_category_button));
            }
        } else {
            label.setText(getString(R.string.add_category_label));
            saveButton.setText(getString(R.string.add_category_button));
        }

        saveButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                Toast.makeText(getActivity(), "EDIT CATEGORY BUTTON PRESSED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "ADD CATEGORY BUTTON PRESSED", Toast.LENGTH_SHORT).show();
            }
            // TODO: Implement save functionality
        });
    }
}
