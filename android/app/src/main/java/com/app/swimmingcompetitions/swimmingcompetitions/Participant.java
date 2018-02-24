package com.app.swimmingcompetitions.swimmingcompetitions;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

class Participant implements Serializable {
    private String userId;
    private int listviewIndex;
    private String firstName;
    private String gender;
    private String lastName;
    private Date birthDate;
    private String score;

    private Boolean competed;

    public Participant(String userId, String firstName, String lastName, String gender, String birthDate, String score, String competed) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = new Date(birthDate);
        this.score = score;
        this.competed = Boolean.valueOf(competed);
    }

    public Participant(String userId, JSONObject data) throws JSONException {
        this.userId = userId;
        if(data.has("firstName")) {
            this.firstName = data.getString("firstName");
        }
        if(data.has("lastName")) {
            this.lastName = data.getString("lastName");
        }
        if(data.has("birthDate")) {
            this.birthDate = new Date(data.getString("birthDate"));
        }
        if(data.has("gender")) {
            this.gender = data.getString("gender");
        }
        this.score = "0.0";
        this.competed = false;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public Participant(JSONObject data) throws JSONException {
        String userId = data.getString("userId");
        new Participant(userId, data);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
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

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getCompeted() {
        return competed;
    }

    public JSONObject getJSON_Object() throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", this.userId);
        jsonObject.put("firstName", this.firstName);
        jsonObject.put("lastName", this.lastName);
        jsonObject.put("gender", this.gender);
        jsonObject.put("birthDate", this.birthDate);
        jsonObject.put("score", this.score);
        jsonObject.put("competed", this.competed);

        return jsonObject;
    }
}
