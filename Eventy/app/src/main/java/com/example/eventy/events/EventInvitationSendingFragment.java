package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventInvitationSendingBinding;
import com.example.eventy.events.organization.invitations.InvitedEmailsFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EventInvitationSendingFragment extends Fragment {

    private FragmentEventInvitationSendingBinding binding;
    private InvitedEmailsFragment invitedEmailsFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventInvitationSendingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView addEmailButton = root.findViewById(R.id.add_email_button);
        TextInputEditText emailTextInput = root.findViewById(R.id.email_input);
        TextView emailInputErrorText = root.findViewById(R.id.email_input_error);

        invitedEmailsFragment = new InvitedEmailsFragment(addEmailButton, emailTextInput, emailInputErrorText);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_invited_people, invitedEmailsFragment)
                .commit();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ArrayList<String> getInvitedEmails() {
        return invitedEmailsFragment.getInvitedEmails();
    }
}