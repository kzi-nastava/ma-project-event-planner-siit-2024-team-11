package com.example.eventy.events.budget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.adapters.events.BudgetItemSolutionSelectionAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetItemSolutionSelectionDialog extends Dialog implements View.OnClickListener {

    private List<SolutionCard> solutionCardList = new ArrayList<>();
    private String categoryName;
    private BudgetItemSolutionSelectionAdapter adapter;
    private MaterialButton productsButton;
    private MaterialButton servicesButton;
    private LinearLayout stageOneLayout;
    private LinearLayout stageTwoLayout;
    private RecyclerView recycler;
    private NavController navController;


    public BudgetItemSolutionSelectionDialog(@NonNull Context context, String categoryName, NavController navController) {
        super(context);
        this.categoryName = categoryName;
        this.navController = navController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_budget_item_solution_selection);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        stageOneLayout = findViewById(R.id.dialog_budget_item_selection_first_stage);
        stageTwoLayout = findViewById(R.id.dialog_budget_item_selection_second_stage);
        productsButton = findViewById(R.id.dialog_budget_item_selection_products_button);
        servicesButton = findViewById(R.id.dialog_budget_item_selection_services_button);
        recycler = findViewById(R.id.dialog_budget_item_selection_recycler);

        productsButton.setOnClickListener(v -> {
            setStageTwo("Product");
        });

        servicesButton.setOnClickListener(v -> {
            setStageTwo("Service");
        });

        adapter = new BudgetItemSolutionSelectionAdapter(getContext(), solutionCardList, navController, this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchData(String selection) {
        ArrayList<String> categories = new ArrayList<>();
        categories.add(this.categoryName);

        Call<PagedResponse<SolutionCard>> call = ClientUtils.solutionService.getSolutions(null, selection, categories, null, null, null, null, null, null, true, 0, 999999, null);
        call.enqueue(new Callback<PagedResponse<SolutionCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<SolutionCard>> call, Response<PagedResponse<SolutionCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    solutionCardList.addAll(response.body().getContent());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<SolutionCard>> call, Throwable t) {
            }
        });
    }

    private void setStageTwo(String selection) {
        stageOneLayout.setVisibility(View.GONE);
        stageTwoLayout.setVisibility(View.VISIBLE);
        fetchData(selection);
    }

    @Override
    public void onClick(View v) {

    }
}
