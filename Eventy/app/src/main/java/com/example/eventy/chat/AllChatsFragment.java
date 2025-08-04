package com.example.eventy.chat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventy.MainActivity;
import com.example.eventy.adapters.chat.ChatAdapter;
import com.example.eventy.chat.model.Chat;
import com.example.eventy.chat.model.Message;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentAllChatsBinding;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllChatsFragment extends Fragment {

    private FragmentAllChatsBinding binding;
    private ChatAdapter adapter;
    private List<Chat> allChats = new ArrayList<>();
    private MainActivity activity;

    public AllChatsFragment() {
        // Required empty public constructor
    }

    public AllChatsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ChatAdapter(requireContext(), allChats, activity);
        fetchChats();

        binding.chatsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatsRecycler.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void handleNewMessage(Message newMessage) {
        Chat wantedChat = allChats.stream().filter(v -> v.getOtherId() == newMessage.getSenderId()).findFirst().orElse(null);

        if (wantedChat != null) {
            allChats.remove(wantedChat);
            allChats.add(0, wantedChat);
            adapter.notifyDataSetChanged();
            adapter.handleNewMessage(newMessage);
        } else {
            if (newMessage.getSenderId() == LoggedInHelperService.getId().longValue()) {
                adapter.handleNewMessage(newMessage);
            }
        }

    }

    private void fetchChats() {
        Call<List<Chat>> call = ClientUtils.chatService.getUserChats();
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null && getActivity() != null) {
                    allChats.clear();
                    allChats.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading chats! Try again.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading categories! Try again.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }
}