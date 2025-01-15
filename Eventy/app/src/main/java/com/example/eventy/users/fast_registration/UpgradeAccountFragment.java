package com.example.eventy.users.fast_registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentUserFastRegistrationUpgradeAccountBinding;

public class UpgradeAccountFragment extends Fragment {

    private FragmentUserFastRegistrationUpgradeAccountBinding binding;
    private boolean isOrganiser = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserFastRegistrationUpgradeAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UpgradeAccountOrganiserFragment())
                .commit();

        Button switchButton = binding.switchFragmentButton;
        switchButton.setOnClickListener(v -> {
            Fragment fragment = isOrganiser ? new UpgradeAccountProviderFragment() : new UpgradeAccountOrganiserFragment();
            getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
            isOrganiser = !isOrganiser;
            binding.registerLabel.setText(isOrganiser ? "Event Organiser" : "Solution Provider");
        });

        binding.loginHereButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.nav_login);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

