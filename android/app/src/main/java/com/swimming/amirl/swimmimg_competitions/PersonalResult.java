package com.swimming.amirl.swimmimg_competitions;


import com.google.firebase.database.IgnoreExtraProperties;
import org.json.JSONObject;
import java.util.Calendar;

@IgnoreExtraProperties
public class PersonalResult {

    private String uid;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String score;
    private String timeStamp;

    public PersonalResult() {}

    public PersonalResult(String uid, String birthDate, String firstName, String lastName, String score, String gender, String timeStamp) {
        this.uid = uid;
        this.birthDate = birthDate;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
        this.timeStamp = timeStamp;
    }

    public PersonalResult(JSONObject data) throws Exception {
        DateUtils dateUtils = new DateUtils();
        this.uid = data.getString("uid");
        this.birthDate =  data.getString("birthDate");
        this.gender =  data.getString("gender");
        this.firstName = data.getString("firstName");
        this.lastName = data.getString("lastName");
        this.score = data.getString("score");
        this.timeStamp = data.getString("timeStamp");
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScore() {
        return (this.score != null && this.score.length() > 4) ? this.score.substring(0, this.score.indexOf('.') + 2) : this.score;
    }

    public void setScore(String score) {
        this.score = score;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Calendar getTimeStamp() throws Exception {
        DateUtils dateUtils = new DateUtils();
        if(this.timeStamp == null || this.timeStamp.isEmpty()) {
            Calendar lastPlaceDate = Calendar.getInstance();
            lastPlaceDate.add(Calendar.DAY_OF_MONTH, -1);
            return lastPlaceDate;
        }
        return dateUtils.strTimeStampToCalendar(this.timeStamp);
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

