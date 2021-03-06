package com.swimming.amirl.swimmimg_competitions;

import org.json.JSONObject;

public class Statistic {
    private String score;
    private Competition competition;

    public Statistic(String score, JSONObject competition) throws Exception {
        this.score = score;
        this.competition = new Competition(competition);
    }

    public String getScore() {
        return this.score.indexOf('.') >= 0 ? this.score.substring(0, this.score.indexOf('.')) : this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}

