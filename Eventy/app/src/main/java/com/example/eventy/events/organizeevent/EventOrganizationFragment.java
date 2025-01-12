package com.example.eventy.events.organizeevent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventOrganizationBinding;
import com.example.eventy.events.model.OrganizeEvent;
import com.example.eventy.events.model.Event;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

enum EventOrganizationStage {
    BASIC_INFORMATION,
    AGENDA_CREATION,
    INVITATION_SENDING
}

public class EventOrganizationFragment extends Fragment {
    private FragmentEventOrganizationBinding binding;
    private EventOrganizationStage eventOrganizationStage;
    private boolean isEventPublic;
    private OrganizeEvent organizeEvent;
    Fragment fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventOrganizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventOrganizationStage = EventOrganizationStage.BASIC_INFORMATION;
        isEventPublic = true;

        EventOrganizationBasicInformationFragment eventOrganizationBasicInformationFragmentFragment = new EventOrganizationBasicInformationFragment();
        EventAgendaCreation eventAgendaCreation = new EventAgendaCreation();
        EventInvitationSendingFragment eventInvitationSendingFragment = new EventInvitationSendingFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.formContainer, eventOrganizationBasicInformationFragmentFragment)
                .commit();

        binding.backButton.setOnClickListener(v -> {
            Fragment fragment;
            String title;

            if(eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION) {
                eventOrganizationStage = EventOrganizationStage.BASIC_INFORMATION;
                fragment = eventOrganizationBasicInformationFragmentFragment;
                title = "Organize an Event";
                binding.backButton.setEnabled(false);
            } else if(eventOrganizationStage == EventOrganizationStage.INVITATION_SENDING) {
                eventOrganizationStage = EventOrganizationStage.AGENDA_CREATION;
                fragment = eventAgendaCreation;
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
            String title = "Organize an Event";
            String submitText = "NEXT";

            if(eventOrganizationStage == EventOrganizationStage.BASIC_INFORMATION) {
                if(eventOrganizationBasicInformationFragmentFragment.isValid()) {
                    eventOrganizationStage = EventOrganizationStage.AGENDA_CREATION;
                    fragment = eventAgendaCreation;
                    title = "Add Agenda";
                    this.isEventPublic = eventOrganizationBasicInformationFragmentFragment.isPublic();
                    binding.backButton.setEnabled(true);
                    this.organizeEvent = new OrganizeEvent(
                            eventOrganizationBasicInformationFragmentFragment.getName(),
                            eventOrganizationBasicInformationFragmentFragment.getDescription(),
                            eventOrganizationBasicInformationFragmentFragment.getMaxNumberParticipants(),
                            eventOrganizationBasicInformationFragmentFragment.isPublic(),
                            eventOrganizationBasicInformationFragmentFragment.getEventTypeId(),
                            eventOrganizationBasicInformationFragmentFragment.getLocation(),
                            eventOrganizationBasicInformationFragmentFragment.getDate(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            LoggedInHelperService.getId()
                    );
                }
                else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please make sure all fields are filled and filled with real values!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                    return;
                }
            } else if(eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION) {
                if(eventAgendaCreation.isValid()) {
                    this.organizeEvent.setAgenda(eventAgendaCreation.getAgenda());
                    if(this.isEventPublic) {
                        Call<Event> call = ClientUtils.eventService.organizeEvent(organizeEvent);
                        call.enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(Call<Event> call, Response<Event> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new AlertDialog.Builder(getContext())
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
                                } else {
                                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while organizing an event!");
                                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    errorOkDialog.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Event> call, Throwable t) {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while organizing an event!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        });

                        return;
                    }
                    else {
                        eventOrganizationStage = EventOrganizationStage.INVITATION_SENDING;
                        fragment = eventInvitationSendingFragment;
                        title = "Send invitations";
                    }
                }
                else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please make sure that there is at least one activity added!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                    return;
                }
            } else {
                this.organizeEvent.setEmails(eventInvitationSendingFragment.getInvitedEmails());
                Call<Event> call = ClientUtils.eventService.organizeEvent(organizeEvent);
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new AlertDialog.Builder(getContext())
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
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while organizing an event!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while organizing an event!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });

                return;
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.formContainer, fragment)
                    .addToBackStack(null)
                    .commit();

            if(eventOrganizationStage == EventOrganizationStage.INVITATION_SENDING ||
                    (this.isEventPublic && eventOrganizationStage == EventOrganizationStage.AGENDA_CREATION)) {
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