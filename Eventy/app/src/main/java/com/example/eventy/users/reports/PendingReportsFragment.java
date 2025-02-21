package com.example.eventy.users.reports;

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
import com.example.eventy.adapters.reports.PendingReportsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentPendingReportsBinding;
import com.example.eventy.reviews.model.Review;
import com.example.eventy.users.model.Report;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReportsFragment extends Fragment {
    private FragmentPendingReportsBinding binding;
    private PendingReportsAdapter pendingReportsAdapter;
    private int page = 0;
    private int pageSize = 10;
    private int totalPages = 99;
    private ArrayList<Report> paginatedReports;
    private boolean isLoading = false;

    public PendingReportsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPendingReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.paginatedReports = new ArrayList<>();

        setupRecyclerView();
        setupPaginationControls();
        fetchPendingReports(page, pageSize);
    }

    private void setupRecyclerView() {
        pendingReportsAdapter = new PendingReportsAdapter(requireContext(), paginatedReports, this);
        binding.allReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.allReports.setAdapter(pendingReportsAdapter);
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                fetchPendingReports(page, pageSize);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                fetchPendingReports(page, pageSize);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_pending_reviews_options,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(1);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                page = 0;
                fetchPendingReports(page, pageSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    public void fetchPendingReports(int page, int pageSize) {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Call<PagedResponse<Report>> call = ClientUtils.reportService.getPendingReports(page, pageSize);
        call.enqueue(new Callback<PagedResponse<Report>>() {
            @Override
            public void onResponse(Call<PagedResponse<Report>> call, Response<PagedResponse<Report>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<Report> pagedResponse = response.body();
                    paginatedReports.clear();
                    paginatedReports.addAll(pagedResponse.getContent());
                    pendingReportsAdapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();

                } else {
                    showErrorDialog("Error while loading pending reports!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<Report>> call, Throwable t) {
                showErrorDialog("Error while loading pending reports!");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
