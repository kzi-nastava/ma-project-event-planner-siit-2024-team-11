package com.example.eventy.solutions.solutionCategories;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentCategoryHomeBinding;
import com.example.eventy.home.events.EventsFragment;

public class CategoryHomeFragment extends Fragment {

    private FragmentCategoryHomeBinding binding;
    private CategoryManagementFragment activeCategoryFragment;
    private CategoryRequestManagementFragment requestFragment;

    public CategoryHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryHomeBinding.inflate(inflater, container, false);

        loadInitialItems();

        binding.tabCategoryRequests.setOnClickListener(v -> {
            binding.tabActiveCategories.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabActiveCategories.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabCategoryRequests.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabCategoryRequests.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            loadRequests();
        });

        binding.tabActiveCategories.setOnClickListener(v -> {
            binding.tabCategoryRequests.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabCategoryRequests.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabActiveCategories.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabActiveCategories.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            loadActiveCategories();
        });

        return binding.getRoot();
    }

    private void loadInitialItems() {
        this.activeCategoryFragment = new CategoryManagementFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.category_frame_container, this.activeCategoryFragment)
                .commit();
    }

    private void loadActiveCategories() {
        this.activeCategoryFragment = new CategoryManagementFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.category_frame_container, this.activeCategoryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadRequests() {
        this.requestFragment = new CategoryRequestManagementFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.category_frame_container, this.requestFragment)
                .addToBackStack(null)
                .commit();
    }
}