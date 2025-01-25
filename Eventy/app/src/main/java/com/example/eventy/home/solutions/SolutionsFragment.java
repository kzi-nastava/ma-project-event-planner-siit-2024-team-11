package com.example.eventy.home.solutions;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.eventy.adapters.solutions.SolutionsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentHomeSolutionsBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.events.model.EventFilters;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.events.model.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Product;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.solutions.model.SolutionsFilter;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolutionsFragment extends Fragment {
    private FragmentHomeSolutionsBinding binding;
    private SolutionsAdapter solutionsAdapter;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 99;
    private String sort = "id,asc";
    private String search = "";
    private SolutionsFilter solutionsFilter;
    private ArrayList<SolutionCard> paginatedSolutions;
    private boolean isLoading = false;

    public SolutionsFragment(SolutionsFilter solutionsFilter) {
        this.solutionsFilter = solutionsFilter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeSolutionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.paginatedSolutions = new ArrayList<>();
        setupRecyclerView();
        setupPaginationControls();
        fetchSolutions(search, solutionsFilter, page, pageSize, sort);
    }

    private void setupRecyclerView() {
        solutionsAdapter = new SolutionsAdapter(requireContext(), paginatedSolutions);
        binding.solutionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.solutionsRecycler.setAdapter(solutionsAdapter);
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                fetchSolutions(search, solutionsFilter, page, pageSize, sort);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                fetchSolutions(search, solutionsFilter, page, pageSize, sort);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_options,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(2);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                page = 0;
                fetchSolutions(search, solutionsFilter, page, pageSize, sort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void fetchSolutions(String search, SolutionsFilter solutionsFilter, int page, int pageSize, String sort) {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Double minPrice;
        try {
            minPrice = Double.parseDouble(solutionsFilter.getMinPrice());
        } catch (Exception ignored) {
            minPrice = null;
        }

        Double maxPrice;
        try {
            maxPrice = Double.parseDouble(solutionsFilter.getMaxPrice());
        } catch (Exception ignored) {
            maxPrice = null;
        }

        String company = solutionsFilter.getCompany().equals("-") ? null : solutionsFilter.getCompany();

        Call<PagedResponse<SolutionCard>> call = ClientUtils.solutionService.getSolutions(
                search, solutionsFilter.getType(), solutionsFilter.getCategories(),
                solutionsFilter.getEventTypes(), company, minPrice, maxPrice,
                solutionsFilter.getStartDate(), solutionsFilter.getEndDate(),
                solutionsFilter.getAvailable(), page, pageSize, sort
        );
        call.enqueue(new Callback<PagedResponse<SolutionCard>>() {
            @Override
            public void onResponse(Call<PagedResponse<SolutionCard>> call, Response<PagedResponse<SolutionCard>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<SolutionCard> pagedResponse = response.body();

                    paginatedSolutions.clear();
                    paginatedSolutions.addAll(pagedResponse.getContent());
                    solutionsAdapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();

                } else {
                    showErrorDialog("Error while loading solutions!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<SolutionCard>> call, Throwable t) {
                showErrorDialog("Error while loading solutions!");
                showErrorDialog(t.getMessage());
                isLoading = false;
            }
        });
    }

    private void updatePaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setEnabled(page > 0);
        binding.btnNext.setEnabled(page < totalPages - 1);

        binding.tvPageInfo.setText(String.format("Page %d of %d", page + 1, totalPages));
    }

    private void showErrorDialog(String message) {
        if (isAdded() && getActivity() != null) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }

    public void updateFilters(SolutionsFilter solutionsFilter) {
        if (!isAdded()) {
            return;
        }

        this.solutionsFilter = solutionsFilter;

        page = 0;
        pageSize = 5;
        binding.spinnerPageSize.setSelection(2);

        fetchSolutions(search, solutionsFilter, page, pageSize, sort);
    }

    public void updateSort(String selectedSort) {
        sort = selectedSort;
        fetchSolutions(search, solutionsFilter, page, pageSize, sort);
    }

    public void updateSearch(String searchValue) {
        search = searchValue;
        fetchSolutions(search, solutionsFilter, page, pageSize, sort);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
