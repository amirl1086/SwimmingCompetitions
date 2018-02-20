package com.app.swimmingcompetitions.swimmingcompetitions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

class Participant {
    private String id;
    private int listviewIndex;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Double score;
    private Boolean competed;

    public Participant(String id, String firstName, String lastName, String birthDate, String score, String competed) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = new Date(birthDate);
        this.score = Double.valueOf(score);
        this.competed = Boolean.valueOf(competed);
    }

    public Participant(String id, JSONObject data) throws JSONException {
        this.id = id;
        this.firstName = data.getString("firstName");
        this.lastName = data.getString("lastName");
        this.birthDate = new Date(data.getString("birthDate"));
        this.score = Double.valueOf(data.getString("score"));
        this.competed = Boolean.valueOf(data.getString("competed"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
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

    public Double getScore() {
        return this.score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean isCompeted() {
        return this.competed;
    }

    public void setCompeted(Boolean competed) {
        this.competed = competed;
    }

    public void setListviewIndex(int i) {
        this.listviewIndex = i;
    }

    public int getListviewIndex() {
        return this.listviewIndex;
    }
}
