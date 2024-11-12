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

        // Creating 3 Service objects
        Service service1 = new Service();
        service1.setName("Wedding Photography");
        service1.setDescription("Professional wedding photography service.");
        service1.setPrice(1500.0);
        service1.setDiscount(10);
        service1.setImageUrls(new ArrayList<>(Arrays.asList("image1.jpg", "image2.jpg")));
        service1.setIsDeleted(false);
        service1.setIsVisible(true);
        service1.setIsAvailable(true);
        service1.setSpecifics("Full-day photography");
        service1.setMinReservationTime(30);
        service1.setMaxReservationTime(180);
        service1.setReservationDeadline(7);
        service1.setCancellationDeadline(3);
        service1.setReservationConfirmationType(ReservationConfirmationType.AUTOMATIC);

        Service service2 = new Service();
        service2.setName("DJ Service");
        service2.setDescription("High-quality DJ service for your events.");
        service2.setPrice(800.0);
        service2.setDiscount(5);
        service2.setImageUrls(new ArrayList<>(Arrays.asList("dj1.jpg", "dj2.jpg")));
        service2.setIsDeleted(false);
        service2.setIsVisible(true);
        service2.setIsAvailable(true);
        service2.setSpecifics("Includes sound and lighting equipment");
        service2.setMinReservationTime(60);
        service2.setMaxReservationTime(120);
        service2.setReservationDeadline(14);
        service2.setCancellationDeadline(7);
        service2.setReservationConfirmationType(ReservationConfirmationType.MANUAL);

        Service service3 = new Service();
        service3.setName("Event Catering");
        service3.setDescription("Delicious catering service for all types of events.");
        service3.setPrice(1200.0);
        service3.setDiscount(15);
        service3.setImageUrls(new ArrayList<>(Arrays.asList("catering1.jpg", "catering2.jpg")));
        service3.setIsDeleted(false);
        service3.setIsVisible(true);
        service3.setIsAvailable(true);
        service3.setSpecifics("Custom menu available");
        service3.setMinReservationTime(30);
        service3.setMaxReservationTime(150);
        service3.setReservationDeadline(10);
        service3.setCancellationDeadline(5);
        service3.setReservationConfirmationType(ReservationConfirmationType.AUTOMATIC);

        // Creating 2 Product objects
        Product product1 = new Product();
        product1.setName("Floral Centerpiece");
        product1.setDescription("Elegant floral centerpiece for your event.");
        product1.setPrice(50.0);
        product1.setDiscount(0);
        product1.setImageUrls(new ArrayList<>(Arrays.asList("floral1.jpg", "floral2.jpg")));
        product1.setIsDeleted(false);
        product1.setIsVisible(true);
        product1.setIsAvailable(true);

        Product product2 = new Product();
        product2.setName("Custom Gift Basket");
        product2.setDescription("Personalized gift basket for special occasions.");
        product2.setPrice(75.0);
        product2.setDiscount(5);
        product2.setImageUrls(new ArrayList<>(Arrays.asList("gift1.jpg", "gift2.jpg")));
        product2.setIsDeleted(false);
        product2.setIsVisible(true);
        product2.setIsAvailable(true);

        // Adding all objects to the featuredSolutions list
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
