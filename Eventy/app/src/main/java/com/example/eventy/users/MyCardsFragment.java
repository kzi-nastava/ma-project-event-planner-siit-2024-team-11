package com.example.eventy.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.adapters.events.FeaturedEventsAdapter;
import com.example.eventy.adapters.solutions.FeaturedSolutionsAdapter;
import com.example.eventy.databinding.FragmentHomeFeaturedEventsBinding;
import com.example.eventy.databinding.FragmentMyCardsBinding;
import com.example.eventy.model.enums.PrivacyType;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.Event;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;
import com.example.eventy.model.utils.Location;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MyCardsFragment extends Fragment {
    private FragmentMyCardsBinding binding;
    private FeaturedEventsAdapter featuredEventsAdapter;
    private FeaturedSolutionsAdapter featuredSolutionsAdapter;
    private User user;

    public MyCardsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(this.user.getAccountType() == UserType.ORGANIZER) {
            ArrayList<Event> featuredEvents = getFeaturedEvents();

            featuredEventsAdapter = new FeaturedEventsAdapter(requireContext(), featuredEvents);

            binding.featuredEventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.featuredEventsRecycler.setAdapter(featuredEventsAdapter);
        }
        else {
            ArrayList<Solution> featuredSolutions = getFeaturedSolutions();

            featuredSolutionsAdapter = new FeaturedSolutionsAdapter(requireContext(), featuredSolutions);

            binding.featuredSolutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.featuredSolutionsRecycler.setAdapter(featuredSolutionsAdapter);
        }
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
        featuredEvents.add(new Event("M & J's Wedding", "An elegant wedding celebration of joining 2 people into one soul, very beautiful very demure.", 200, PrivacyType.PUBLIC, new Date(), weddingLocation, weddingType));
        featuredEvents.add(new Event("Tech Conference", "A tech conference with industry leaders", 500, PrivacyType.PRIVATE, new Date(), conferenceLocation, conferenceType));
        featuredEvents.add(new Event("Summer Music Concert", "Enjoy the best live music performances", 1000, PrivacyType.PUBLIC, new Date(), concertLocation, concertType));
        featuredEvents.add(new Event("VIP PartyLounge", "An exclusive party for select guests", 100, PrivacyType.PRIVATE, new Date(), partyLocation, partyType));
        featuredEvents.add(new Event("Business Meeting", "Discussing the upcoming quarter's goals", 30, PrivacyType.PUBLIC, new Date(), meetingLocation, meetingType));

        return featuredEvents;
    }

    @NonNull
    private static ArrayList<Solution> getFeaturedSolutions() {
        ArrayList<Solution> featuredSolutions = new ArrayList<>();

        Service service1 = new Service(
                "Photography",
                new Category("photography", "Neki description", Status.ACCEPTED),
                "Professional wedding photography service.",
                1500.0, 10,
                new ArrayList<>(Arrays.asList("image1.jpg", "image2.jpg")),
                false, true, true, "Full-day photography",
                30, 180, 7, 3,
                ReservationConfirmationType.AUTOMATIC
        );

        Service service2 = new Service(
                "Bon Jovi",
                new Category("music", "Neki description", Status.ACCEPTED),
                "Best band ever!", 800.0, 5,
                new ArrayList<>(Arrays.asList("dj1.jpg", "dj2.jpg")),
                false, true, true, "Includes sound and lighting equipment",
                60, 120, 14, 7,
                ReservationConfirmationType.MANUAL
        );

        Service service3 = new Service(
                "Event Catering - The best",
                new Category("catering", "Neki description", Status.ACCEPTED),
                "Delicious catering service for all types of events.",
                1200.0, 15,
                new ArrayList<>(Arrays.asList("catering1.jpg", "catering2.jpg")),
                false, true, true, "Custom menu available",
                30, 150, 10, 5,
                ReservationConfirmationType.AUTOMATIC
        );

        Product product1 = new Product(
                "Sweet 16 - cake",
                new Category("cake", "Neki description", Status.ACCEPTED),
                "Elegant floral centerpiece for your event.",
                50.0, 0,
                new ArrayList<>(Arrays.asList("floral1.jpg", "floral2.jpg")),
                false, true, true
        );

        Product product2 = new Product(
                "Custom Gift Candy Basket",
                new Category("gifts", "Neki description", Status.ACCEPTED),
                "Personalized gift basket for special occasions.", 75.0, 5,
                new ArrayList<>(Arrays.asList("gift1.jpg", "gift2.jpg")),
                false, true, true
        );

        featuredSolutions.add(service1);
        featuredSolutions.add(product1);
        featuredSolutions.add(service2);
        featuredSolutions.add(service3);
        featuredSolutions.add(product2);

        return featuredSolutions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}