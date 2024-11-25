package com.example.eventy.events;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentEventOrganizationBinding;

enum EventOrganizationStage {
    BASIC_INFORMATION,
    AGENDA_CREATION,
    INVITATION_SENDING
}

public class EventOrganizationFragment extends Fragment {
    private FragmentEventOrganizationBinding binding;
    private EventOrganizationStage eventOrganizationStage;
    private boolean isEventPublic;

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
            Fragment fragment;
            String title;
            String submitText = "NEXT";

            if(eventOrganizationStage == EventOrganizationStage.BASIC_INFORMATION) {
                eventOrganizationStage = EventOrganizationStage.AGENDA_CREATION;
                fragment = new EventAgendaCreation();
                title = "Add Agenda";
                binding.backButton.setEnabled(true);
            } else if(eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION) {
                eventOrganizationStage = EventOrganizationStage.INVITATION_SENDING;
                fragment = new EventInvitationSending();
                title = "Invite people";
            } else {
                // here we will create the event or maybe upstairs somewhere
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