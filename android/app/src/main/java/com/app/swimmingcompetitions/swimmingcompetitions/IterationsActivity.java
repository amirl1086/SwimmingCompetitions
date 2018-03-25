package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IterationsActivity extends LoadingDialog implements AsyncResponse {

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
    private LinearLayout mainViewLayout;
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
                if(this.allParticipants == null) {
                    setNoParticipantsMessage();
                }
                else {
                    this.currentParticipants = new ArrayList<>();

                    int numOfParticipantsInIteration = this.selectedCompetition.getNumOfParticipants();
                    int totalNumOfParticipants = this.allParticipants.size();
                    int totalCurrentParticipants = (numOfParticipantsInIteration > totalNumOfParticipants ? totalNumOfParticipants : numOfParticipantsInIteration);

                    for(int i = 0; i < totalCurrentParticipants; i++) {
                        this.currentParticipants.add(this.allParticipants.get(i));
                    }

                    this.handler = new Handler();

                    this.buttonsLayout = findViewById(R.id.buttons_layout);
                    this.participantNamesLayout = findViewById(R.id.participant_names_layout);
                    this.participantResultsLayout = findViewById(R.id.participant_results_layout);
                    this.timeView = findViewById(R.id.time_view);
                    this.start = findViewById(R.id.start_time_btn);
                    this.reset = findViewById(R.id.reset_btn);
                    this.endIterationButton = findViewById(R.id.end_iteration_btn);

                    this.buttonsLayout.post(new Runnable(){
                        //layout is ready, set the buttons according to the device width
                        @Override
                        public void run(){
                            setParticipantsView();
                        }
                    });
                    this.start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startClicked(view);
                        }
                    });
                    this.reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            resetClicked();
                        }
                    });
                    this.endIterationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {endIterationClicked(view);
                        }
                    });
                }
            }
            catch (JSONException e) {
                showToast("IterationsActivity onCreate: Error getting participants");
            }
        }
    }

    private void setNoParticipantsMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("אין משתתפים לתחרות זו!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) {} });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void initIteration(Competition competition) {
        this.selectedCompetition = competition;
        try {
            this.currentParticipants = this.selectedCompetition.getCurrentParticipants();

            resetClicked();
            setParticipantsView();
        }
        catch (JSONException e) {
            showToast("IterationsActivity onCreate: Error getting participants");
        }
    }

    private void endIterationClicked(View view) {

        //this.currentParticipants = this.selectedCompetition.getNewParticipants(this.allParticipants);

        try {
            this.selectedCompetition.setCurrentParticipants(this.currentParticipants);
            this.selectedCompetition.setAllParticipants(this.allParticipants);

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            JSONObject data = new JSONObject();
            //set up action params

            data.put("urlSuffix", "/setCompetitionResults");
            data.put("httpMethod", "POST");
            data.put("competition", this.selectedCompetition.getJSON_Object().toString());

            showProgressDialog("שומר תוצאות...");

            jsonAsyncTaskPost.execute(data.toString());
        }
        catch (JSONException e) {
            showToast("IterationsActivity endIterationClicked: Error creating JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void resetClicked() {
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

        for(int i = 0; i < this.currentParticipants.size(); i++){
            Participant participant = this.currentParticipants.get(i);
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

            TextView nameView = getTextView(participant.getFirstName() + " " + participant.getLastName(), totalWidth / numOfParticipants, 22,  Color.BLACK, Gravity.CENTER);
            this.participantNamesLayout.addView(nameView);

            TextView resultView = getTextView("00:00", totalWidth / numOfParticipants, 22,  Color.BLACK, Gravity.CENTER);
            resultView.setId(i);
            this.participantResultsLayout.addView(resultView);
        }

        for(int i = 0; i < numOfParticipants; i++){
            Participant participant = this.currentParticipants.get(i);

            Button button = new Button(this);
            button.setWidth(totalWidth / numOfParticipants);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            button.setHeight(40);
            button.setText("עצור");
            button.setTag(participant.getUserId());
            button.setGravity(Gravity.CENTER);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { participantFinishedIteration(view); }
            });

            this.buttonsLayout.addView(button);
        }
    }

    private void participantFinishedIteration(View view) {
        Participant selectedParticipant = null;
        for(Participant participant : this.currentParticipants) {
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
        textView.setHeight(100);
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
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");
                if(response.getBoolean("success")) {
                    if(dataObj.get("type").equals("resultsMap")) {
                        dataObj.remove("type");
                        switchToViewResultsActivity(dataObj);
                    }
                    else if(dataObj.get("type").equals("newIteration")){
                        Competition competition = new Competition(dataObj);
                        initIteration(competition);
                    }
                }
                else {
                    showToast("LogInActivity processFinish: Error saving competition results");
                }
            }
            else {
                showToast("LogInActivity processFinish: Error saving competition results");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        hideProgressDialog();
    }

    private void switchToViewResultsActivity(JSONObject dataObj) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("competitionResults", dataObj.toString());
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
}
