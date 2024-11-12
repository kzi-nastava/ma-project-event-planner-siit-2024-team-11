package com.example.eventy.home.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.databinding.FragmentHomeEventsBinding;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.utils.Location;

import java.util.ArrayList;
import java.util.Date;

public class EventsFragment  extends Fragment {
    private FragmentHomeEventsBinding binding;
    private EventsAdapter eventsAdapter;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Event> events = getEvents();

        eventsAdapter = new EventsAdapter(requireContext(), events);

        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsAdapter);
    }

    @NonNull
    private static ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<>();

        // event types
        EventType weddingType = new EventType("Wedding", "An unforgettable celebration of love and commitment", true);
        EventType conferenceType = new EventType("Conference", "A professional event for exchanging ideas and insights", true);
        EventType concertType = new EventType("Concert", "A thrilling live performance of music", true);
        EventType partyType = new EventType("Party", "An entertaining social gathering filled with fun", true);
        EventType meetingType = new EventType("Meeting", "A strategic assembly to align goals and plans", true);
        EventType workshopType = new EventType("Workshop", "An interactive session focused on skill development", true);
        EventType seminarType = new EventType("Seminar", "An educational event with expert speakers", true);
        EventType festivalType = new EventType("Festival", "A lively celebration with cultural activities", true);
        EventType sportsEventType = new EventType("Sports Event", "A competition showcasing athletic talent", true);
        EventType charityEventType = new EventType("Charity Event", "A fundraising event for a noble cause", true);

        // locations
        Location weddingLocation = new Location("Grand Hall", "123 Wedding St, Cityville", 40.7128, -74.0060);
        Location conferenceLocation = new Location("Tech Center", "456 Innovation Blvd, Tech City", 37.7749, -122.4194);
        Location concertLocation = new Location("Stadium Arena", "789 Music Ave, Townsville", 34.0522, -118.2437);
        Location partyLocation = new Location("Luxe Lounge", "101 Party Ln, Fun Town", 51.5074, -0.1278);
        Location meetingLocation = new Location("Boardroom", "200 Corporate Rd, Business Park", 42.3601, -71.0589);
        Location workshopLocation = new Location("SkillSpace", "321 Learning Dr, Workshop City", 45.4215, -75.6972);
        Location seminarLocation = new Location("Knowledge Auditorium", "555 Wisdom Blvd, Studyville", 34.0522, -118.2437);
        Location festivalLocation = new Location("Festival Grounds", "789 Culture St, Festive Town", 55.7558, 37.6173);
        Location sportsEventLocation = new Location("Olympic Stadium", "101 Sport Ave, Game City", 48.8566, 2.3522);
        Location charityEventLocation = new Location("Community Center", "500 Giving Ln, Charity Village", 40.7306, -73.9352);

        // add to events
        events.add(new Event("Mark & Jana's Wedding", "An unforgettable celebration of love and commitment", 200, PrivacyType.PUBLIC, new Date(), weddingLocation, weddingType));
        events.add(new Event("Tech Conference", "A tech conference with industry leaders", 500, PrivacyType.PRIVATE, new Date(), conferenceLocation, conferenceType));
        events.add(new Event("Summer Music Concert", "Enjoy the best live music performances", 1000, PrivacyType.PUBLIC, new Date(), concertLocation, concertType));
        events.add(new Event("VIP PartyLounge", "An exclusive party for select guests", 100, PrivacyType.PRIVATE, new Date(), partyLocation, partyType));
        events.add(new Event("Business Meeting", "Discussing the upcoming quarter's goals", 30, PrivacyType.PUBLIC, new Date(), meetingLocation, meetingType));
        events.add(new Event("Art of Coding Workshop", "A hands-on coding workshop for developers", 50, PrivacyType.PUBLIC, new Date(), workshopLocation, workshopType));
        events.add(new Event("Science & Health Seminar", "A seminar exploring the latest in science and health", 300, PrivacyType.PRIVATE, new Date(), seminarLocation, seminarType));
        events.add(new Event("Cultural Fest 2024", "A celebration of cultures with music, food, and art", 1500, PrivacyType.PUBLIC, new Date(), festivalLocation, festivalType));
        events.add(new Event("Championship Finals", "The most exciting sports event of the season", 5000, PrivacyType.PUBLIC, new Date(), sportsEventLocation, sportsEventType));
        events.add(new Event("Hope Foundation Gala", "A charity gala supporting local communities", 200, PrivacyType.PRIVATE, new Date(), charityEventLocation, charityEventType));

        return events;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
