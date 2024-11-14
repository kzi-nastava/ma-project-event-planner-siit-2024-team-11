package com.example.eventy.home.events.event_card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.eventy.databinding.FragmentEventCardBinding;

public class EventCardFragment extends Fragment {
    private FragmentEventCardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // custom code

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
