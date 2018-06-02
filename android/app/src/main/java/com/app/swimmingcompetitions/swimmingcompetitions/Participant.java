package com.app.swimmingcompetitions.swimmingcompetitions;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

class Participant implements Serializable {
    private String uid;
    private int listviewIndex;
    private String firstName;
    private String gender;
    private String lastName;
    private String birthDate;
    private String score;

    private String competed;

    public Participant(String uid, String firstName, String lastName, String gender, String birthDate, String score, String competed) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.score = score;
        this.competed = competed;
    }

    public Participant(String uid, JSONObject data) throws JSONException {
        this.uid = uid;
        if(data.has("firstName")) {
            this.firstName = data.getString("firstName");
        }
        if(data.has("lastName")) {
            this.lastName = data.getString("lastName");
        }
        if(data.has("birthDate")) {
            this.birthDate = data.getString("birthDate");
        }
        if(data.has("gender")) {
            this.gender = data.getString("gender");
        }
        if(data.has("score")) {
            this.score = data.getString("score");
        }
        if(data.has("competed")) {
            this.competed = data.getString("competed");
        }
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public Participant(JSONObject data) throws JSONException {
        this(data.getString("uid"), data);
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getScore() {
        return (this.score != null && this.score.length() > 4) ? this.score.substring(0, 3) : this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String isCompeted() {
        return this.competed;
    }

    public void setCompeted(String competed) {
        this.competed = competed;
    }

    public void setListviewIndex(int i) {
        this.listviewIndex = i;
    }

    public int getListviewIndex() {
        return this.listviewIndex;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public JSONObject getJSON_Object() throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("uid", this.uid);
        jsonObject.put("firstName", this.firstName);
        jsonObject.put("lastName", this.lastName);
        jsonObject.put("gender", this.gender);
        jsonObject.put("birthDate", this.birthDate);
        jsonObject.put("score", this.score);
        jsonObject.put("competed", this.competed);

        return jsonObject;
    }
}
