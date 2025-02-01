package com.example.eventy.events.model;

public class EventStats {
    private EventCard eventCard;
    private int visitors;
    private double averageGrade;
    private int[] gradeDistribution;

    public EventStats(EventCard eventCard, int visitors, double averageGrade, int[] gradeDistribution) {
        this.eventCard = eventCard;
        this.visitors = visitors;
        this.averageGrade = averageGrade;
        this.gradeDistribution = gradeDistribution;
    }

    public EventCard getEventCard() {
        return eventCard;
    }

    public void setEventCard(EventCard eventCard) {
        this.eventCard = eventCard;
    }

    public int getVisitors() {
        return visitors;
    }

    public void setVisitors(int visitors) {
        this.visitors = visitors;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public int[] getGradeDistribution() {
        return gradeDistribution;
    }

    public void setGradeDistribution(int[] gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }
}

