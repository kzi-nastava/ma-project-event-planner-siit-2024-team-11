package com.example.eventy.users.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentOrganizerEventsBinding;
import com.example.eventy.events.model.EventCard;

import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerEventsFragment extends Fragment {
    private FragmentOrganizerEventsBinding binding;
    private EventsAdapter eventsAdapter;
    private Long userId;
    private boolean isMyCards;
    private int page = 0;
    private int pageSize = 5;
    private boolean isLoading = false;
    private ArrayList<EventCard> eventCards;
    private boolean canGoFurther = true;
    public OrganizerEventsFragment(Long userId, boolean isMyCards) {
        this.userId = userId;
        this.isMyCards = isMyCards;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrganizerEventsBinding.inflate(inflater, container, false);

        eventCards = new ArrayList<>();
        eventsAdapter = new EventsAdapter(requireContext(), eventCards);

        binding.eventsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecycler.setAdapter(eventsAdapter);

        loadCards("", page);

        binding.eventsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == eventCards.size() - 1 && canGoFurther) {
                    loadCards(binding.searchInput.getQuery().toString(), ++page);
                }
            }
        });

        binding.searchEventsButton.setOnClickListener(v -> {
            page = 0;
            eventCards = new ArrayList<>();
            loadCards(binding.searchInput.getQuery().toString(), page);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCards(String search, int page) {
        if (this.isMyCards) {
            isLoading = true;

            Call<PagedResponse<EventCard>> call = ClientUtils.userService.getMyEvents(this.userId, search, page, pageSize);
            call.enqueue(new Callback<PagedResponse<EventCard>>() {
                @Override
                public void onResponse(Call<PagedResponse<EventCard>> call, Response<PagedResponse<EventCard>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        eventCards.addAll(response.body().getContent());
                        eventsAdapter.notifyDataSetChanged();

                        if(response.body().getContent().isEmpty()) {
                            canGoFurther = false;
                        }
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading my events!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<EventCard>> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading my events!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();

                    isLoading = false;
                }
            });
        }
        else {
            isLoading = true;

            Call<PagedResponse<EventCard>> call = ClientUtils.userService.getMyFavoriteEvents(userId, search, page, pageSize);
            call.enqueue(new Callback<PagedResponse<EventCard>>() {
                @Override
                public void onResponse(Call<PagedResponse<EventCard>> call, Response<PagedResponse<EventCard>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        eventCards.addAll(response.body().getContent());
                        eventsAdapter.notifyDataSetChanged();

                        if(response.body().getContent().isEmpty()) {
                            canGoFurther = false;
                        }
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading favorite events!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<EventCard>> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading favorite events!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();

                    isLoading = false;
                }
            });
        }
    }
}