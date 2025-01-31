package com.example.eventy.solutions.solutionCategories;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.adapters.solutions.SolutionCategoryAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.custom.SolutionCategoryDialog;
import com.example.eventy.databinding.FragmentCategoryManagementBinding;
import com.example.eventy.model.enums.Status;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.solutions.services.SolutionCategoryService;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryManagementFragment extends Fragment {

    private FragmentCategoryManagementBinding binding;
    private SolutionCategoryAdapter adapter;
    private int pageIndex = 0;
    private int pageSize = 5;
    private int totalPages = 99;
    private boolean isLoading = false;
    private List<CategoryWithID> paginatedCategories = new ArrayList<>();


    public CategoryManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SolutionCategoryAdapter(requireContext(), paginatedCategories);

        setupPaginationControls();
        fetchCategories();


        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.categoriesRecycler.setAdapter(adapter);

        binding.floatingActionButton.setOnClickListener(v -> {
            SolutionCategoryDialog dialog = new SolutionCategoryDialog(getContext(), null, null, null, (idValue, nameValue, descriptionValue) -> {
                if (idValue != -1L) {
                    Toast.makeText(getContext(), "Unexpected error!", Toast.LENGTH_SHORT).show();
                } else {
                    Call<CategoryWithID> call = ClientUtils.categoryService.createCategory(new Category(nameValue, descriptionValue, Status.ACCEPTED));
                    call.enqueue(new Callback<CategoryWithID>() {
                        @Override
                        public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                            if (response.isSuccessful()) {
                                fetchCategories();
                            } else {
                                Toast.makeText(getContext(), "Error with creating new category!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryWithID> call, Throwable t) {
                            Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            dialog.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchCategories() {
        if (isLoading) return;
        isLoading = true;
        Call<PagedResponse<CategoryWithID>> call = ClientUtils.categoryService.getActiveCategoriesPaged(pageIndex, pageSize);

        call.enqueue(new Callback<PagedResponse<CategoryWithID>>() {
            @Override
            public void onResponse(Call<PagedResponse<CategoryWithID>> call, Response<PagedResponse<CategoryWithID>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<CategoryWithID> pagedResponse = response.body();

                    paginatedCategories.clear();
                    paginatedCategories.addAll(pagedResponse.getContent());
                    adapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();
                } else {
                    if (isAdded() && getActivity() != null) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading categories! Try again.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<CategoryWithID>> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading categories! Try again.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
                isLoading = false;
            }
        });
    }

    private void updatePaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setEnabled(pageIndex > 0);
        binding.btnNext.setEnabled(pageIndex < totalPages - 1);

        binding.tvPageInfo.setText(String.format("Page %d of %d", pageIndex + 1, totalPages));
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (pageIndex > 0) {
                pageIndex--;
                fetchCategories();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (pageIndex < totalPages - 1) {
                pageIndex++;
                fetchCategories();
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
                pageIndex = 0;
                fetchCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }
}