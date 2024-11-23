package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventAgendaCreationBinding;
import com.example.eventy.databinding.FragmentSeeAgendaBinding;
import com.example.eventy.events.model.Activity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeeAgendaFragment extends Fragment {

    private FragmentSeeAgendaBinding binding;
    private ArrayList<Activity> agenda;

    public SeeAgendaFragment(ArrayList<Activity> agenda) {
        this.agenda = agenda;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSeeAgendaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TableAdapter adapter = new TableAdapter(this.agenda);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}