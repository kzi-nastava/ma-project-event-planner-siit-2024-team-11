package com.example.eventy.events.eventtypes;

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
import com.example.eventy.common.PagedResponse;
import com.example.eventy.databinding.FragmentEventTypesBinding;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.events.model.EventTypeWithActivity;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypesFragment extends Fragment {
    private FragmentEventTypesBinding binding;

    private RecyclerView recyclerView;
    private EventTypeCardAdapter adapter;
    private List<EventTypeCard> namesList;
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

        loadNames("", page);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == namesList.size() - 1) {
                    loadNames(binding.searchInput.getText().toString(), ++page);
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

    private void loadNames(String search, int page) {
        isLoading = true;

        Call<PagedResponse<EventTypeCard>> call = ClientUtils.eventTypeService.getEventTypes(search, page, pageSize);
        call.enqueue(new Callback<PagedResponse<EventTypeCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<EventTypeCard>> call, Response<PagedResponse<EventTypeCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    namesList.addAll(response.body().getContent());
                    adapter.notifyDataSetChanged();
                } else {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error while loading")
                            .setMessage("Error while loading event types!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.icon_error)
                            .show();
                }

                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<EventTypeCard>> call, Throwable t) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error while loading")
                        .setMessage("Error while loading event types!")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setIcon(R.drawable.icon_error)
                        .show();

                isLoading = false;
            }
        });
    }
}