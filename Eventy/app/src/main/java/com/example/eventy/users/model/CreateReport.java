package com.example.eventy.users.model;

public class CreateReport {
    private String reason;
    private Long senderUserId;
    private Long reportedUserId;

    public CreateReport() {}

    public CreateReport(String reason, Long senderUserId, Long reportedUserId) {
        this.reason = reason;
        this.senderUserId = senderUserId;
        this.reportedUserId = reportedUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }
}
