package com.example.eventy.reviews.model;

public class CreateReview {
    private Long graderId;
    private Long solutionId;
    private Long eventId;
    private Integer grade;
    private String comment;

    public CreateReview() {}

    public CreateReview(Long graderId, Long solutionId, Long eventId, Integer grade, String comment) {
        this.graderId = graderId;
        this.solutionId = solutionId;
        this.eventId = eventId;
        this.grade = grade;
        this.comment = comment;
    }

    public Long getGraderId() {
        return graderId;
    }

    public void setGraderId(Long graderId) {
        this.graderId = graderId;
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CreateReviewDTO{" +
                "reviewerId=" + graderId +
                ", solutionId=" + solutionId +
                ", eventId=" + eventId +
                ", grade=" + grade +
                ", comment='" + comment + '\'' +
                '}';
    }
}