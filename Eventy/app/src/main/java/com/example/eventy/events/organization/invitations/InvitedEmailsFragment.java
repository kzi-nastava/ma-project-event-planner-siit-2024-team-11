package com.example.eventy.events.organization.invitations;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventy.adapters.events.InvitedEmailsAdapter;
import com.example.eventy.databinding.FragmentEventInvitationSendingInvitedEmailsBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class InvitedEmailsFragment extends Fragment {
    private FragmentEventInvitationSendingInvitedEmailsBinding binding;
    private InvitedEmailsAdapter invitedEmailsAdapter;
    ArrayList<String> invitedEmails;
    private ImageView addEmailButton;
    private TextInputEditText emailTextInput;
    private TextView emailInputErrorText;

    public InvitedEmailsFragment() {
        // Required empty public constructor
    }

    public InvitedEmailsFragment(ImageView addEmailButton, TextInputEditText emailTextInput, TextView emailInputErrorText) {
        this.addEmailButton = addEmailButton;
        this.emailTextInput = emailTextInput;
        this.emailInputErrorText = emailInputErrorText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventInvitationSendingInvitedEmailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        invitedEmails = getInvitedEmails();
        invitedEmailsAdapter = new InvitedEmailsAdapter(requireContext(), invitedEmails);

        binding.invitedEmailsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.invitedEmailsRecycler.setAdapter(invitedEmailsAdapter);

        addEmailButton.setOnClickListener(v ->
            addItem(String.valueOf(emailTextInput.getText()))
        );
    }

    @NonNull
    private static ArrayList<String> getInvitedEmails() {
        ArrayList<String> invitedEmails = new ArrayList<>();
        return invitedEmails;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addItem(String item) {
        if (item.isEmpty()) {
            emailInputErrorText.setHeight(dpToPx(this.getContext(), 18));
            emailInputErrorText.setText("Email can't be empty!");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()) {
            emailInputErrorText.setHeight(dpToPx(this.getContext(), 18));
            emailInputErrorText.setText("Invalid email format!");
            return;
        }

        emailInputErrorText.setHeight(dpToPx(this.getContext(), 0));
        invitedEmails.add(item);
        invitedEmailsAdapter.notifyDataSetChanged();
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
