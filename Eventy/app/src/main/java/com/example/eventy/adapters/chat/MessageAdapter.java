package com.example.eventy.adapters.chat;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.chat.model.Message;
import com.example.eventy.chat.model.MessageList;
import com.example.eventy.users.services.LoggedInHelperService;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public MessageAdapter(Context context, List<Message> messages) {
        if (messages != null) {
            this.messages = messages;
        }
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.message_instance, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message != null) {
            holder.senderId = message.getSenderId();
            holder.message.setText(message.getMessage());
            holder.timestamp = message.getTimestamp();

            Long currentId = LoggedInHelperService.getId();
            if (currentId != null && currentId.longValue() == holder.senderId) {
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.own_chatter_message_text_color));

                TextView chatMessageContent = holder.itemView.findViewById(R.id.chat_message_content);
                chatMessageContent.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.own_chatter_message_background) );
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chatMessageContent.getLayoutParams();

                layoutParams.startToStart = -1; // Align to parent's start
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // Align to parent's end

                chatMessageContent.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        Long senderId;
        TextView message;
        LocalDateTime timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_content);
        }
    }
}
