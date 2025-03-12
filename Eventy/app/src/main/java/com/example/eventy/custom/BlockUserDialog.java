package com.example.eventy.custom;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventy.R;
import com.example.eventy.users.model.BlockUser;
import com.example.eventy.utils.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUserDialog extends Dialog implements View.OnClickListener {
    public AppCompatButton closeButton;
    private String title;
    private String message;
    private BlockUser blockUser;
    Activity activity;

    public BlockUserDialog(Activity a, String title, String message, BlockUser blockUser) {
        super(a);
        this.activity = a;
        this.title = title;
        this.message = message;
        this.blockUser = blockUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_block_user);
        closeButton = (AppCompatButton) findViewById(R.id.confirm_button);
        closeButton.setOnClickListener(this);

        setupDialogDetails();
    }

    private void setupDialogDetails() {
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        TextView messageTextView = findViewById(R.id.message);
        messageTextView.setText(message);
    }

    @Override
    public void onClick(View v) {
        Call<BlockUser> call = ClientUtils.userService.blockUser(this.blockUser);
        call.enqueue(new Callback<BlockUser>() {
            @Override
            public void onResponse(Call<BlockUser> call, Response<BlockUser> response) {
                if (response.isSuccessful()) {
                    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                    navController.popBackStack();
                    navController.navigate(R.id.nav_home);
                    dismiss();

                } else {
                    dismiss();
                    showErrorDialog("Error while blocking an user!");
                }
            }

            @Override
            public void onFailure(Call<BlockUser> call, Throwable t) {
                Log.wtf("OVDEEEE", t.getMessage());
                dismiss();
                showErrorDialog("Error while blocking an user!");
            }
        });
    }

    private void showErrorDialog(String message) {
        Activity activity = getOwnerActivity();
        if (activity != null && !activity.isFinishing()) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(activity, "Error", message);
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        }
    }
}
