package com.app.swimmingcompetitions.swimmingcompetitions;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

class Participant implements Serializable {
    private String id;
    private int listviewIndex;
    private String firstName;
    private String gender;
    private String lastName;
    private Date birthDate;
    private Double score;

    private Boolean competed;

    public Participant(String id, String firstName, String lastName, String gender, String birthDate, String score, String competed) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = new Date(birthDate);
        this.score = Double.valueOf(score);
        this.competed = Boolean.valueOf(competed);
    }

    public Participant(String id, JSONObject data) throws JSONException {
        this.id = id;
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
        this.score = 0.0;
        this.competed = false;
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

        jsonObject.put("id", this.id);
        jsonObject.put("firstName", this.firstName);
        jsonObject.put("lastName", this.lastName);
        jsonObject.put("gender", this.gender);
        jsonObject.put("birthDate", this.birthDate);
        jsonObject.put("score", this.score);
        jsonObject.put("competed", this.competed);

        return jsonObject;
    }
}
