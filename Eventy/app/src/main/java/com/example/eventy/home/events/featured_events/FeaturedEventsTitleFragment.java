package com.example.eventy.home.events.featured_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventy.databinding.FragmentHomeFeaturedEventsTitleBinding;

public class FeaturedEventsTitleFragment extends Fragment {
    private FragmentHomeFeaturedEventsTitleBinding binding;

    public FeaturedEventsTitleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedEventsTitleBinding.inflate(inflater, container, false);
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
