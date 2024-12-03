package com.example.eventy.home.solutions.featured_solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.solutions.FeaturedSolutionsAdapter;
import com.example.eventy.databinding.FragmentHomeFeaturedSolutionsBinding;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;

public class FeaturedSolutionsFragment extends Fragment {
    private FragmentHomeFeaturedSolutionsBinding binding;
    private FeaturedSolutionsAdapter featuredSolutionsAdapter;

    public FeaturedSolutionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedSolutionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Solution> featuredSolutions = getFeaturedSolutions();

        featuredSolutionsAdapter = new FeaturedSolutionsAdapter(requireContext(), featuredSolutions);

        binding.featuredSolutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.featuredSolutionsRecycler.setAdapter(featuredSolutionsAdapter);
    }

    @NonNull
    private static ArrayList<Solution> getFeaturedSolutions() {
        ArrayList<Solution> featuredSolutions = new ArrayList<>();

        ArrayList<EventType> eventTypes = new ArrayList<>();
        EventType eventType1 = new EventType();
        eventType1.setName("Wedding");
        EventType eventType2 = new EventType();
        eventType2.setName("Sport");
        EventType eventType3 = new EventType();
        eventType3.setName("Conference");
        EventType eventType4 = new EventType();
        eventType4.setName("Party");

        eventTypes.add(eventType1);
        eventTypes.add(eventType2);
        eventTypes.add(eventType3);
        eventTypes.add(eventType4);

        Service service1 = new Service(
            "Photography",
            new Category("photography", "Neki description", Status.ACCEPTED),
            "Professional wedding photography service.",
            1500.0, 10,
            new ArrayList<>(Arrays.asList("image1.jpg", "image2.jpg")),
            false, true, true, "Full-day photography",
            30, 180, 7, 3,
            ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Service service2 = new Service(
            "Bon Jovi",
            new Category("music", "Neki description", Status.ACCEPTED),
            "Best band ever!", 800.0, 5,
            new ArrayList<>(Arrays.asList("dj1.jpg", "dj2.jpg")),
            false, true, true, "Includes sound and lighting equipment",
            60, 120, 14, 7,
            ReservationConfirmationType.MANUAL, eventTypes
        );

        Service service3 = new Service(
            "Event Catering - The best",
            new Category("catering", "Neki description", Status.ACCEPTED),
            "Delicious catering service for all types of events.",
            1200.0, 15,
            new ArrayList<>(Arrays.asList("catering1.jpg", "catering2.jpg")),
            false, true, true, "Custom menu available",
            30, 150, 10, 5,
            ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Product product1 = new Product(
            "Sweet 16 - cake",
            new Category("cake", "Neki description", Status.ACCEPTED),
            "Elegant floral centerpiece for your event.",
            50.0, 0,
            new ArrayList<>(Arrays.asList("floral1.jpg", "floral2.jpg")),
            false, true, true, eventTypes
        );

        Product product2 = new Product(
            "Custom Gift Candy Basket",
            new Category("gifts", "Neki description", Status.ACCEPTED),
            "Personalized gift basket for special occasions.", 75.0, 5,
            new ArrayList<>(Arrays.asList("gift1.jpg", "gift2.jpg")),
            false, true, true, eventTypes
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
