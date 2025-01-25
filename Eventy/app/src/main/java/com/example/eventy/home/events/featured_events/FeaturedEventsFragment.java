package com.example.eventy.home.events.featured_events;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.adapters.events.FeaturedEventsAdapter;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentHomeFeaturedEventsBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturedEventsFragment extends Fragment {
    private FragmentHomeFeaturedEventsBinding binding;
    private FeaturedEventsAdapter featuredEventsAdapter;
    private ArrayList<EventCard> featuredEvents;
    private boolean isLoading = false;

    public FeaturedEventsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeFeaturedEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.featuredEvents = new ArrayList<>();
        setupRecyclerView();
        fetchFeaturedEvents();
    }

    private void setupRecyclerView() {
        featuredEventsAdapter = new FeaturedEventsAdapter(requireContext(), featuredEvents);
        binding.featuredEventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.featuredEventsRecycler.setAdapter(featuredEventsAdapter);
    }

    private void fetchFeaturedEvents() {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Call<EventCard[]> call = ClientUtils.eventService.getFeaturedEvents();
        call.enqueue(new Callback<EventCard[]>() {
            @Override
            public void onResponse(Call<EventCard[]> call, Response<EventCard[]> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    EventCard[] featuredEventsArray = response.body();
                    featuredEvents.clear();
                    featuredEvents.addAll(Arrays.asList(featuredEventsArray));
                    featuredEventsAdapter.notifyDataSetChanged();

                } else {
                    showErrorDialog("Error while loading featured events!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<EventCard[]> call, Throwable t) {
                showErrorDialog("Error while loading featured events!");
                showErrorDialog(t.getMessage());
                isLoading = false;
            }
        });
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}