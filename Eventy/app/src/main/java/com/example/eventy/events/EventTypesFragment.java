package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventTypesBinding;

import java.util.ArrayList;
import java.util.List;

public class EventTypesFragment extends Fragment {
    private FragmentEventTypesBinding binding;

    private RecyclerView recyclerView;
    private EventTypeCardAdapter adapter;
    private List<String> namesList;
    private boolean isLoading = false;
    private int page = 1;
    private int pageSize = 10;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventTypesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.addEventTypeButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            navController.navigate(R.id.nav_add_event_type);
        });

        recyclerView = binding.eventTypesCardsContainer;
        namesList = new ArrayList<>();
        adapter = new EventTypeCardAdapter(namesList);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        loadNames(page);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == namesList.size() - 1) {
                    loadNames(++page);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadNames(int page) {
        isLoading = true;

        // Simulate fetching data (you can replace this with an API call or database query)
        recyclerView.postDelayed(() -> {
            int start = (page - 1) * pageSize;
            for (int i = start; i < start + pageSize; i++) {
                namesList.add("Name " + (i + 1));
            }
            adapter.notifyDataSetChanged();
            isLoading = false;
        }, 1000); // Simulate a network delay
    }
}