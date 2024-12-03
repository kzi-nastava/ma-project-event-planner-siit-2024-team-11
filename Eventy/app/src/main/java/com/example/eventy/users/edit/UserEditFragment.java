package com.example.eventy.users.edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentUserEditBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;

import java.util.ArrayList;

public class UserEditFragment extends Fragment {

    private FragmentUserEditBinding binding;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.user = new User(UserType.ORGANIZER, new ArrayList<>(), "organizer@gmail.com", "Some address 23",
                "+381 34 24 53 243", "Organizer", "Ofevents", null, null);

        if(this.user.getAccountType() != UserType.PROVIDER) {
            binding.editTitle.setText("Edit " + this.user.getFirstName() + " " + this.user.getLastName());
        }
        else {
            binding.editTitle.setText("Edit " + this.user.getName());
        }

        if(this.user.getAccountType() == UserType.PROVIDER) {
            UserProviderEditFragment userProviderEditFragment = new UserProviderEditFragment(user);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, userProviderEditFragment)
                    .commit();

            binding.editButton.setOnClickListener(userProviderEditFragment::confirmEdit);
        }
        else {
            UserOrganizerEditFragment userOrganizerEditFragment = new UserOrganizerEditFragment(user);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, userOrganizerEditFragment)
                    .commit();

            binding.editButton.setOnClickListener(userOrganizerEditFragment::confirmEdit);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}