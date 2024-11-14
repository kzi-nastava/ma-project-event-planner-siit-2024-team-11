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
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
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

        ArrayList<Event> featuredEvents = getFeaturedEvents();

        featuredEventsAdapter = new FeaturedEventsAdapter(requireContext(), featuredEvents);

        binding.featuredEventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.featuredEventsRecycler.setAdapter(featuredEventsAdapter);
    }

    @NonNull
    private static ArrayList<Event> getFeaturedEvents() {
        ArrayList<Event> featuredEvents = new ArrayList<>();

        // event types
        EventType weddingType = new EventType("Wedding", "A celebration of marriage", true);
        EventType conferenceType = new EventType("Conference", "Professional gathering for knowledge exchange", true);
        EventType concertType = new EventType("Concert", "Live musical performance", true);
        EventType partyType = new EventType("Party", "A social gathering with music and dancing", true);
        EventType meetingType = new EventType("Meeting", "A formal gathering of individuals for a specific purpose", true);

        // locations
        Location weddingLocation = new Location("Grand Hall", "123 Wedding St, Cityville", 40.7128, -74.0060);
        Location conferenceLocation = new Location("Tech Center", "456 Innovation Blvd, Tech City", 37.7749, -122.4194);
        Location concertLocation = new Location("Stadium Arena", "789 Music Ave, Townsville", 34.0522, -118.2437);
        Location partyLocation = new Location("Luxe Lounge", "101 Party Ln, Fun Town", 51.5074, -0.1278);
        Location meetingLocation = new Location("Boardroom", "200 Corporate Rd, Business Park", 42.3601, -71.0589);

        // add to featured events
        featuredEvents.add(new Event("Mia & Jackson Wedding", "An elegant wedding celebration of joining 2 people into one soul, very beautiful very demure.", 200, PrivacyType.PUBLIC, new Date(), weddingLocation, weddingType));
        featuredEvents.add(new Event("Tech Conference", "A tech conference with industry leaders", 500, PrivacyType.PRIVATE, new Date(), conferenceLocation, conferenceType));
        featuredEvents.add(new Event("Summer Music Concert", "Enjoy the best live music performances", 1000, PrivacyType.PUBLIC, new Date(), concertLocation, concertType));
        featuredEvents.add(new Event("VIP PartyLounge", "An exclusive party for select guests", 100, PrivacyType.PRIVATE, new Date(), partyLocation, partyType));
        featuredEvents.add(new Event("Business Meeting", "Discussing the upcoming quarter's goals", 30, PrivacyType.PUBLIC, new Date(), meetingLocation, meetingType));

        return featuredEvents;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}