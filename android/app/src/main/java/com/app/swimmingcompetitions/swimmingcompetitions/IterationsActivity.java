package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IterationsActivity extends AppCompatActivity implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private User currentUser;
    private Competition selectedCompetition;
    private ArrayList<Participant> allParticipants;
    private ArrayList<Participant> currentParticipants;

    private TextView timeView;
    private Button start, reset, endIterationButton;

    private long millisecondTime, startTime, timeBuff, updateTime = 0L ;
    private int seconds, minutes, milliSeconds;
    private Handler handler;

    private GridLayout buttonsLayout;
    private GridLayout participantNamesLayout;
    private GridLayout participantResultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iterations);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

            try {
                this.allParticipants = this.selectedCompetition.getParticipants();
            }
            catch (JSONException e) {
                showToast("IterationsActivity onCreate: Error getting participants");
            }

            this.currentParticipants = this.selectedCompetition.getNewParticipants(this.allParticipants);

            this.buttonsLayout = findViewById(R.id.buttons_layout);
            this.participantNamesLayout = findViewById(R.id.participant_names_layout);
            this.participantResultsLayout = findViewById(R.id.participant_results_layout);

            this.buttonsLayout.post(new Runnable(){
                //layout is ready, set the buttons according to the device width
                public void run(){
                    setParticipantsView();
                }
            });

            this.timeView = findViewById(R.id.time_view);
            this.start = findViewById(R.id.start_time_btn);
            this.reset = findViewById(R.id.reset_btn);
            this.endIterationButton = findViewById(R.id.end_iteration_btn);

            this.handler = new Handler();

            this.start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startClicked(view);
                }
            });
            this.reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetClicked(view);
                }
            });
            this.endIterationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endIterationClicked(view);
                }
            });
        }
    }

    private void endIterationClicked(View view) {

        this.currentParticipants = this.selectedCompetition.getNewParticipants(this.allParticipants);

        if(this.currentParticipants.size() == 0) { //competition is over
            try {
                this.selectedCompetition.setParticipants(this.allParticipants);

                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                jsonAsyncTaskPost.delegate = this;
                JSONObject data = new JSONObject();
                //set up action params

                data.put("urlSuffix", "/setCompetitionResults");
                data.put("httpMethod", "POST");
                data.put("competition", this.selectedCompetition.getJSON_Object().toString());

                jsonAsyncTaskPost.execute(data.toString());
            }
            catch (JSONException e) {
                showToast("IterationsActivity endIterationClicked: Error creating JSONObject");
            }

            //call the server


        }
        else {
            resetClicked(view);
            setParticipantsView();
        }

    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void resetClicked(View view) {
        millisecondTime = 0L ;
        startTime = 0L ;
        timeBuff = 0L ;
        updateTime = 0L ;
        seconds = 0 ;
        minutes = 0 ;
        milliSeconds = 0 ;
        timeView.setText("00:00");
        start.setText("התחל");
        endIterationButton.setEnabled(true);

        int numOfParticipants = this.selectedCompetition.getNumOfParticipants();

        for(int i = 0; i < numOfParticipants; i++){
            Participant participant = allParticipants.get(i);
            participant.setListviewIndex(i);

            TextView resultView = findViewById(participant.getListviewIndex());
            resultView.setText("00:00");
        }
    }

    private void startClicked(View view) {
        String mode = (String) start.getText();
        if(mode.equals("התחל")) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            start.setText("עצור");
            endIterationButton.setEnabled(false);
        }
        else if(mode.equals("עצור")) {
            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            start.setText("המשך");
            endIterationButton.setEnabled(true);
        }
        else if(mode.equals("המשך")) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            start.setText("עצור");
        }
    }

    private void setParticipantsView() {
        this.buttonsLayout.removeAllViews();
        this.participantNamesLayout.removeAllViews();
        this.participantResultsLayout.removeAllViews();

        int totalWidth = buttonsLayout.getWidth();
        int numOfParticipants = this.currentParticipants.size();

        for(int i = 0; i < numOfParticipants; i++){
            Participant participant = this.currentParticipants.get(i);
            participant.setListviewIndex(i);

            TextView nameView = getTextView(participant.getFirstName() + " " + participant.getLastName(), totalWidth / numOfParticipants, 18,  Color.BLACK, Gravity.CENTER);
            nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            this.participantNamesLayout.addView(nameView);

            TextView resultView = getTextView("00:00", totalWidth / numOfParticipants, 18,  Color.BLACK, Gravity.CENTER);
            resultView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            resultView.setId(i);
            this.participantResultsLayout.addView(resultView);
        }

        for(int i = 0; i < numOfParticipants; i++){
            Participant participant = currentParticipants.get(i);

            Button button = new Button(this);
            button.setWidth(totalWidth / numOfParticipants);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            button.setText("עצור");
            button.setTag(participant.getUserId());
            button.setGravity(Gravity.CENTER);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { participantFinishedIteration(view);
                }
            });

            this.buttonsLayout.addView(button);
        }
    }

    private void participantFinishedIteration(View view) {
        Participant selectedParticipant = null;
        for(Participant participant : allParticipants) {
            if(participant.getUserId() == view.getTag()) {
                selectedParticipant = participant;
            }
        }

        selectedParticipant.setCompeted(true);
        Double score = seconds + (60 * minutes) + (0.001 * milliSeconds);
        selectedParticipant.setScore(score.toString());

        TextView resultView = findViewById(selectedParticipant.getListviewIndex());
        String result = "";

        if(minutes > 0 && minutes < 10) {
            result += /*"0" +*/ minutes + ":";
        }
        if(seconds < 10) {
            result += /*"0" + */(seconds + ":" + milliSeconds);
        }
        else {
            result += (seconds + ":" + milliSeconds);
        }
        resultView.setText(result);
    }

    private TextView getTextView(String text, int width, int textSize, int color, int alignment) {
        TextView textView = new TextView(this);
        textView.setWidth(width);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(color);
        textView.setGravity(alignment);
        return textView;
    }

    public Runnable runnable = new Runnable() {
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000);
            timeView.setText("" + minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliSeconds));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void processFinish(String result) {

        JSONObject dataObj = null;
        try {
            JSONObject response = new JSONObject(result);
            dataObj = response.getJSONObject("data");
            switchToViewResultsActivity(dataObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void switchToViewResultsActivity(JSONObject dataObj) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("competitionResults", dataObj.toString());
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
}
