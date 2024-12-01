package com.example.eventy.adapters.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.events.organization.invitations.InvitedEmailsFragment;

import java.util.ArrayList;

public class InvitedEmailsAdapter extends RecyclerView.Adapter<InvitedEmailsAdapter.InvitedEmailsViewHolder> {
    private ArrayList<String> invitedEmails;
    private LayoutInflater layoutInflater;
    private InvitedEmailsFragment invitedEmailsFragment;

    public InvitedEmailsAdapter(Context context, ArrayList<String> invitedEmails, InvitedEmailsFragment invitedEmailsFragment) {
        this.invitedEmails = invitedEmails;
        this.layoutInflater = LayoutInflater.from(context);
        this.invitedEmailsFragment = invitedEmailsFragment;
    }

    @NonNull
    @Override
    public InvitedEmailsAdapter.InvitedEmailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_event_invitation_email, parent, false);

        return new InvitedEmailsAdapter.InvitedEmailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitedEmailsAdapter.InvitedEmailsViewHolder holder, int position) {
        String email = invitedEmails.get(position);
        if (email != null) {
            String text = position + 1 +  ". " + email;
            holder.invitedEmail.setText(text);

            Button removeEmailButton = holder.itemView.findViewById(R.id.remove_email_button);
            TextView invitedEmailValueText = holder.itemView.findViewById(R.id.invited_email_value);

            removeEmailButton.setOnClickListener(v -> {
                invitedEmailsFragment.removeItem((String) invitedEmailValueText.getText());
            });
        }
    }

    @Override
    public int getItemCount() {
        return invitedEmails.size();
    }

    public static class InvitedEmailsViewHolder extends RecyclerView.ViewHolder {
        TextView invitedEmail;

        public InvitedEmailsViewHolder(@NonNull View itemView) {
            super(itemView);
            invitedEmail = itemView.findViewById(R.id.invited_email_value);
        }
    }
}
