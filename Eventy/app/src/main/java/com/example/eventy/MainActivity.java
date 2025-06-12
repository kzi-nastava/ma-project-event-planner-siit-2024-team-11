package com.example.eventy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.NavigationUI;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.eventy.chat.AllChatsFragment;
import com.example.eventy.chat.model.Message;
import com.example.eventy.common.EncryptionUtil;
import com.example.eventy.databinding.ActivityMainBinding;
import com.example.eventy.interactions.model.Notification;
import com.example.eventy.interactions.notifications.NotificationsFragment;
import com.example.eventy.interactions.service.NotificationHelper;
import com.example.eventy.users.model.AuthResponse;
import com.example.eventy.users.model.UserNotificationInfo;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.users.view_model.UserNotificationInfoViewModel;
import com.example.eventy.utils.ClientUtils;
import com.example.eventy.utils.FragmentTransition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MenuItem notificationItem;
    private Boolean openedNotifications = false;
    private StompClient mStompClient;
    private StompClient chatStompClient;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CompositeDisposable chatCompositeDisposable = new CompositeDisposable();
    NotificationsFragment notificationsFragment;
    AllChatsFragment allChatsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientUtils.init(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        NotificationHelper.createNotificationChannel(this);

        LoggedInHelperService.init(getApplicationContext(), binding.navView, this);
        String jwtToken = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE)
                .getString("JWT_TOKEN", null);

        if (jwtToken != null) {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            LocalDateTime tokenExpires = decodedJWT.getExpiresAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            if (tokenExpires.isBefore(LocalDateTime.now())) {
                this.logout();
                LoggedInHelperService.manageNavigationItems();

                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
                openedNotifications = false;
            } else {
                UserNotificationInfoViewModel userNotificationInfoViewModel = new ViewModelProvider(this).get(UserNotificationInfoViewModel.class);
                userNotificationInfoViewModel.setLoggedInUserId(LoggedInHelperService.getId());

                connectToMobileWebSocket(jwtToken);
                connectToChatSocket(jwtToken);
            }
        }

        UserNotificationInfoViewModel userViewModel = new ViewModelProvider(this).get(UserNotificationInfoViewModel.class);
        userViewModel.getNotificationInfo().observe(this, userNotificationInfo -> {
            if (userNotificationInfo != null) {
                invalidateOptionsMenu();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_login, R.id.nav_register, R.id.nav_manipulate_service,
            R.id.nav_event_organization, R.id.nav_own_services_test, R.id.nav_event_types, R.id.nav_add_event_type,
            R.id.nav_edit_event_type, R.id.nav_event_type_details, R.id.nav_other_user_profile_page,
            R.id.nav_edit_user, R.id.nav_my_profile, R.id.service_reservation, R.id.fast_registration,
            R.id.upgrade_profile, R.id.nav_category_home, R.id.nav_event_details, R.id.nav_solution_details,
            R.id.nav_event_stats, R.id.nav_notifications, R.id.nav_product_creation, R.id.nav_product_update,
            R.id.nav_purchase, R.id.nav_pending_reviews, R.id.nav_event_edit, R.id.nav_single_chat, R.id.nav_all_chats,
            R.id.nav_pricelist)
            .setOpenableLayout(drawer)
            .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // so the title in the navbar doesn't show up
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
            }
        });
        LoggedInHelperService.manageNavigationItems();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (intent.getBooleanExtra("open_notifications", false)) {
            Long userId = LoggedInHelperService.getId();

            if (userId != null) {
                UserNotificationInfoViewModel userNotificationInfoViewModel = new ViewModelProvider(this).get(UserNotificationInfoViewModel.class);
                userNotificationInfoViewModel.setLoggedInUserId(LoggedInHelperService.getId());

                notificationsFragment = new NotificationsFragment();
                FragmentTransition.to(notificationsFragment, this, true, R.id.nav_host_fragment_content_main);
            }
        }

        if (data != null && "confirm-registration".equals(data.getHost())) {
            String id = data.getQueryParameter("id");
            if (id != null) {
                Call<AuthResponse> call = ClientUtils.authService.confirmRegistration(Long.valueOf(id));
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("JWT_TOKEN", response.body().getAccessToken());
                            editor.apply();

                            LoggedInHelperService.manageNavigationItems();
                        } else {
                            new MaterialAlertDialogBuilder(getApplicationContext())
                                    .setTitle("An error occured")
                                    .setMessage("An error occured!")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setIcon(R.drawable.icon_error)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        new MaterialAlertDialogBuilder(getApplicationContext())
                                .setTitle("An error occured")
                                .setMessage("An error occured!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.icon_error)
                                .show();
                    }
                });
            }
        }

        if (data != null && "fast-registration".equals(data.getHost())) {
            String encryptedEmail = data.getQueryParameter("value");
            String email;
            try {
                email = EncryptionUtil.decrypt(encryptedEmail);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Pass the decrypted email to the FastRegistrationFragment
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("encryptedEmail", encryptedEmail);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.fast_registration, bundle);
        }

        if (data != null && "homepage".equals(data.getHost())) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
        }

        if (data != null && "event-details".equals(data.getHost())) {
            String eventId = data.getQueryParameter("id");

            if (eventId != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("EventID", Long.parseLong(eventId));

                navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.popBackStack();
                navController.navigate(R.id.nav_event_details, bundle);
            } else {
                navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        String role = LoggedInHelperService.getRole();

        menu.findItem(R.id.action_profile).setVisible(role != null);
        menu.findItem(R.id.action_messages).setVisible(role != null);
        notificationItem = menu.findItem(R.id.action_notifications);
        menu.findItem(R.id.action_notifications).setVisible(role != null);
        menu.findItem(R.id.action_logout).setVisible(role != null);

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        UserNotificationInfoViewModel viewModel = new ViewModelProvider(MainActivity.this).get(UserNotificationInfoViewModel.class);
        UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();

        if (notificationItem != null && currentInfo != null) {
            boolean hasNewNotifications = currentInfo.getHasNewNotifications();
            boolean isMuted = currentInfo.getAreNotificationsMuted();

            notificationItem.setIcon(hasNewNotifications && !isMuted
                    ? R.drawable.icon_notifications_red_dot
                    : R.drawable.icon_notifications);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            if (openedNotifications) {
                updateNotificationsInfo();
                openedNotifications = false;
            }
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.nav_my_profile);
            return true;

        } else if (id == R.id.action_messages) {
            if (openedNotifications) {
                updateNotificationsInfo();
                openedNotifications = false;
            }

            AllChatsFragment allChatsFragmentNew = (AllChatsFragment) getSupportFragmentManager()
                    .findFragmentByTag(AllChatsFragment.class.getSimpleName());
            if (allChatsFragmentNew == null) {
                allChatsFragmentNew = new AllChatsFragment(this);
            }
            this.allChatsFragment = allChatsFragmentNew;

            FragmentTransition.to(allChatsFragmentNew, this, true, R.id.nav_host_fragment_content_main);
            return true;

        } else if (id == R.id.action_notifications) {
            openedNotifications = true;
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            // Check if the fragment already exists using the FragmentManager
            NotificationsFragment notificationsFragmentNew = (NotificationsFragment) getSupportFragmentManager()
                    .findFragmentByTag(NotificationsFragment.class.getSimpleName());

            if (notificationsFragmentNew == null) {
                notificationsFragmentNew = new NotificationsFragment();
            }
            this.notificationsFragment = notificationsFragmentNew;

            FragmentTransition.to(notificationsFragmentNew, this, true, R.id.nav_host_fragment_content_main);
            return true;

        } else if(id == R.id.action_logout) {
            this.logout();
            LoggedInHelperService.manageNavigationItems();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.popBackStack();
            navController.navigate(R.id.nav_home);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void logout() {
        if (chatStompClient != null) {
            chatStompClient.disconnect();
            Log.d("WebScoket", "Disconnected chat on logout");
        }

        if (mStompClient != null) {
            mStompClient.disconnect();
            Log.d("WebSocket", "Disconnected on logout");
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EventyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("JWT_TOKEN");
        editor.apply();
    }

    private void updateNotificationsInfo() {
        Long loggedInUserId = LoggedInHelperService.getId();

        if (loggedInUserId != null) {
            Call<LocalDateTime> call = ClientUtils.userService.updateLastReadNotifications(loggedInUserId);
            call.enqueue(new Callback<LocalDateTime>() {
                @Override
                public void onResponse(Call<LocalDateTime> call, Response<LocalDateTime> response) {
                    if (response.isSuccessful() && response.body() != null && this != null) {
                        LocalDateTime lastRead = response.body();

                        UserNotificationInfoViewModel viewModel = new ViewModelProvider(MainActivity.this).get(UserNotificationInfoViewModel.class);
                        UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();
                        if (currentInfo != null) {
                            UserNotificationInfo updatedInfo = new UserNotificationInfo(
                                    currentInfo.getUserId(),
                                    currentInfo.getAreNotificationsMuted(),
                                    lastRead,  // update lastReadNotifications
                                    false      // set hasNewNotifications to false
                            );
                            viewModel.setNotificationInfo(updatedInfo); // update back to MainActivity
                        }
                    }
                }

                @Override
                public void onFailure(Call<LocalDateTime> call, Throwable t) {
                    Log.wtf("TAMARA ERROR: NotificationInfo", "Can't load UsedNotificationInfo");
                }
            });
        }
    }

    public void connectToMobileWebSocket(String jwtToken) {
        String ip_addr = BuildConfig.IP_ADDR;
        String WEBSOCKET_URL = "ws://" + ip_addr + ":8080/web-notifications";
        String TOKEN = "Bearer " + jwtToken;

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", TOKEN));

        mStompClient.withClientHeartbeat(2000).withServerHeartbeat(2000);
        resetSubscriptions();

        // Manage connection lifecycle
        Disposable dispLifecycle = mStompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {
                    case OPENED:
                        Log.d("WebSocket", "STOMP connection opened");
                        break;
                    case ERROR:
                        Log.e("WebSocket", "STOMP connection error", lifecycleEvent.getException());
                        break;
                    case CLOSED:
                        Log.d("WebSocket", "STOMP connection closed");
                        resetSubscriptions();
                        break;
                    case FAILED_SERVER_HEARTBEAT:
                        Log.w("WebSocket", "STOMP failed server heartbeat");
                        break;
                }
            });

        compositeDisposable.add(dispLifecycle);

        // Subscribe to user-specific mobile notifications
        Long userId = LoggedInHelperService.getId();
        Disposable dispTopic = mStompClient.topic("/topic/mobile/" + userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(topicMessage -> {
                handleNotification(topicMessage.getPayload());
            }, throwable -> {
                Log.e("WebSocket", "Error subscribing to topic", throwable);
            });

        compositeDisposable.add(dispTopic);

        // Connect
        mStompClient.connect(headers);
    }

    public void connectToChatSocket(String jwtToken) {
        String ip_addr = BuildConfig.IP_ADDR;
        String WEBSOCKET_URL = "ws://" + ip_addr + ":8080/chats";
        String TOKEN = "Bearer " + jwtToken;

        chatStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", TOKEN));

        chatStompClient.withClientHeartbeat(2000).withServerHeartbeat(2000);
        resetChatSubscriptions();

        // Manage connection lifecycle
        Disposable dispLifecycle = chatStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("WebSocket", "CHAT STOMP connection opened");
                            break;
                        case ERROR:
                            Log.e("WebSocket", "CHAT STOMP connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d("WebSocket", "CHAT STOMP connection closed");
                            resetChatSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.w("WebSocket", "CHAT STOMP failed server heartbeat");
                            break;
                    }
                });

        chatCompositeDisposable.add(dispLifecycle);

        // Subscribe to user-specific mobile notifications
        Long userId = LoggedInHelperService.getId();
        Disposable dispTopic = chatStompClient.topic("/topic/chat/" + userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    handleNewMessage(topicMessage.getPayload());
                }, throwable -> {
                    Log.e("WebSocket", "Error subscribing to topic", throwable);
                });

        chatCompositeDisposable.add(dispTopic);

        // Connect
        chatStompClient.connect(headers);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void resetChatSubscriptions() {
        if (chatCompositeDisposable != null) {
            chatCompositeDisposable.dispose();
        }
        chatCompositeDisposable = new CompositeDisposable();
    }

    private void handleNotification(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            Notification notification = objectMapper.readValue(message, Notification.class);

            UserNotificationInfoViewModel viewModel = new ViewModelProvider(MainActivity.this).get(UserNotificationInfoViewModel.class);
            UserNotificationInfo currentInfo = viewModel.getNotificationInfo().getValue();
            if (currentInfo != null) {
                UserNotificationInfo updatedInfo = new UserNotificationInfo(
                        currentInfo.getUserId(),
                        currentInfo.getAreNotificationsMuted(),
                        currentInfo.getLastReadNotifications(),
                        true
                );
                viewModel.setNotificationInfo(updatedInfo);

                if (!isAppInForeground() && !currentInfo.getAreNotificationsMuted()) {
                    NotificationHelper.showNotification(this, notification.getTitle(), notification.getMessage());
                }
            }

            if (notificationsFragment != null) {
                notificationsFragment.addNewNotification(notification);
            }

        } catch (Exception e) {
            Log.e("NotificationHandler", "Error deserializing message", e);
        }
    }

    private void handleNewMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            Message newMessage = objectMapper.readValue(message, Message.class);
            if (allChatsFragment != null) {
                allChatsFragment.handleNewMessage(newMessage);
            }

        } catch (Exception e) {
            Log.e("ChatHandler", "Error deserializing message", e);
        }
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();

        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
                if (processInfo.processName.equals(this.getPackageName()) &&
                        processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStompClient != null) {
            mStompClient.disconnect();
            Log.d("WebSocket", "Disconnected on activity destroy");
        }
    }
}
