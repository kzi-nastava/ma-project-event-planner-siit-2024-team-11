package com.example.eventy.events.budget;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.adapters.events.BudgetItemAdapter;
import com.example.eventy.databinding.FragmentBudgetBinding;
import com.example.eventy.events.model.Budget;
import com.example.eventy.events.model.BudgetItem;
import com.example.eventy.utils.ClientUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private BudgetItemAdapter adapter;
    private Long eventId;
    private Budget budget;
    private List<BudgetItem> items;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            this.eventId = getArguments().getLong("eventId");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Call<Budget> call = ClientUtils.budgetService.getBudget(eventId);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    budget = response.body();
                    items = budget.getCategoryItems();
                    loadAdapter();
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {

            }
        });

        binding.fragmentBudgetAddBudgetItemButton.setOnClickListener(v -> {
            BudgetItemCreationDialog dialog = new BudgetItemCreationDialog(requireContext(), eventId, (categoryId, allocatedFunds) -> {
                Call<BudgetItem> creationCall = ClientUtils.budgetService.createBudgetItem(eventId, categoryId, allocatedFunds);
                creationCall.enqueue(new Callback<BudgetItem>() {
                    @Override
                    public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                        if (response.isSuccessful()) {
                            items.add(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<BudgetItem> call, Throwable t) {

                    }
                });
            });
            dialog.show();
        });
    }

    private void loadAdapter() {
        NavController navController = Navigation.findNavController(getView());
        adapter = new BudgetItemAdapter(requireContext(), items, eventId, navController);
        binding.fragmentBudgetRecyclerView.setAdapter(adapter);
        binding.fragmentBudgetRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}