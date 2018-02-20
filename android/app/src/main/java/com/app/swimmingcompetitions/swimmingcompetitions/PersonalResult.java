package com.app.swimmingcompetitions.swimmingcompetitions;

public class PersonalResult {

    private String userId;
    private String competitionId;
    private double score;
    private int rank;
    public PersonalResult(String competitionId, String userId, double score, int rank) {
        this.competitionId = competitionId;
        this.userId = userId;
        this.score = score;
        this.rank = rank;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

}
