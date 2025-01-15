package com.example.eventy.events.eventtypes;

import static android.content.Intent.getIntent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEditEventTypeBinding;
import com.example.eventy.events.model.CreatedEventType;
import com.example.eventy.events.model.EventType;
import com.example.eventy.events.model.EventTypeWithActivity;
import com.example.eventy.events.model.UpdateEventType;
import com.example.eventy.model.solution.CategoryWithID;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventTypeFragment extends Fragment {
    private FragmentEditEventTypeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditEventTypeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Long typeId = getArguments().getLong("EventTypeID");

        binding.backButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("EventTypeID", typeId);

            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_type_details);
        });

        Call<EventTypeWithActivity> call = ClientUtils.eventTypeService.get(typeId);
        call.enqueue(new Callback<EventTypeWithActivity>() {
            @Override
            public void onResponse(Call<EventTypeWithActivity> call, Response<EventTypeWithActivity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // prefill with data, what about the multiple select
                    binding.nameInput.setText(response.body().getName());
                    binding.descriptionInput.setText(response.body().getDescription());

                    TextInputEditText multiSelectEditText = binding.selectCategoriesInput;
                    String[] options = {"Option 1", "Option 2", "Option 3", "Option 4"};
                    boolean[] selectedOptions = new boolean[options.length];

                    List<CategoryWithID> recommendedSolutionCategories = response.body().getRecommendedSolutionCategories();
                    for(int i = 0;i < options.length;i++) {
                        for(int j = 0;j < recommendedSolutionCategories.size();j++) {
                            if(options[i].equals(recommendedSolutionCategories.get(j).getName())) {
                                selectedOptions[i] = true;
                                break;
                            }
                        }
                    }

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
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<EventTypeWithActivity> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        addValidation(binding.nameInputLayout, binding.nameInput, this::validateRequired);
        addValidation(binding.descriptionInputLayout, binding.descriptionInput, this::validateRequired);

        binding.editTypeButton.setOnClickListener(v -> {
            binding.nameInput.setText(binding.nameInput.getText());
            binding.descriptionInput.setText(binding.descriptionInput.getText());

            if(binding.nameInputLayout.getError() == null && binding.descriptionInputLayout.getError() == null &&
                    binding.selectCategoriesInputLayout.getError() == null) {
                Call<EventType> callEdit = ClientUtils.eventTypeService.update(new UpdateEventType(
                        typeId,
                        binding.nameInput.getText().toString(),
                        binding.descriptionInput.getText().toString(),
                        Arrays.stream(binding.selectCategoriesInput.getText().toString().trim().split(","))
                                .map(String::trim) // Remove any extra spaces around the numbers
                                .filter(s -> !s.isEmpty())
                                .map(Long::parseLong) // Convert to Long
                                .collect(Collectors.toList())
                ));
                callEdit.enqueue(new Callback<EventType>() {
                    @Override
                    public void onResponse(Call<EventType> call, Response<EventType> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            NavController navController = Navigation.findNavController(v);

                            navController.popBackStack();

                            navController.navigate(R.id.nav_event_types);
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Invalid input data!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EventType> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Invalid input data!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });
            } else {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Invalid input data!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

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