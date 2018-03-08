package com.app.swimmingcompetitions.swimmingcompetitions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PersonalResult {

    private String userId;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String score;
    private String rank;

    public PersonalResult(String userId, String birthDate, String firstName, String lastName, String score, String rank) {
        this.userId = userId;
        this.birthDate = new Date(birthDate);
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
        this.rank = rank;
    }

    public PersonalResult(JSONObject data) throws JSONException {
        this.userId = data.getString("userId");
        this.birthDate =  new Date(data.getString("birthDate"));
        this.firstName = data.getString("firstName");
        this.lastName = data.getString("lastName");
        this.score = data.getString("score");
        this.rank = data.getString("rank");
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
