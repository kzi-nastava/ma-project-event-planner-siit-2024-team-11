package com.example.eventy.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.databinding.FragmentBasicInformationBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;

public class BasicInformationFragment extends Fragment {

    private FragmentBasicInformationBinding binding;
    private User user;

    public BasicInformationFragment(User user) {
        this.user = user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBasicInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(user.getAccountType() != UserType.PROVIDER) {
            binding.descriptionText.setVisibility(View.GONE);
        }
        else {
            binding.descriptionText.setText(this.user.getDescription());
        }

        binding.emailText.setText(this.user.getEmail());
        binding.addressText.setText(this.user.getAddress());
        binding.phoneNumberText.setText(this.user.getPhoneNumber());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}