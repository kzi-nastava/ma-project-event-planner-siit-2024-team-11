package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventAgendaCreationBinding;
import com.example.eventy.events.model.Activity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class EventAgendaCreation extends Fragment {

    private FragmentEventAgendaCreationBinding binding;
    private ArrayList<Activity> agenda;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.agenda = new ArrayList<>();

        binding = FragmentEventAgendaCreationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TabLayout tabLayout = binding.tabLayout;

        tabLayout.addTab(tabLayout.newTab().setText("Basic info"));
        tabLayout.addTab(tabLayout.newTab().setText("See Agenda"));

        // Default fragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new AddActivityFragment(this.agenda))
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment;
                if (tab.getPosition() == 0) {
                    selectedFragment = new AddActivityFragment(agenda);
                } else {
                    selectedFragment = new SeeAgendaFragment(agenda);
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}