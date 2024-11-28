package com.example.eventy.adapters.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;

import java.util.ArrayList;

public class InvitedEmailsAdapter extends RecyclerView.Adapter<InvitedEmailsAdapter.InvitedEmailsViewHolder> {
    private ArrayList<String> invitedEmails;
    private LayoutInflater layoutInflater;

    public InvitedEmailsAdapter(Context context, ArrayList<String> invitedEmails) {
        this.invitedEmails = invitedEmails;
        this.layoutInflater = LayoutInflater.from(context);
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

            /*Button seeMoreButton = holder.itemView.findViewById(R.id.see_more_button);
            seeMoreButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "See more: " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            Button favoriteButton = holder.itemView.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Favorite: " + event.getName(), Toast.LENGTH_SHORT).show();
            });*/
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
