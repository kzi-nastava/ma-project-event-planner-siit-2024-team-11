package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventTypeDetailsBinding;

import java.util.ArrayList;
import java.util.List;

public class EventTypeDetailsFragment extends Fragment {
    private FragmentEventTypeDetailsBinding binding;

    private RecyclerView recyclerView;
    private EventTypeCardAdapter adapter;
    private List<String> categoriesList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventTypeDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_event_types);
        });

        binding.editButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_edit_event_type);
        });

        recyclerView = binding.categoriesContainer;
        categoriesList = new ArrayList<>();
        adapter = new EventTypeCardAdapter(categoriesList);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        loadCategories();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCategories() {
        // Simulate fetching data (you can replace this with an API call or database query)
        recyclerView.postDelayed(() -> {
            for (int i = 0; i < 10; i++) {
                categoriesList.add("Name " + (i + 1));
            }
            adapter.notifyDataSetChanged();
        }, 50); // Simulate a network delay
    }
}