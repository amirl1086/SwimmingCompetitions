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
    private Integer numOfParticipants;
    private Integer length;


    public Competition(String id, String name, String activityDate, String swimmingStyle, Integer numOfParticipants, Integer length) {
        this.id = id;
        this.name = name;
        this.activityDate = new Date(activityDate);
        this.numOfParticipants = numOfParticipants;
        this.swimmingStyle = swimmingStyle;
        this.length = length;
    }

    public JSONObject getJSON_Object() {
        try {
            JSONObject data = new JSONObject();
            data.put("id", this.getId());
            data.put("name", this.getName());
            data.put("swimmingStyle", this.getSwimmingStyle());
            data.put("activityDate", this.getActivityDate().toString());
            data.put("numOfParticipantsInIteration", this.getNumOfParticipants());
            data.put("length", this.getLength());
            return data;
        } catch (JSONException e) {
            return null;
        }
    }

    public String getSwimmingStyle() {
        return swimmingStyle;
    }

    public void setSwimmingStyle(String swimmingStyle) {
        this.swimmingStyle = swimmingStyle;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public Integer getNumOfParticipants() {
        return numOfParticipants;
    }

    public Integer getLength() {
        return length;
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
        this.numOfParticipants = numOfParticipants;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
