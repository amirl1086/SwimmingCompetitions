package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewCompetitionActivity extends LoadingDialog implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private User currentUser = null;
    private String currentCallout;
    private Competition selectedCompetition;
    private Button startCompetitionBtn;
    private JSONObject dataObj;
    private Button registerEditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);

        TextView competitionName = findViewById(R.id.competition_name);
        TextView date = findViewById(R.id.date_of_competition);
        TextView time = findViewById(R.id.time_of_competition);
        TextView distance = findViewById(R.id.distance_of_competition);
        TextView style = findViewById(R.id.style_of_competition);
        TextView ages = findViewById(R.id.ages_range_of_competition);
        TextView participantsForIteration = findViewById(R.id.num_of_participants_for_competition);

        this.registerEditBtn = findViewById(R.id.register_edit_btn);
        this.startCompetitionBtn = findViewById(R.id.start_competition);

        DateUtils dateUtils = new DateUtils();
        Calendar calendar;

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
                ArrayList<Participant> participants = this.selectedCompetition.getParticipants();

                if(intent.hasExtra("newParticipant")) {
                    JSONObject newParticipantJson = new JSONObject(intent.getStringExtra("newParticipant"));
                    Participant newParticipant = new Participant(newParticipantJson.getString("uid"), newParticipantJson);

                    participants.add(newParticipant);
                    this.selectedCompetition.setAllParticipants(participants);
                }

                if(this.currentUser.getType().equals("coach")) {
                    this.registerEditBtn.setText("ערוך תחרות");
                    this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switchToCreateNewCompetitionActivityEditMode();
                        }
                    });
                }
                else if(this.currentUser.getType().equals("student")) {
                    this.startCompetitionBtn.setVisibility(View.INVISIBLE);

                    if(this.selectedCompetition.isCurrentUserRegistered(this.currentUser, participants)) {
                        this.registerEditBtn.setText("בטל רישום לתחרות");

                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelRegistration();
                            }
                        });
                    }
                    else {
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinToCompetition();
                            }
                        });
                    }

                }
            }
            catch (JSONException e) {
                showToast("שגיאה באתחול התחרות, נסה לאתחל את האפליקציה");
            }


            calendar = dateUtils.dateToCalendar(new Date(this.selectedCompetition.getActivityDate()));
            date.setText(dateUtils.getDate(calendar));
            competitionName.setText(selectedCompetition.getName());
            time.setText(dateUtils.getTime(calendar));
            distance.setText(selectedCompetition.getLength());
            style.setText(selectedCompetition.getSwimmingStyle());
            ages.setText(selectedCompetition.getAgesString());
            participantsForIteration.setText(String.valueOf(selectedCompetition.getNumOfParticipants()));
        }
    }

    private void cancelRegistration() {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "cancelRegistration";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("נרשם לתחרות...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    public void switchToRegisterUserToCompetitionActivity(View view) {
        Intent intent = new Intent(this, PreCompetitionRegisterActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void switchToCreateNewCompetitionActivityEditMode() {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("editMode", true);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void switchToIterationsActivity() {
        Intent intent = new Intent(this, IterationsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void initCompetitionForIterations(View view) {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "initCompetitionForIterations";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "GET");
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("מאתחל תחרות למקצים...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    public void joinToCompetition() {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "joinToCompetition";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("firstName", this.currentUser.getFirstName());
            data.put("lastName", this.currentUser.getLastName());
            data.put("birthDate", this.currentUser.getBirthDate());
            data.put("gender", this.currentUser.getGender());
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("נרשם לתחרות...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {
                    if(this.currentCallout.equals("initCompetitionForIterations")) {
                        this.dataObj = response.getJSONObject("data");

                        if (dataObj.get("type").equals("resultsMap")) {
                            this.dataObj.remove("type");
                            showToast("התחרות הנוכחית הסתיימה");
                            startCompetitionBtn.setText("צפה בתוצאות");
                            startCompetitionBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switchToViewResultsActivity();
                                }
                            });
                        }
                        else if (this.dataObj.get("type").equals("newIteration")) {
                            this.dataObj.remove("type");
                            this.selectedCompetition = new Competition(this.dataObj);
                            if (this.selectedCompetition.getParticipants().size() == 0) {
                                showToast("לא קיימים משתתפים לתחרות");
                            }
                            else {
                                switchToIterationsActivity();
                            }
                        }
                    }
                    else if(this.currentCallout.equals("joinToCompetition")) {
                        this.registerEditBtn.setText("בטל רישום");
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelRegistration();
                            }
                        });
                        showToast("נרשמת לתחרות בהצלחה");
                    }
                    else if(this.currentCallout.equals("cancelRegistration")) {
                        this.registerEditBtn.setText("הרשם לתחרות");
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinToCompetition();
                            }
                        });
                        showToast("הרישום הוסר בהצלחה");
                    }
                }
                else {
                    showToast("ViewCompetitionsActivity processFinish: Error loging in");
                }
            }
        }
        catch (JSONException e) {
            showToast("ViewCompetitionsActivity processFinish: Error parsing JSONObject");
        }

        hideProgressDialog();
    }

    private void switchToViewResultsActivity() {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("competitionResults", this.dataObj.toString());
        System.out.println("switchToViewResultsActivity dataObj " + this.dataObj.toString());
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }
}
