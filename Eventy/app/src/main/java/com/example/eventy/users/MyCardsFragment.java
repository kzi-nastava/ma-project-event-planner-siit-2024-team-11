package com.example.eventy.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentMyCardsBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.example.eventy.users.pup.PUPOwnServicesFragment;

public class MyCardsFragment extends Fragment {
    private FragmentMyCardsBinding binding;
    private User user;

    public MyCardsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(this.user.getAccountType() == UserType.ORGANIZER) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new OrganizerEventsFragment())
                    .commit();
        }
        else {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PUPOwnServicesFragment())
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}