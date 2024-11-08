package com.example.eventy.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.tabEvent.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.tab_active_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.tab_inactive_background));
        });

        binding.tabSolutions.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.tab_inactive_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.tab_active_background));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}