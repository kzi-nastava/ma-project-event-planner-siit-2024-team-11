package com.example.eventy.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentOtherUserProfilePageBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class OtherUserProfilePageFragment extends Fragment {
    private FragmentOtherUserProfilePageBinding binding;

    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOtherUserProfilePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.user = new User(UserType.ORGANIZER, new ArrayList<>(), "organizer@gmail.com", "Some address 23",
                "+381 34 24 53 243", "Organizer", "Ofevents", null, null);

        TabLayout tabLayout = binding.tabLayout;

        tabLayout.addTab(tabLayout.newTab().setText("Basic information"));
        tabLayout.addTab(tabLayout.newTab().setText(user.getAccountType() == UserType.ORGANIZER ? "My Events" : "My Products/Services"));

        // Default fragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new BasicInformationFragment())
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment;
                if (tab.getPosition() == 0) {
                    selectedFragment = new BasicInformationFragment();
                } else {
                    selectedFragment = new MyCardsFragment();
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}