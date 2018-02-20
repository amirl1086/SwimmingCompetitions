package com.app.swimmingcompetitions.swimmingcompetitions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Competition implements Serializable {

    private String id;
    private String name;
    private String swimmingStyle;
    private String participants;
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

        if(data.has("participants")) {
            this.participants = data.getString("participants");
        }
    }

    public ArrayList<Participant> getParticipants() throws JSONException {
        JSONObject dataObj = new JSONObject(this.participants);
        Iterator<String> participantIds = dataObj.keys();
        ArrayList<Participant> participants = new ArrayList<>();

        while (participantIds.hasNext()) {
            String currentId = participantIds.next();
            JSONObject participantJson = new JSONObject(dataObj.get(currentId).toString());
            Participant participant = new Participant(currentId, participantJson);
            participants.add(participant);
        }
        return participants;
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

    public ArrayList<Participant> getNewParticipants(ArrayList<Participant> participants) throws JSONException{
        ArrayList<Participant> newParticipants = new ArrayList<>();

        for(Participant participant : participants) {
            if(!participant.isCompeted()) {
                newParticipants.add(participant);
            }
        }

        return newParticipants;
    }

    public void setParticipants(ArrayList<Participant> participants) throws JSONException{
        JSONObject participantsMap = new JSONObject();
        for(Participant participant : participants) {
            JSONObject participantJson = new JSONObject(participant.toString());
            participantsMap.put(participant.getId(), participantJson);
        }
        this.participants = participantsMap.toString();
    }





    /* GETTERS AND SETTERS */
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

    public int getNumOfParticipants() {
        return Integer.valueOf(this.numOfParticipants);
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
