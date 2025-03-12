package com.example.eventy.chat.services;

import com.example.eventy.chat.model.Chat;
import com.example.eventy.chat.model.Message;
import com.example.eventy.chat.model.MessageList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    String prefix = "chats";

    @POST(prefix + "/{otherId}")
    Call<Void> createChat(@Path("otherId") long otherId);

    @GET(prefix)
    Call<List<Chat>> getUserChats();

    @GET(prefix + "/messages/{chatId}")
    Call<MessageList> getMessages(@Path("chatId") long chatId);

    @POST(prefix + "/messages/{chatId}")
    Call<Void> sendMessage(@Path("chatId") long chatId, @Body Message newMessage);
}
