package com.example.eventy.users.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentOtherUserProfilePageBinding;
import com.example.eventy.databinding.FragmentUserMyProfilePageBinding;
import com.example.eventy.users.BasicInformationFragment;
import com.example.eventy.users.MyCardsFragment;
import com.example.eventy.users.OrganizerEventsFragment;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.example.eventy.users.pup.PUPOwnServicesFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class UserMyProfilePageFragment extends Fragment {

    private FragmentUserMyProfilePageBinding binding;

    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserMyProfilePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.user = new User(UserType.ORGANIZER, new ArrayList<>(), "organizer@gmail.com", "Some address 23",
                "+381 34 24 53 243", "Organizer", "Ofevents", null, null);

        if(user.getAccountType() != UserType.AUTH_USER) {
            binding.upgradeButton.setVisibility(View.GONE);
        }

        TabLayout tabLayout = binding.tabLayout;

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_info));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_organize_event));
        if(user.getAccountType() == UserType.ORGANIZER || user.getAccountType() == UserType.PROVIDER) {
            tabLayout.addTab(tabLayout.newTab().setText("My")
                    .setIcon(user.getAccountType() == UserType.ORGANIZER ? R.drawable.icon_event_seat : R.drawable.icon_service));
        }
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_favorite).setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_favorite).setText("Solutions"));

        // Default fragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new BasicInformationFragment(this.user))
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment;
                if (tab.getPosition() == 0) {
                    selectedFragment = new BasicInformationFragment(user);
                } else if(tab.getPosition() == 1) {
                    selectedFragment = new UserCalendarFragment();
                } else if(tab.getPosition() == 2) {
                    selectedFragment = new MyCardsFragment(user);
                } else if(tab.getPosition() == 3) {
                    selectedFragment = new OrganizerEventsFragment();
                } else {
                    selectedFragment = new PUPOwnServicesFragment();
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

        if(user.getAccountType() != UserType.ORGANIZER && user.getAccountType() != UserType.PROVIDER) {
            tabLayout.setVisibility(View.GONE);
        }

        if(user.getAccountType() == UserType.PROVIDER) {
            binding.nameText.setText(user.getName());
        }
        else {
            binding.nameText.setText(user.getFirstName() + " " + user.getLastName());
        }

        binding.editButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            navController.popBackStack();

            navController.navigate(R.id.nav_edit_user);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}