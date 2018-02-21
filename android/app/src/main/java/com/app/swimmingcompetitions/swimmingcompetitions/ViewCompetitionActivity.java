package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
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

public class ViewCompetitionActivity extends AppCompatActivity implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private User currentUser = null;
    private Competition selectedCompetition;

    private Button registerEditBtn;
    private Button registerTempUserBtn;
    private Button startCompetitionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);

        TextView competitionName;
        TextView date;
        TextView time;
        TextView distance;
        TextView style;
        TextView ages;
        TextView participantsForIteration;
        Calendar calendar;
        DateUtils dateUtils = new DateUtils();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

            if(intent.hasExtra("newParticipant")) {
                try {
                    JSONObject newParticipantJson = new JSONObject(intent.getStringExtra("newParticipant"));
                    Participant newParticipant = new Participant(newParticipantJson.getString("id"), newParticipantJson);
                    ArrayList<Participant> participants = this.selectedCompetition.getParticipants();
                    participants.add(newParticipant);
                    this.selectedCompetition.setParticipants(participants);
                }
                catch (JSONException e) {
                    showToast("ViewCompetitionActivity onCreate: Error adding new participant");
                }

            }

            this.registerEditBtn = findViewById(R.id.register_edit_btn);
            this.registerTempUserBtn = findViewById(R.id.register_temporary_user_btn);
            this.startCompetitionBtn = findViewById(R.id.start_competition);

            if(this.currentUser.getType().equals("coach")) {
                this.registerEditBtn.setText("ערוך תחרות");
                this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchToCreateNewCompetitionActivityEditMode();
                    }
                });
            }
            else {
                this.startCompetitionBtn.setVisibility(View.INVISIBLE);

                this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerUserToCompetition();
                    }
                });
            }

            competitionName = findViewById(R.id.competition_name);
            date = findViewById(R.id.date_of_competition);
            time = findViewById(R.id.time_of_competition);
            distance = findViewById(R.id.distance_of_competition);
            style = findViewById(R.id.style_of_competition);
            ages = findViewById(R.id.ages_range_of_competition);
            participantsForIteration = findViewById(R.id.num_of_participants_for_competition);
            calendar = dateUtils.dateToCalendar(this.selectedCompetition.getActivityDate());

            date.setText(dateUtils.getHebrewDate(calendar));
            competitionName.setText(selectedCompetition.getName());
            time.setText(dateUtils.getTime(calendar));
            distance.setText(selectedCompetition.getLength());
            style.setText(selectedCompetition.getSwimmingStyle());
            ages.setText(selectedCompetition.getAgesString());
            participantsForIteration.setText(String.valueOf(selectedCompetition.getNumOfParticipants()));
        }
    }

    public void registerTempUserToCompetition(View view) {
        Intent intent = new Intent(this, RegisterTempUserActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void registerUserToCompetition() {
        try {
            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;

            JSONObject data = new JSONObject();
            data.put("urlSuffix", "/joinToCompetition");
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());

            jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            showToast("ViewCompetitionActivity registerUserToCompetition: Error calling joinToCompetition");
        }
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

    public void switchToIterationsActivity(final View view) {
        Intent intent = new Intent(this, IterationsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject dataObj = response.getJSONObject("data");
            if (response.getBoolean("success")) {
                //switchToMainMenuActivity(dataObj);
            } else {
                showToast("LogInActivity processFinish: Error registering");
            }
        } catch (JSONException e) {
            showToast("RegisterActivity, processFinish: Error parsing JSONObject");
        }
    }
}
