package com.example.eventy.interactions.notifications;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.R;
import com.example.eventy.adapters.notifications.NotificationsAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentNotificationsBinding;
import com.example.eventy.interactions.model.Notification;
import com.example.eventy.users.model.UserNotificationInfo;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.users.view_model.UserNotificationInfoViewModel;
import com.example.eventy.utils.ClientUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationsAdapter notificationsAdapter;
    private int page = 0;
    private int pageSize = 5;
    private int totalPages = 99;
    private ArrayList<Notification> paginatedNotifications;
    private boolean isLoading = false;
    private Long loggedInUserId;

    public NotificationsFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.paginatedNotifications = new ArrayList<>();

        loggedInUserId = LoggedInHelperService.getId();
        if (loggedInUserId == null) {
            return;
        }

        setupRecyclerView();
        setupPaginationControls();
        setupMuteButton();
        fetchNotifications(page, pageSize);
    }

    private void setupRecyclerView() {
        UserNotificationInfoViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserNotificationInfoViewModel.class);
        UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();

        notificationsAdapter = new NotificationsAdapter(requireContext(), paginatedNotifications, currentInfo);
        binding.notificationsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationsRecycler.setAdapter(notificationsAdapter);
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                fetchNotifications(page, pageSize);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (page < totalPages - 1) {
                page++;
                fetchNotifications(page, pageSize);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_notifications_options,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(0);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                page = 0;
                fetchNotifications(page, pageSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void setupMuteButton() {
        UserNotificationInfoViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserNotificationInfoViewModel.class);
        LiveData<UserNotificationInfo> liveData = viewModel.getNotificationInfo();

        liveData.observe(getViewLifecycleOwner(), currentInfo -> {
            if (currentInfo != null) {
                updateMuteButtonUI(currentInfo.getAreNotificationsMuted());
            }
        });

        binding.muteAll.setOnClickListener(v -> {
            UserNotificationInfo currentInfo = liveData.getValue();
            if (currentInfo != null) {
                boolean newMuteState = !currentInfo.getAreNotificationsMuted();

                Call<Boolean> call = ClientUtils.userService.toggleNotifications(loggedInUserId, newMuteState);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                            Boolean currentMuteState = response.body();

                            UserNotificationInfo updatedInfo = new UserNotificationInfo(
                                    currentInfo.getUserId(),
                                    currentMuteState,
                                    currentInfo.getLastReadNotifications(),
                                    currentInfo.getHasNewNotifications()
                            );
                            viewModel.setNotificationInfo(updatedInfo);

                            updateMuteButtonUI(currentMuteState);

                        } else {
                            showErrorDialog("Error while changing the mute state!");
                            showErrorDialog(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        showErrorDialog("Error while changing the mute state!");
                        showErrorDialog(t.getMessage());
                    }
                });
            }
        });
    }

    private void updateMuteButtonUI(boolean areMuted) {
        if (areMuted) {
            binding.muteAll.setBackgroundResource(R.drawable.notifications_unmute_all);
            binding.muteAll.setText("Unmute All");
        } else {
            binding.muteAll.setBackgroundResource(R.drawable.notifications_mute_all);
            binding.muteAll.setText("Mute All");
        }
    }

    private void fetchNotifications(int page, int pageSize) {
        if (!isAdded()) {
            return;
        }

        if (isLoading) return;
        isLoading = true;

        Call<PagedResponse<Notification>> call = ClientUtils.notificationService.getNotificationsByUserId(loggedInUserId, page, pageSize);
        call.enqueue(new Callback<PagedResponse<Notification>>() {
            @Override
            public void onResponse(Call<PagedResponse<Notification>> call, Response<PagedResponse<Notification>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    PagedResponse<Notification> pagedResponse = response.body();
                    paginatedNotifications.clear();
                    paginatedNotifications.addAll(pagedResponse.getContent());
                    notificationsAdapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();

                } else {
                    showErrorDialog("Error while loading events!");
                    showErrorDialog(response.message());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PagedResponse<Notification>> call, Throwable t) {
                showErrorDialog("Error while loading events!");
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
    public void onPause() {
        super.onPause();
        updateNotificationsInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        updateNotificationsInfo();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        updateNotificationsInfo();
    }

    private void updateNotificationsInfo() {
        if (loggedInUserId != null) {
            Call<LocalDateTime> call = ClientUtils.userService.updateLastReadNotifications(loggedInUserId);
            call.enqueue(new Callback<LocalDateTime>() {
                @Override
                public void onResponse(Call<LocalDateTime> call, Response<LocalDateTime> response) {
                    if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                        LocalDateTime lastRead = response.body();

                        UserNotificationInfoViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserNotificationInfoViewModel.class);
                        UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();
                        if (currentInfo != null) {
                            UserNotificationInfo updatedInfo = new UserNotificationInfo(
                                    currentInfo.getUserId(),
                                    currentInfo.getAreNotificationsMuted(),
                                    lastRead,
                                    false
                            );
                            viewModel.setNotificationInfo(updatedInfo);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LocalDateTime> call, Throwable t) {
                    Log.wtf("TAMARA ERROR: NotificationInfo", "Can't load UsedNotificationInfo");
                }
            });
        }
    }

    public void addNewNotification(Notification notification) {
        UserNotificationInfoViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserNotificationInfoViewModel.class);
        UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();
        if (currentInfo != null) {
            UserNotificationInfo updatedInfo = new UserNotificationInfo(
                    currentInfo.getUserId(),
                    currentInfo.getAreNotificationsMuted(),
                    currentInfo.getLastReadNotifications(),
                    currentInfo.getHasNewNotifications()
            );
            notificationsAdapter.updateNotificationsInfo(updatedInfo);
        }
        paginatedNotifications.add(0, notification);
        notificationsAdapter.notifyItemInserted(0);
        binding.notificationsRecycler.scrollToPosition(0);
    }
}
