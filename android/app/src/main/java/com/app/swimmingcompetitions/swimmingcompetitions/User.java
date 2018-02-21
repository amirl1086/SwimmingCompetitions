package com.app.swimmingcompetitions.swimmingcompetitions;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String email;
    private String gender;
    private String type;

    public User(String uid, String firstName, String lastName, String birthDate, String email, String gender, String type) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.gender = gender;
        this.type = type;
    }

    public User(JSONObject userJson) throws JSONException{
        this.uid = userJson.getString("uid");
        this.firstName = userJson.getString("firstName");
        this.lastName = userJson.getString("lastName");
        this.birthDate = userJson.getString("birthDate");
        this.email = userJson.getString("email");
        this.gender = userJson.getString("gender");
        this.type = userJson.getString("type");
    }

    public JSONObject getJSON_Object() {
        try {
            JSONObject data = new JSONObject();
            data.put("uid", this.getUid());
            data.put("firstName", this.getFirstName());
            data.put("lastName", this.getLastName());
            data.put("birthDate", this.getBirthDate());
            data.put("email", this.getEmail());
            data.put("gender", this.getGender());
            data.put("type", this.getType());
            return data;
        } catch (JSONException e) {
            return null;
        }
    }


    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getType() {
        return type;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setType(String type) {
        this.type = type;
    }
}
