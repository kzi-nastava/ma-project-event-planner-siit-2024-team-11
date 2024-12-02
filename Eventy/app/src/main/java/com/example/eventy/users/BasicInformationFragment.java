package com.example.eventy.users;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentBasicInformationBinding;
import com.example.eventy.register.CarouselAdapter;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BasicInformationFragment extends Fragment {

    private FragmentBasicInformationBinding binding;
    private User user;
    private List<Uri> images = Arrays.asList(
            Uri.parse("android.resource://com.example.eventy/" + R.mipmap.logo)
    );
    private CarouselAdapter carouselAdapter;

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

        ViewPager2 viewPager = binding.profilePictures;

        if(this.user.getProfilePictures() != null && !this.user.getProfilePictures().isEmpty()) {
            this.images = this.user.getProfilePictures()
                    .stream()
                    .map(Uri::parse)
                    .collect(Collectors.toList());
        }

        carouselAdapter = new CarouselAdapter(this.images);
        viewPager.setAdapter(carouselAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}