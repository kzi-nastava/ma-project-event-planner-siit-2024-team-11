package com.example.eventy.home.events.featured_events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.adapters.events.FeaturedEventsAdapter;
import com.example.eventy.databinding.FragmentHomeFeaturedEventsBinding;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventType;
import com.example.eventy.model.utils.Location;

import java.util.ArrayList;
import java.util.Date;

public class FeaturedEventsFragment extends Fragment {
    private FragmentHomeFeaturedEventsBinding binding;
    private FeaturedEventsAdapter featuredEventsAdapter;

    public FeaturedEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<EventCard> featuredEventCards = getFeaturedEvents();

        featuredEventsAdapter = new FeaturedEventsAdapter(requireContext(), featuredEventCards);

        binding.featuredEventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.featuredEventsRecycler.setAdapter(featuredEventsAdapter);
    }

    @NonNull
    private static ArrayList<EventCard> getFeaturedEvents() {
        ArrayList<EventCard> featuredEventCards = new ArrayList<>();

        return featuredEventCards;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}