package com.example.eventy.home.solutions.product_card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.eventy.databinding.FragmentEventCardBinding;
import com.example.eventy.databinding.FragmentProductCardBinding;

public class ProductCardFragment extends Fragment {
    private FragmentProductCardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductCardBinding.inflate(inflater, container, false);
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
