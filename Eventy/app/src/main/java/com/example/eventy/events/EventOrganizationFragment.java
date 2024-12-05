package com.example.eventy.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventOrganizationBinding;

import java.util.ArrayList;

enum EventOrganizationStage {
    BASIC_INFORMATION,
    AGENDA_CREATION,
    INVITATION_SENDING
}

public class EventOrganizationFragment extends Fragment {
    private FragmentEventOrganizationBinding binding;
    private EventOrganizationStage eventOrganizationStage;
    private boolean isEventPublic;
    Fragment fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventOrganizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventOrganizationStage = EventOrganizationStage.BASIC_INFORMATION;
        isEventPublic = true;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.formContainer, new EventOrganizationBasicInformationFragment())
                .commit();

        binding.backButton.setOnClickListener(v -> {
            Fragment fragment;
            String title;

            if(eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION) {
                eventOrganizationStage = EventOrganizationStage.BASIC_INFORMATION;
                fragment = new EventOrganizationBasicInformationFragment();
                title = "Organize an Event";
                binding.backButton.setEnabled(false);
            } else if(eventOrganizationStage == EventOrganizationStage.INVITATION_SENDING) {
                eventOrganizationStage = EventOrganizationStage.AGENDA_CREATION;
                fragment = new EventAgendaCreation();
                title = "Add Agenda";
            } else {
                return;
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.formContainer, fragment)
                    .addToBackStack(null)
                    .commit();

            if(binding.submitButton.getText().equals("ADD EVENT")) {
                binding.submitButton.setText("NEXT");
                binding.submitButton.setIconResource(R.drawable.icon_arrow_forward);
            }
            binding.titleText.setText(title);
        });

        binding.submitButton.setOnClickListener(v -> {
            String title;
            String submitText = "NEXT";

            if(eventOrganizationStage == EventOrganizationStage.BASIC_INFORMATION) {
                eventOrganizationStage = EventOrganizationStage.AGENDA_CREATION;
                fragment = new EventAgendaCreation();
                title = "Add Agenda";
                binding.backButton.setEnabled(true);
            } else if(eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION) {
                eventOrganizationStage = EventOrganizationStage.INVITATION_SENDING;
                fragment = new EventInvitationSendingFragment();
                title = "Send invitations";
            } else {
                EventInvitationSendingFragment invitationFragment = (EventInvitationSendingFragment) fragment;
                ArrayList<String> invitedEmails = invitationFragment.getInvitedEmails();

                if (invitedEmails.isEmpty()) {
                    // event creation failed (no invited people)
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(this.getActivity(), "Error", "At least one person needs to be invited before proceeding!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                } else {
                    // event creation successful
                    StringBuilder emails = new StringBuilder();
                    for (String email : invitedEmails) {
                        emails.append(email);
                        emails.append(",");
                    }
                    Toast.makeText(this.getContext(), emails.toString(), Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(this.getContext())
                        .setTitle(" Successful creation")
                        .setMessage("Your event has been created successfully! Invitations have been sent to the specified email addresses.")
                        .setIcon(R.drawable.icon_success_png)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // this leads to home (for now), will lead to the event page or user profile
                                NavController navController = Navigation.findNavController(v);
                                navController.popBackStack();
                                navController.navigate(R.id.nav_home);
                            }})
                        .show();
                }

                return;
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.formContainer, fragment)
                    .addToBackStack(null)
                    .commit();

            if(eventOrganizationStage == EventOrganizationStage.INVITATION_SENDING ||
                    (!isEventPublic && eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION)) {
                submitText = "ADD EVENT";
                binding.submitButton.setIconResource(R.drawable.icon_add);
            }
            binding.submitButton.setText(submitText);
            binding.titleText.setText(title);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}