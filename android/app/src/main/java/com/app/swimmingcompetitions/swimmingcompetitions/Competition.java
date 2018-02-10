package com.app.swimmingcompetitions.swimmingcompetitions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Competition implements Serializable {

    private String id;
    private String name;
    private String swimmingStyle;
    private Date activityDate;
    private String numOfParticipants;
    private String fromAge;
    private String toAge;
    private String length;


    public Competition(String id, String name, String activityDate, String swimmingStyle, Integer numOfParticipants, Integer fromAge, Integer toAge, Integer length) {
        this.id = id;
        this.name = name;
        this.activityDate = new Date(activityDate);
        this.numOfParticipants = String.valueOf(numOfParticipants);
        this.swimmingStyle = swimmingStyle;
        this.length = String.valueOf(length);
        this.fromAge = String.valueOf(fromAge);
        this.toAge = String.valueOf(toAge);
    }

    public Competition(String id, JSONObject data) throws JSONException {
        this.id = id;
        this.name = data.getString("name");
        String activityDate = data.getString("activityDate");
        this.activityDate = new Date(activityDate);
        this.numOfParticipants = data.getString("numOfParticipants");
        this.swimmingStyle = data.getString("swimmingStyle");
        this.length = data.getString("length");
        this.fromAge = data.getString("fromAge");
        this.toAge = data.getString("toAge");
    }


    public JSONObject getJSON_Object() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("id", this.getId());
        data.put("name", this.getName());
        data.put("swimmingStyle", this.getSwimmingStyle());
        data.put("activityDate", this.getActivityDate().toString());
        data.put("numOfParticipants", this.getNumOfParticipants());
        data.put("length", this.getLength());
        data.put("fromAge", this.getFromAge());
        data.put("toAge", this.getToAge());
        return data;
    }

    public String getSwimmingStyle() {
        return swimmingStyle;
    }

    public void setSwimmingStyle(String swimmingStyle) {
        this.swimmingStyle = swimmingStyle;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Date getActivityDate() {
        return this.activityDate;
    }

    public String getNumOfParticipants() {
        return this.numOfParticipants;
    }

    public String getLength() {
        return this.length;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public void setNumOfParticipants(Integer numOfParticipants) {
        this.numOfParticipants = String.valueOf(numOfParticipants);
    }

    public void setLength(Integer length) {
        this.length =  String.valueOf(length);
    }

    public String getAgesString() {
        return this.fromAge.toString() + " - " + this.toAge.toString();
    }

    public String getFromAge() {
        return this.fromAge;
    }

    public String getToAge() {
        return this.toAge;
    }
}
