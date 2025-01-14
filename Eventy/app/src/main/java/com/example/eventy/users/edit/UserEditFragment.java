package com.example.eventy.users.edit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentUserEditBinding;
import com.example.eventy.users.model.User;
import com.example.eventy.users.model.UserType;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditFragment extends Fragment {

    private FragmentUserEditBinding binding;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Call<User> call = ClientUtils.userService.get(LoggedInHelperService.getId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();

                if(user.getUserType() != UserType.PROVIDER) {
                    binding.editTitle.setText("Edit " + user.getFirstName() + " " + user.getLastName());
                }
                else {
                    binding.editTitle.setText("Edit " + user.getName());
                }

                if(user.getUserType() == UserType.PROVIDER) {
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
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while getting user profile!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}