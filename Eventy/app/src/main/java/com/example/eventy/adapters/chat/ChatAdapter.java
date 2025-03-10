package com.example.eventy.adapters.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.MainActivity;
import com.example.eventy.R;
import com.example.eventy.chat.SingleChatFragment;
import com.example.eventy.chat.model.Chat;
import com.example.eventy.chat.model.Message;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.utils.FragmentTransition;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> allChats = new ArrayList<>();
    private LayoutInflater layoutInflater;
    public static SingleChatFragment openChat = null;
    public static MainActivity activity;

    public ChatAdapter(Context context, List<Chat> allChats, MainActivity activity) {
        if (allChats != null) {
            this.allChats = allChats;
        }
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        ChatAdapter.activity = activity;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.chat_instance, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = allChats.get(position);
        if (chat != null) {
            holder.id = chat.getChatId();
            holder.otherId = chat.getOtherId();
            holder.otherName.setText(chat.getOtherName());
            holder.lastMessageTime = chat.getLastMessageTime();
            holder.otherImageBase64 = chat.getOtherPicture();

            Drawable picture = PictureHelperService.getPicture(chat.getOtherPicture(), context);
            if (picture != null) {
                holder.otherImage.setBackground(picture);
            }
        }
    }

    public void handleNewMessage(Message message) {
        if (openChat != null) {
            openChat.handleNewMessage(message);
        }
    }

    @Override
    public int getItemCount() {
        return allChats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Long id;
        Long otherId;
        TextView otherName;
        ShapeableImageView otherImage;
        LocalDateTime lastMessageTime;
        String otherImageBase64;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            otherName = itemView.findViewById(R.id.chatter_name);
            otherImage = itemView.findViewById(R.id.chatter_profile_picture);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putLong("chatId", id);
            args.putLong("otherId", otherId);
            args.putString("otherName", otherName.getText().toString());
            args.putString("otherImage", otherImageBase64);

            openChat = new SingleChatFragment();
            openChat.setArguments(args);
            FragmentTransition.to(openChat, activity, true, R.id.nav_host_fragment_content_main);
        }
    }
}
