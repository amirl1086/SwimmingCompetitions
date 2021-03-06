package com.swimming.amirl.swimmimg_competitions;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Competition implements Serializable {

    private String id;
    private String name;
    private String swimmingStyle;
    private String participants;
    private String currentParticipants;
    private Boolean isDone;
    private String activityDate;
    private String numOfParticipants;
    private String fromAge;
    private String toAge;
    private String length;


    public Competition(String id, String name, String activityDate, String swimmingStyle, Integer numOfParticipants, Integer fromAge, Integer toAge, Integer length) {
        this.id = id;
        this.name = name;
        this.activityDate = activityDate;
        this.numOfParticipants = String.valueOf(numOfParticipants);
        this.swimmingStyle = swimmingStyle;
        this.length = String.valueOf(length);
        this.fromAge = String.valueOf(fromAge);
        this.toAge = String.valueOf(toAge);
    }

    public Competition(String id, JSONObject data) throws JSONException {
        this.id = id;
        this.name = data.getString("name");
        this.activityDate = data.getString("activityDate");
        this.numOfParticipants = data.getString("numOfParticipants");
        this.swimmingStyle = data.getString("swimmingStyle");
        this.length = data.getString("length");
        this.fromAge = data.getString("fromAge");
        this.toAge = data.getString("toAge");

        if (data.has("participants")) {
            this.participants = data.getString("participants");
        }

        if (data.has("currentParticipants")) {
            this.currentParticipants = data.getString("currentParticipants");
        }
    }

    public Competition(JSONObject data) throws JSONException {
        this(data.getString("id"), data);
    }

    public JSONObject getJSON_Object() throws JSONException {
        JSONObject data = new JSONObject();

        data.put("id", this.id);
        data.put("name", this.name);
        data.put("participants", this.participants);
        data.put("currentParticipants", this.currentParticipants);
        data.put("swimmingStyle", this.swimmingStyle);
        data.put("activityDate", this.activityDate);
        data.put("numOfParticipants", this.numOfParticipants);
        data.put("length", this.length);
        data.put("fromAge", this.fromAge);
        data.put("toAge", this.toAge);

        return data;
    }

    public ArrayList<Participant> getParticipants() throws JSONException {
        ArrayList<Participant> participants = new ArrayList<>();
        if (this.participants != null) {
            participants = getParticipants(new JSONObject(this.participants));
        }
        return participants;
    }

    public ArrayList<Participant> getParticipants(JSONObject dataObj) throws JSONException {
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

    public ArrayList<Participant> getCurrentParticipants() throws JSONException {
        ArrayList<Participant> participants = new ArrayList<>();
        if (this.currentParticipants != null) {
            participants = getParticipants(new JSONObject(this.currentParticipants));
        }
        return participants;
    }

    public void setCurrentParticipants(ArrayList<Participant> participants) throws JSONException {
        this.currentParticipants = getParticipantsStr(participants);
    }

    public void setAllParticipants(ArrayList<Participant> participants) throws JSONException {
        this.participants = getParticipantsStr(participants);
    }

    public String getParticipantsStr(ArrayList<Participant> participants) throws JSONException {
        JSONObject participantsMap = new JSONObject();
        for (Participant participant : participants) {
            JSONObject participantJson = participant.getJSON_Object();
            participantsMap.put(participant.getUid(), participantJson);
        }
        return participantsMap.toString();
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

    public String getActivityDate() {
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

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public void setNumOfParticipants(Integer numOfParticipants) {
        this.numOfParticipants = String.valueOf(numOfParticipants);
    }

    public void setLength(Integer length) {
        this.length = String.valueOf(length);
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


    public boolean isCurrentUserRegistered(User currentUser, ArrayList<Participant> participants) {
        for (int i = 0; i < participants.size(); i++) {
            if (currentUser.getUid().equals(participants.get(i).getUid())) {
                return true;
            }
        }
        return false;
    }
}