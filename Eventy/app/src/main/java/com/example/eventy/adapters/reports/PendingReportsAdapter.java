package com.example.eventy.adapters.reports;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.custom.ReportDetailsDialog;
import com.example.eventy.users.model.Report;
import com.example.eventy.users.reports.PendingReportsFragment;
import com.example.eventy.utils.ClientUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReportsAdapter extends RecyclerView.Adapter<PendingReportsAdapter.NotificationViewHolder> {
    private ArrayList<Report> pendingReports;
    private LayoutInflater layoutInflater;
    private PendingReportsFragment pendingReportsFragment;

    public PendingReportsAdapter(Context context, ArrayList<Report> pendingReports, PendingReportsFragment pendingReportsFragment) {
        this.pendingReports = pendingReports;
        this.layoutInflater = LayoutInflater.from(context);
        this.pendingReportsFragment = pendingReportsFragment;
    }

    @NonNull
    @Override
    public PendingReportsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = layoutInflater.inflate(R.layout.fragment_pending_report, parent, false);

        return new PendingReportsAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Report report = pendingReports.get(position);

        if (report != null) {
            holder.reason.setText(report.getReason());

            holder.seeDetails.setOnClickListener(view -> {
                ReportDetailsDialog reportDetailsDialog = new ReportDetailsDialog(
                    (Activity) holder.itemView.getContext(),
                    "REPORT DETAILS",
                    report);
                reportDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportDetailsDialog.show();
            });

            holder.acceptReport.setOnClickListener(view -> {
                Call<Report> call = ClientUtils.reportService.acceptReport(report.getId());
                call.enqueue(new Callback<Report>() {
                    @Override
                    public void onResponse(Call<Report> call, Response<Report> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            pendingReportsFragment.fetchPendingReports(0, pendingReportsFragment.getPageSize());
                        }
                    }

                    @Override
                    public void onFailure(Call<Report> call, Throwable t) {}
                });
            });

            holder.declineReport.setOnClickListener(view -> {
                Call<Report> call = ClientUtils.reportService.declineReport(report.getId());
                call.enqueue(new Callback<Report>() {
                    @Override
                    public void onResponse(Call<Report> call, Response<Report> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            pendingReportsFragment.fetchPendingReports(0, pendingReportsFragment.getPageSize());
                        }
                    }

                    @Override
                    public void onFailure(Call<Report> call, Throwable t) {}
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return pendingReports.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView reason;
        AppCompatButton seeDetails, acceptReport, declineReport;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            reason = itemView.findViewById(R.id.reason_value);
            seeDetails = itemView.findViewById(R.id.see_report_details);
            acceptReport = itemView.findViewById(R.id.accept_report_button);
            declineReport = itemView.findViewById(R.id.decline_report_button);
        }
    }
}