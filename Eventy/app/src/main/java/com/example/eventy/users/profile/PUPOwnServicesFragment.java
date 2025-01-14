package com.example.eventy.users.profile;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.adapters.events.EventsAdapter;
import com.example.eventy.adapters.solutions.SolutionsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.MultiSpinner;
import com.example.eventy.databinding.FragmentPupOwnServicesBinding;
import com.example.eventy.events.model.EventCard;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.model.enums.Status;
import com.example.eventy.events.model.EventType;
import com.example.eventy.model.solution.Category;
import com.example.eventy.model.solution.Service;
import com.example.eventy.model.solution.Solution;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PUPOwnServicesFragment extends Fragment {
    private FragmentPupOwnServicesBinding binding;
    private SolutionsAdapter solutionsAdapter;
    private TextView showSelectedDateText;
    private Button dateRangeButton;
    private Long userId;
    private boolean isMyCards;
    private int page = 0;
    private int pageSize = 5;
    private boolean isLoading = false;
    private ArrayList<Solution> solutionCards;
    private boolean canGoFurther = true;

    public PUPOwnServicesFragment(Long userId, boolean isMyCards) {
        this.userId = userId;
        this.isMyCards = isMyCards;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPupOwnServicesBinding.inflate(inflater, container, false);

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

        binding.searchButton.setOnClickListener(v -> {
            page = 0;
            solutionCards = new ArrayList<>();
            loadCards(binding.searchInput.getQuery().toString(), page);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Error while loading")
                                .setMessage("Error while loading my solutions!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<Solution>> call, Throwable t) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error while loading")
                            .setMessage("Error while loading my solutions!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.icon_error)
                            .show();

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
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Error while loading")
                                .setMessage("Error while loading favorite solutions!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }

                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PagedResponse<Solution>> call, Throwable t) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error while loading")
                            .setMessage("Error while loading favorite solutions!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.icon_error)
                            .show();

                    isLoading = false;
                }
            });
        }
    }
}
