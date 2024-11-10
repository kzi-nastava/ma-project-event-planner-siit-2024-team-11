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
import com.example.eventy.home.events.featured_events.FeaturedEventsFragment;
import com.example.eventy.home.events.featured_events.FeaturedEventsTitleFragment;
import com.example.eventy.register.RegisterOrganiserFragment;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // load Featured Events/Solutions title container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, new FeaturedEventsTitleFragment())
                .commit();

        // Load Top 5 Events/Solutions container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, new FeaturedEventsFragment())
                .commit();

        binding.tabEvent.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            Fragment featuredEventsTitleFragment = new FeaturedEventsTitleFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_title, featuredEventsTitleFragment)
                    .addToBackStack(null)
                    .commit();

            Fragment fragmentFeaturedEvents = new FeaturedEventsFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_view, fragmentFeaturedEvents)
                    .addToBackStack(null)
                    .commit();
        });

        binding.tabSolutions.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            Fragment fragment = new RegisterOrganiserFragment();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_view, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }


}