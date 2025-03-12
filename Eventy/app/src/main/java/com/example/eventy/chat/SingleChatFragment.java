package com.example.eventy.chat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.adapters.chat.ChatAdapter;
import com.example.eventy.adapters.chat.MessageAdapter;
import com.example.eventy.chat.model.Chat;
import com.example.eventy.chat.model.Message;
import com.example.eventy.chat.model.MessageList;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentAllChatsBinding;
import com.example.eventy.databinding.FragmentSingleChatBinding;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleChatFragment extends Fragment {

    private FragmentSingleChatBinding binding;
    private MessageAdapter adapter;
    private List<Message> allMessages = new ArrayList<>();
    private long chatId;
    private String otherName;
    private String otherImage;
    private long otherId;


    public SingleChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            chatId = getArguments().getLong("chatId");
            otherId = getArguments().getLong("otherId");
            otherName = getArguments().getString("otherName");
            otherImage = getArguments().getString("otherImage");
        }

        binding = FragmentSingleChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MessageAdapter(requireContext(), allMessages);
        fetchMessages();

        binding.chatterName.setText(otherName);
        Drawable picture = PictureHelperService.getPicture(otherImage, getContext());
        if (picture != null) {
            binding.chatterProfilePicture.setBackground(picture);
        }

        binding.messagesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.messagesRecycler.setAdapter(adapter);

        binding.chatMoreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);

            Menu menu = popupMenu.getMenu();

            menu.add(Menu.NONE, 0, Menu.NONE, "Report user");
            menu.add(Menu.NONE, 1, Menu.NONE, "Block user");

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        // TO-DO: Report user functionality
                        Toast.makeText(getContext(), "Report user clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case 1:
                        // TO-DO: Block user functionality
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });

        binding.sendButton.setOnClickListener(v -> {
            sendMessage();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchMessages() {
        Call<MessageList> call = ClientUtils.chatService.getMessages(chatId);
        call.enqueue(new Callback<MessageList>() {
            @Override
            public void onResponse(Call<MessageList> call, Response<MessageList> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    allMessages.clear();
                    allMessages.addAll(response.body().getAllMessages());
                    adapter.notifyDataSetChanged();
                    binding.messagesRecycler.post(() -> binding.messagesRecycler.scrollToPosition(adapter.getItemCount() - 1));
                } else {
                    if (getActivity() != null) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading chats! Try again.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageList> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading categories! Try again.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    public void handleNewMessage(Message newMessage) {
        if (newMessage.getSenderId() == otherId || newMessage.getSenderId() == LoggedInHelperService.getId().longValue()) {
            allMessages.add(newMessage);
            adapter.notifyDataSetChanged();
        }
    }

    private void sendMessage() {
        if (!binding.messageEditText.getText().toString().trim().isEmpty()) {
            Call<Void> call = ClientUtils.chatService.sendMessage(chatId, new Message(LoggedInHelperService.getId(), binding.messageEditText.getText().toString().trim(), LocalDateTime.now()));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        binding.messageEditText.setText("");
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error sending the message");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Connection error");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        }
    }

}