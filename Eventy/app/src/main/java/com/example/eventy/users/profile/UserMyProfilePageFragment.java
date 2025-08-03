package com.example.eventy.users.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserMyProfilePageBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMyProfilePageFragment extends Fragment {
    private FragmentUserMyProfilePageBinding binding;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserMyProfilePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Call<User> call = ClientUtils.userService.get(LoggedInHelperService.getId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();

                if (user == null) {
                    if (isAdded() && getActivity() != null) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Unable to load the currently logged-in user!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.setOnDismissListener(dialog -> {
                            NavController navController = Navigation.findNavController(container);
                            navController.popBackStack();
                            navController.navigate(R.id.nav_home);
                        });
                        errorOkDialog.show();
                        return;
                    }
                }

                if (user.getUserType() != UserType.AUTHENTICATED) {
                    binding.upgradeButton.setVisibility(View.GONE);
                } else {
                    binding.upgradeButton.setVisibility(View.VISIBLE);
                    binding.upgradeButton.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(container);
                        navController.popBackStack();
                        navController.navigate(R.id.upgrade_profile);
                    });
                }

                TabLayout tabLayout = binding.tabLayout;

                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_info));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_organize_event));
                if(user.getUserType() == UserType.ORGANIZER || user.getUserType() == UserType.PROVIDER) {
                    tabLayout.addTab(tabLayout.newTab().setText("My")
                            .setIcon(user.getUserType() == UserType.ORGANIZER ? R.drawable.icon_event_seat : R.drawable.icon_service));
                }
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_favorite).setText("Events"));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_favorite).setText("Solutions"));

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
                        } else if (tab.getPosition() == 1) {
                            selectedFragment = new UserCalendarFragment();
                        } else if (tab.getPosition() == 2) {
                            selectedFragment = new MyCardsFragment(user);
                        } else if (tab.getPosition() == 3) {
                            selectedFragment = new OrganizerEventsFragment(user.getId(), false);
                        } else {
                            selectedFragment = new PUPOwnServicesFragment(user.getId(), false);
                        }

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, selectedFragment)
                                .commit();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });

                if(user.getUserType() != UserType.ORGANIZER && user.getUserType() != UserType.PROVIDER) {
                    tabLayout.setVisibility(View.GONE);
                }

                if (user.getUserType() == UserType.PROVIDER) {
                    binding.nameText.setText(user.getName());
                    binding.nameText.setVisibility(View.VISIBLE);
                } else if (user.getUserType() == UserType.AUTHENTICATED) {
                    binding.nameText.setVisibility(View.GONE);
                } else {
                    binding.nameText.setText(user.getFirstName() + " " + user.getLastName());
                    binding.nameText.setVisibility(View.VISIBLE);
                }

                binding.editButton.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_edit_user);
                });

                binding.deactivateButton.setOnClickListener(v -> {
                    Call<Void> callDeactivate = ClientUtils.userService.deactivate(LoggedInHelperService.getId());
                    callDeactivate.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle(" Successful deactivation")
                                        .setMessage("User is now deactivated.")
                                        .setIcon(R.drawable.icon_success_png)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // this is like logout
                                                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.remove("JWT_TOKEN");
                                                editor.apply();

                                                LoggedInHelperService.manageNavigationItems();

                                                NavController navController = Navigation.findNavController(v);
                                                navController.popBackStack();
                                                navController.navigate(R.id.nav_home);
                                            }})
                                        .show();
                            } else {
                                showErrorDialog("Error while deactivating account! You are not permitted do deactivate this account while you still have " + (user.getUserType() == UserType.ORGANIZER ? "organized events" : "reserved solutions") + ".");
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            showErrorDialog("Error while deactivating account! You are not permitted do deactivate this account while you still have " + (user.getUserType() == UserType.ORGANIZER ? "organized events" : "reserved solutions") + ".");
                        }
                    });
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showErrorDialog("Error while getting user profile!");
            }
        });

        return root;
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