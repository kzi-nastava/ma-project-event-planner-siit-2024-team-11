package com.example.eventy.home.events;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.R;
import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentHomeEventsBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventsFragment extends Fragment {
    private FragmentHomeEventsBinding binding;
    private EventsAdapter eventsAdapter;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 100;
    private String sort = "type";
    private ArrayList<EventCard> paginatedEvents;
    private boolean isLoading = false;

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

        this.paginatedEvents = new ArrayList<>();
        setupRecyclerView();
        setupPaginationControls();

        fetchEvents("", null, null, null, null, null, page, pageSize, sort);
    }

    private void setupRecyclerView() {
        eventsAdapter = new EventsAdapter(requireContext(), paginatedEvents);
        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsAdapter);
    }

    private void setupPaginationControls() {
        binding.btnPrevious.setOnClickListener(v -> {
            if (page > 0) {
                page--;
               fetchEvents("", null, null, null, null, null, page, pageSize, sort);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                fetchEvents("", null, null, null, null, null, page, pageSize, sort);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_options,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(2);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                page = 0; // Reset to the first page
                fetchEvents("", null, null, null, null, null, page, pageSize, sort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchEvents(String search, ArrayList<String> eventTypes, Integer maxParticipants, String location, LocalDateTime startDate, LocalDateTime endDate, int page, int pageSize, String sort) {
        if (isLoading) return;
        isLoading = true;

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<PagedResponse<EventCard>> call = ClientUtils.eventService.getEvents(
            search, eventTypes, maxParticipants, location, startDate, endDate, page, pageSize, sort
        );

        call.enqueue(new Callback<PagedResponse<EventCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<EventCard>> call, Response<PagedResponse<EventCard>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<EventCard> pagedResponse = response.body();

                    paginatedEvents.clear();
                    paginatedEvents.addAll(pagedResponse.getContent());
                    eventsAdapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();

                } else {
                    showErrorDialog("Error while loading events!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
                //binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PagedResponse<EventCard>> call, Throwable t) {
                showErrorDialog("Error while loading events!");
                showErrorDialog(t.getMessage());
                isLoading = false;
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updatePaginationControls() {
        binding.btnPrevious.setEnabled(page > 0);
        binding.btnNext.setEnabled(page < totalPages - 1);

        binding.tvPageInfo.setText(
                String.format("Page %d of %d", page + 1, totalPages)
        );
    }

    private void showErrorDialog(String message) {
        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorOkDialog.show();
    }
}
