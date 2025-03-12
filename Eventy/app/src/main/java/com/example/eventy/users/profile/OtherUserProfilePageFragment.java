package com.example.eventy.users.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.custom.BlockUserDialog;
import com.example.eventy.custom.CreateReportDialog;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentOtherUserProfilePageBinding;
import com.example.eventy.users.model.BlockUser;
import com.example.eventy.users.model.CreateReport;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserProfilePageFragment extends Fragment {
    private FragmentOtherUserProfilePageBinding binding;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOtherUserProfilePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Long id = -1L;
        if (getArguments() != null) {
            id = getArguments().getLong("userId");
        }

        Call<User> call = ClientUtils.userService.get(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();

                    TabLayout tabLayout = binding.tabLayout;

                    tabLayout.addTab(tabLayout.newTab().setText("Basic information"));
                    tabLayout.addTab(tabLayout.newTab().setText(user.getUserType() == UserType.ORGANIZER ? "My Events" : "My Products/Services"));

                    // Default fragment
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, new BasicInformationFragment(user))
                            .commit();

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            Fragment selectedFragment;
                            if (tab.getPosition() == 0) {
                                selectedFragment = new BasicInformationFragment(user);
                            } else {
                                selectedFragment = new MyCardsFragment(user);
                            }

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainer, selectedFragment)
                                    .commit();
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    if(user.getUserType() != UserType.ORGANIZER && user.getUserType() != UserType.PROVIDER) {
                        tabLayout.setVisibility(View.GONE);
                    }

                    if(user.getUserType() == UserType.PROVIDER) {
                        binding.nameText.setText(user.getName());
                    }
                    else {
                        binding.nameText.setText(user.getFirstName() + " " + user.getLastName());
                    }

                    binding.reportButton.setOnClickListener(v -> {
                        CreateReport createReport = new CreateReport(
                            null,
                            LoggedInHelperService.getId(),
                            user.getId()
                        );

                        CreateReportDialog createReportDialog = new CreateReportDialog(getActivity(), "Report \"" + user.getEmail() + "\"?", "Please provide a reason for the report!", createReport);
                        createReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        createReportDialog.show();
                    });

                    binding.blockButtom.setOnClickListener(v -> {
                        BlockUser blockUser = new BlockUser(
                            user.getId(),
                            LoggedInHelperService.getId()
                        );

                        BlockUserDialog blockUserDialog = new BlockUserDialog(getActivity(), "Block user?", "Are you sure you want to block \"" + user.getEmail() + "\"?", blockUser);
                        blockUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        blockUserDialog.show();
                    });

                } else if (response.code() == 403) {  // ---> blocked user
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Account Blocked", "You cannot access this profile page!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.setOnDismissListener(dialog -> {
                        NavController navController = Navigation.findNavController(container);
                        navController.popBackStack();
                        navController.navigate(R.id.nav_home);
                    });
                    errorOkDialog.show();

                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Unable to load user profile!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Unable to load user profile!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}