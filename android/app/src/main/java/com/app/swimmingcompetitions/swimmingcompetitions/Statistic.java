package com.app.swimmingcompetitions.swimmingcompetitions;

import org.json.JSONObject;

public class Statistic {
    private int score;

    private Competition competition;
    public Statistic(int score, JSONObject competition) throws Exception {
        this.score = score;
        this.competition = new Competition(competition);
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
