package com.example.eventy.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private boolean isOrganiser = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load ChildFragmentA initially
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterOrganiserFragment())
                .commit();

        Button switchButton = binding.switchFragmentButton;
        switchButton.setOnClickListener(v -> {
            Fragment fragment = isOrganiser ? new RegisterProviderFragment() : new RegisterOrganiserFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            isOrganiser = !isOrganiser;
            binding.switchFragmentButton.setText(isOrganiser ? "Register as Product/Service Provider" : "Register as Event Organiser");
            binding.registerLabel.setText(isOrganiser ? "Register as Event Organiser" : "Register as Product/Service Provider");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}