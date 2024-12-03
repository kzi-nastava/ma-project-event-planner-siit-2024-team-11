package com.example.eventy.home.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.solutions.SolutionsAdapter;
import com.example.eventy.databinding.FragmentHomeSolutionsBinding;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.event.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;

public class SolutionsFragment extends Fragment {
    private FragmentHomeSolutionsBinding binding;
    private SolutionsAdapter solutionsAdapter;

    public SolutionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeSolutionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Solution> solutions = getSolutions();

        solutionsAdapter = new SolutionsAdapter(requireContext(), solutions);

        binding.solutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.solutionsRecycler.setAdapter(solutionsAdapter);
    }

    @NonNull
    private static ArrayList<Solution> getSolutions() {
        ArrayList<Solution> solutions = new ArrayList<>();

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

        solutions.add(service1);
        solutions.add(product1);
        solutions.add(service2);
        solutions.add(service3);
        solutions.add(product2);

        Service service4 = new Service(
                "Event Decor",
                new Category("decor", "Neki description", Status.ACCEPTED),
                "Luxurious event decor for any theme.",
                1000.0, 12,
                new ArrayList<>(Arrays.asList("decor1.jpg", "decor2.jpg")),
                false, true, true, "Includes venue setup and takedown",
                45, 200, 10, 5,
                ReservationConfirmationType.MANUAL, eventTypes
        );

        Service service5 = new Service(
                "Makeup Artist",
                new Category("beauty", "Neki description", Status.ACCEPTED),
                "Professional makeup services for any occasion.",
                300.0, 0,
                new ArrayList<>(Arrays.asList("makeup1.jpg", "makeup2.jpg")),
                false, true, true, "Includes trial session and travel to venue",
                30, 90, 7, 3,
                ReservationConfirmationType.AUTOMATIC, eventTypes
        );

        Service service6 = new Service(
                "DJ Service",
                new Category("entertainment", "Neki description", Status.ACCEPTED),
                "High-energy DJ service for any event.",
                500.0, 8,
                new ArrayList<>(Arrays.asList("dj3.jpg", "dj4.jpg")),
                false, true, true, "Includes lighting and custom playlists",
                60, 180, 14, 7,
                ReservationConfirmationType.MANUAL, eventTypes
        );

        Product product3 = new Product(
                "Handmade Candle Set",
                new Category("candles", "Neki description", Status.ACCEPTED),
                "Set of 3 scented candles, perfect for gifting.",
                30.0, 5,
                new ArrayList<>(Arrays.asList("candle1.jpg", "candle2.jpg")),
                false, true, true, eventTypes
        );

        Product product4 = new Product(
                "Wedding Guestbook",
                new Category("stationery", "Neki description", Status.ACCEPTED),
                "Elegant guestbook for your special day.",
                40.0, 10,
                new ArrayList<>(Arrays.asList("guestbook1.jpg", "guestbook2.jpg")),
                false, true, true, eventTypes
        );

        solutions.add(service4);
        solutions.add(service5);
        solutions.add(service6);
        solutions.add(product3);
        solutions.add(product4);

        return solutions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
