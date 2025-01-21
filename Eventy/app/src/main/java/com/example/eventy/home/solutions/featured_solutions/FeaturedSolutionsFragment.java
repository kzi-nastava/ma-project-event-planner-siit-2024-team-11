package com.example.eventy.home.solutions.featured_solutions;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.solutions.FeaturedSolutionsAdapter;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentHomeFeaturedSolutionsBinding;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturedSolutionsFragment extends Fragment {
    private FragmentHomeFeaturedSolutionsBinding binding;
    private FeaturedSolutionsAdapter featuredSolutionsAdapter;
    private ArrayList<SolutionCard> featuredSolutions;
    private boolean isLoading = false;

    public FeaturedSolutionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedSolutionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.featuredSolutions = new ArrayList<>();
        setupRecyclerView();
        fetchFeaturedSolutions();
    }

    private void setupRecyclerView() {
        featuredSolutionsAdapter = new FeaturedSolutionsAdapter(requireContext(), featuredSolutions);
        binding.featuredSolutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.featuredSolutionsRecycler.setAdapter(featuredSolutionsAdapter);
    }

    private void fetchFeaturedSolutions() {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Call<SolutionCard[]> call = ClientUtils.solutionService.getFeaturedSolutions();
        call.enqueue(new Callback<SolutionCard[]>() {
            @Override
            public void onResponse(Call<SolutionCard[]> call, Response<SolutionCard[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    SolutionCard[] featuredSolutionsArray = response.body();
                    featuredSolutions.clear();
                    featuredSolutions.addAll(Arrays.asList(featuredSolutionsArray));
                    featuredSolutionsAdapter.notifyDataSetChanged();

                } else {
                    showErrorDialog("Error while loading featured solutions!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<SolutionCard[]> call, Throwable t) {
                showErrorDialog("Error while loading featured solutions!");
                showErrorDialog(t.getMessage());
                isLoading = false;
            }
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
