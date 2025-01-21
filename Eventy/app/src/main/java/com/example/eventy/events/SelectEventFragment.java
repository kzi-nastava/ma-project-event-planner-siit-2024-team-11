package com.example.eventy.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.events.EventsSingleSelectionAdapter;
import com.example.eventy.databinding.FragmentServiceReservationSelectEventRecyclerBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.services.ReservationSelectEventFragment;

import java.util.ArrayList;

public class SelectEventFragment extends Fragment {
    private FragmentServiceReservationSelectEventRecyclerBinding binding;
    private EventsSingleSelectionAdapter eventsSingleSelectionAdapter;
    private ReservationSelectEventFragment reservationSelectEventFragment;

    public SelectEventFragment() {
        // Required empty public constructor
    }

    public SelectEventFragment(ReservationSelectEventFragment reservationSelectEventFragment) {
        this.reservationSelectEventFragment = reservationSelectEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceReservationSelectEventRecyclerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<EventCard> eventCards = getEvents();

        eventsSingleSelectionAdapter = new EventsSingleSelectionAdapter(requireContext(), eventCards, reservationSelectEventFragment);

        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsSingleSelectionAdapter);
    }

    @NonNull
    private static ArrayList<EventCard> getEvents() {
        ArrayList<EventCard> eventCards = new ArrayList<>();

        return eventCards;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

