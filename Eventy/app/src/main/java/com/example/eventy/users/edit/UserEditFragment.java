package com.example.eventy.users.edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

        if(this.user.getAccountType() == UserType.PROVIDER) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserProviderEditFragment())
                    .commit();
        }
        else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserOrganizerEditFragment())
                    .commit();
        }

        binding.editButton.setOnClickListener(v -> {
            // logic for editing, call service


            NavController navController = Navigation.findNavController(v);

            // Problem with back button so we clear the backstack
            navController.popBackStack();

            // change nav
            navController.navigate(R.id.nav_home);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}