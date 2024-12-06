package com.example.eventy.solutions.solutionCategories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.adapters.solutions.SolutionCategoryAdapter;
import com.example.eventy.databinding.FragmentCategoryManagementBinding;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.solution.Category;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryManagementFragment extends Fragment {

    private FragmentCategoryManagementBinding binding;

    private SolutionCategoryAdapter adapter;

    public CategoryManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Category> categories = new ArrayList<>(Arrays.asList(
                new Category("Electronics", "Devices and gadgets", Status.ACCEPTED),
                new Category("Clothing", "Apparel and fashion", Status.ACCEPTED),
                new Category("Books", "Printed and digital books", Status.ACCEPTED),
                new Category("Home & Kitchen", "Furniture and appliances", Status.ACCEPTED),
                new Category("Sports", "Fitness and outdoor equipment", Status.ACCEPTED)
        ));

        adapter = new SolutionCategoryAdapter(requireContext(), categories);

        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.categoriesRecycler.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}