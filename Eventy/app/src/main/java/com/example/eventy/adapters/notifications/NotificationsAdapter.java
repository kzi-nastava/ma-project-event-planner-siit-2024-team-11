package com.example.eventy.adapters.notifications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.interactions.model.Notification;
import com.example.eventy.interactions.model.NotificationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private ArrayList<Notification> notifications;
    private LayoutInflater layoutInflater;

    public NotificationsAdapter(Context context, ArrayList<Notification> notifications) {
        this.notifications = notifications;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NotificationsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_notification_card, parent, false);

        return new NotificationsAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        if (notification != null) {
            if (notification.getType().equals(NotificationType.EVENT_CHANGE)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_eventy_blue);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("EventID", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_event_details, args);
                });

            } else if (notification.getType().equals(NotificationType.RATING_EVENT)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_eventy_blue);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("EventID", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_event_details, args);
                });

            } else if (notification.getType().equals(NotificationType.RATING_SERVICE)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_service_yellow);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("solutionId", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_solution_details, args);
                });

            } else if (notification.getType().equals(NotificationType.RATING_PRODUCT)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_product_pink);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("solutionId", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_solution_details, args);
                });

            } else if (notification.getType().equals(NotificationType.CATEGORY_UPDATED)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_brown);
                holder.notificationCard.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_my_profile);
                });

            } else if (notification.getType().equals(NotificationType.NEW_CATEGORY_SUGGESTION)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_dark_yellow);
                holder.notificationCard.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_category_home);
                });

            } else if (notification.getType().equals(NotificationType.CATEGORY_SUGGESTION_ACCEPTED)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_green);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("solutionId", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_solution_details, args);
                });

            } else if (notification.getType().equals(NotificationType.CATEGORY_SUGGESTION_CHANGED)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_dark_orange_red);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("solutionId", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_solution_details, args);
                });

            } else if (notification.getType().equals(NotificationType.CATEGORY_SUGGESTION_REPLACED)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_red);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("solutionId", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_solution_details, args);
                });

            } else if (notification.getType().equals(NotificationType.REMINDER_SERVICE)) {
                holder.leftSide.setBackgroundResource(R.drawable.notification_type_blue_teal);
                holder.notificationCard.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("EventID", notification.getRedirectionId());

                    NavController navController = Navigation.findNavController(v);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_event_details, args);
                });
            }

            holder.title.setText("\"" + notification.getTitle() + "\"");
            holder.message.setText(notification.getMessage());

            if (notification.getGrade() != null) {
                holder.reviewContainer.setVisibility(View.VISIBLE);
                holder.graderText.setText("By: @" + notification.getGraderEmail());
                Drawable picture = PictureHelperService.getPicture(notification.getGraderImage(), layoutInflater.getContext());
                if (picture != null) {
                    holder.graderImage.setBackground(picture);
                }

                holder.star1.setBackgroundResource(R.drawable.icon_star_gold);
                if (notification.getGrade() == 1) {
                    holder.star2.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star3.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star4.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star5.setBackgroundResource(R.drawable.icon_star_grey);
                } else if (notification.getGrade() == 2) {
                    holder.star2.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star3.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star4.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star5.setBackgroundResource(R.drawable.icon_star_grey);
                } else if (notification.getGrade() == 3) {
                    holder.star2.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star3.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star4.setBackgroundResource(R.drawable.icon_star_grey);
                    holder.star5.setBackgroundResource(R.drawable.icon_star_grey);
                } else if (notification.getGrade() == 4) {
                    holder.star2.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star3.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star4.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star5.setBackgroundResource(R.drawable.icon_star_grey);
                } else if (notification.getGrade() == 5) {
                    holder.star2.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star3.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star4.setBackgroundResource(R.drawable.icon_star_gold);
                    holder.star5.setBackgroundResource(R.drawable.icon_star_gold);
                }
            } else {
                holder.reviewContainer.setVisibility(View.GONE);
            }

            LocalDateTime dateTime = notification.getTimestamp();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm'h'");
            String formattedDate = dateTime.format(formatter);
            holder.dateText.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        LinearLayout notificationCard, reviewContainer;
        TextView title, graderText, message, dateText;
        AppCompatButton star1, star2, star3, star4, star5;
        ConstraintLayout leftSide;
        ImageView graderImage;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationCard = itemView.findViewById(R.id.notification_card);
            reviewContainer = itemView.findViewById(R.id.review_container);
            title = itemView.findViewById(R.id.notification_title);
            graderText = itemView.findViewById(R.id.grader_text);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
            message = itemView.findViewById(R.id.notification_message);
            dateText = itemView.findViewById(R.id.date_text);
            leftSide = itemView.findViewById(R.id.left_side);
            graderImage = itemView.findViewById(R.id.grader_picture);
        }
    }
}