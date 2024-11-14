package com.example.eventy.users.pup;

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
import com.example.eventy.databinding.FragmentPupOwnServicesBinding;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;

public class PUPOwnServicesFragment extends Fragment {
    private FragmentPupOwnServicesBinding binding;
    private SolutionsAdapter solutionsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPupOwnServicesBinding.inflate(inflater, container, false);
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

        Service service4 = new Service(
                "Event Decor",
                new Category("decor", "Neki description", Status.ACCEPTED),
                "Luxurious event decor for any theme.",
                1000.0, 12,
                new ArrayList<>(Arrays.asList("decor1.jpg", "decor2.jpg")),
                false, true, true, "Includes venue setup and takedown",
                45, 200, 10, 5,
                ReservationConfirmationType.MANUAL
        );

        Service service5 = new Service(
                "Makeup Artist",
                new Category("beauty", "Neki description", Status.ACCEPTED),
                "Professional makeup services for any occasion.",
                300.0, 0,
                new ArrayList<>(Arrays.asList("makeup1.jpg", "makeup2.jpg")),
                false, true, true, "Includes trial session and travel to venue",
                30, 90, 7, 3,
                ReservationConfirmationType.AUTOMATIC
        );

        Service service6 = new Service(
                "DJ Service",
                new Category("entertainment", "Neki description", Status.ACCEPTED),
                "High-energy DJ service for any event.",
                500.0, 8,
                new ArrayList<>(Arrays.asList("dj3.jpg", "dj4.jpg")),
                false, true, true, "Includes lighting and custom playlists",
                60, 180, 14, 7,
                ReservationConfirmationType.MANUAL
        );

        solutions.add(service1);
        solutions.add(service2);
        solutions.add(service3);
        solutions.add(service4);
        solutions.add(service5);
        solutions.add(service6);

        return solutions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
