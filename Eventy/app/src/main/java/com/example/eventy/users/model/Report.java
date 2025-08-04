package com.example.eventy.users.model;

public class Report {
    private Long id;
    private String reason;
    private String senderEmail;
    private String reportedUserEmail;

    public Report() {
    }

    public Report(Long id, String reason, String senderEmail, String reportedUserEmail) {
        this.id = id;
        this.reason = reason;
        this.senderEmail = senderEmail;
        this.reportedUserEmail = reportedUserEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReportedUserEmail() {
        return reportedUserEmail;
    }

    public void setReportedUserEmail(String reportedUserEmail) {
        this.reportedUserEmail = reportedUserEmail;
    }
}
