package com.example.eventy.home.solutions.featured_solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventy.databinding.FragmentHomeFeaturedEventsTitleBinding;
import com.example.eventy.databinding.FragmentHomeFeaturedSolutionsTitleBinding;

public class FeaturedSolutionsTitleFragment extends Fragment {
    private FragmentHomeFeaturedSolutionsTitleBinding binding;

    public FeaturedSolutionsTitleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedSolutionsTitleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // custom code

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
