package com.example.eventy.users.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventy.users.model.UserNotificationInfo;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNotificationInfoViewModel extends ViewModel {
    private final MutableLiveData<UserNotificationInfo> notificationInfo = new MutableLiveData<>();
    private final MutableLiveData<Long> loggedInUserId = new MutableLiveData<>();

    public LiveData<UserNotificationInfo> getNotificationInfo() {
        return notificationInfo;
    }

    public void setNotificationInfo(UserNotificationInfo info) {
        notificationInfo.setValue(info);
    }

    public LiveData<Long> getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(Long userId) {
        loggedInUserId.setValue(userId);
        if (userId != null) {
            loadUserNotificationInfo(userId);
        }
    }

    private void loadUserNotificationInfo(Long userId) {
        Call<UserNotificationInfo> call = ClientUtils.userService.getUserNotificationsInfo(userId);
        call.enqueue(new Callback<UserNotificationInfo>() {
            @Override
            public void onResponse(Call<UserNotificationInfo> call, Response<UserNotificationInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserNotificationInfo userNotificationInfo = response.body();

                    notificationInfo.postValue(userNotificationInfo);
                    setNotificationInfo(userNotificationInfo);
                }
            }

            @Override
            public void onFailure(Call<UserNotificationInfo> call, Throwable t) {
                Log.wtf("TAMARA ERROR: NotificationInfo", "Failed to load UserNotificationInfo");
            }
        });
    }

}