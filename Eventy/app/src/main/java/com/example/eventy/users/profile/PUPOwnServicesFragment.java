package com.example.eventy.users.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.adapters.solutions.SolutionsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentPupOwnServicesBinding;
import com.example.eventy.model.solution.Solution;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PUPOwnServicesFragment extends Fragment {
    private FragmentPupOwnServicesBinding binding;
    private SolutionsAdapter solutionsAdapter;
    private Long userId;
    private boolean isMyCards;
    private int page = 0;
    private int pageSize = 5;
    private boolean isLoading = false;
    private ArrayList<Solution> solutionCards;
    private boolean canGoFurther = true;

    public PUPOwnServicesFragment() {
    }

    public PUPOwnServicesFragment(Long userId, boolean isMyCards) {
        this.userId = userId;
        this.isMyCards = isMyCards;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPupOwnServicesBinding.inflate(inflater, container, false);

        solutionCards = new ArrayList<>();
        solutionsAdapter = new SolutionsAdapter(requireContext(), solutionCards);

        binding.solutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.solutionsRecycler.setAdapter(solutionsAdapter);

        loadCards("", page);

        binding.solutionsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == solutionCards.size() - 1 && canGoFurther) {
                    loadCards(binding.searchInput.getQuery().toString(), ++page);
                }
            }
        });

        binding.searchSolutionsButton.setOnClickListener(v -> {
            page = 0;
            solutionCards = new ArrayList<>();
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

            Call<PagedResponse<Solution>> call = ClientUtils.userService.getMySolutions(this.userId, search, page, pageSize);
            call.enqueue(new Callback<PagedResponse<Solution>>() {
                @Override
                public void onResponse(Call<PagedResponse<Solution>> call, Response<PagedResponse<Solution>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        solutionCards.addAll(response.body().getContent());
                        solutionsAdapter.notifyDataSetChanged();

                        if(response.body().getContent().isEmpty()) {
                            canGoFurther = false;
                        }
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading my solutions!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<Solution>> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading my solutions!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();

                    isLoading = false;
                }
            });
        }
        else {
            isLoading = true;

            Call<PagedResponse<Solution>> call = ClientUtils.userService.getMyFavoriteSolutions(userId, search, page, pageSize);
            call.enqueue(new Callback<PagedResponse<Solution>>() {
                @Override
                public void onResponse(Call<PagedResponse<Solution>> call, Response<PagedResponse<Solution>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        solutionCards.addAll(response.body().getContent());
                        solutionsAdapter.notifyDataSetChanged();

                        if(response.body().getContent().isEmpty()) {
                            canGoFurther = false;
                        }
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading favorite solutions!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<Solution>> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading favorite solutions!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();

                    isLoading = false;
                }
            });
        }
    }
}
