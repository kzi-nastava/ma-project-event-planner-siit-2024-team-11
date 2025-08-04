package com.example.eventy.events.eventtypes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventTypeDetailsBinding;
import com.example.eventy.events.model.EventTypeWithActivity;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeDetailsFragment extends Fragment {
    private FragmentEventTypeDetailsBinding binding;

    private RecyclerView recyclerView;
    private CategoryCardAdapter adapter;
    private List<String> categoriesList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventTypeDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Long typeId = getArguments().getLong("EventTypeID");

        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_types);
        });

        binding.editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("EventTypeID", typeId);

            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_edit_event_type, args);
        });

        if (typeId == 0) {
            binding.toggleActivityButton.setVisibility(View.GONE);
            binding.editButton.setVisibility(View.GONE);
        }

        recyclerView = binding.categoriesContainer;
        categoriesList = new ArrayList<>();
        adapter = new CategoryCardAdapter(categoriesList);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        Call<EventTypeWithActivity> call = ClientUtils.eventTypeService.get(typeId);
        call.enqueue(new Callback<EventTypeWithActivity>() {
            @Override
            public void onResponse(Call<EventTypeWithActivity> call, Response<EventTypeWithActivity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // prefill with data, what about the multiple select
                    binding.eventTypeName.setText(response.body().getName());
                    binding.eventTypeDescription.setText(response.body().getDescription());
                    setToggleActivityButton(!response.body().getIsActive());
                    // category adapter setting
                    categoriesList.addAll(response.body().getRecommendedSolutionCategories().stream().map(CategoryWithID::getName).collect(Collectors.toList()));
                    adapter.notifyDataSetChanged();
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

        binding.toggleActivityButton.setOnClickListener(v -> {
            Call<EventTypeWithActivity> callToggleActivity = ClientUtils.eventTypeService.toggleActivate(typeId);
            callToggleActivity.enqueue(new Callback<EventTypeWithActivity>() {
                @Override
                public void onResponse(Call<EventTypeWithActivity> call, Response<EventTypeWithActivity> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setToggleActivityButton(!response.body().getIsActive());
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Deactivation unsuccessful", "You can't deactivate an event type that is in use!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<EventTypeWithActivity> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Deactivation unsuccessful", "You can't deactivate an event type that is in use!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setToggleActivityButton(boolean isActivate) {
        if(isActivate) {
            binding.toggleActivityButton.setText("Activate");
            binding.toggleActivityButton.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_add));
            binding.toggleActivityButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_button, null)));
            return;
        }

        binding.toggleActivityButton.setText("Deactivate");
        binding.toggleActivityButton.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_delete));
        binding.toggleActivityButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_button, null)));
    }
}