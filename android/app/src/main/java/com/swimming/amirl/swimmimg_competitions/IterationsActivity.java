package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class IterationsActivity extends LoadingDialog implements HttpAsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

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
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            try {
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
                this.allParticipants = this.selectedCompetition.getParticipants();
                this.currentParticipants = this.selectedCompetition.getCurrentParticipants();
            }
            catch (Exception e) {
                showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace: " + Arrays.toString(e.getStackTrace()));
            }

            bindView();
        }
        else {
            switchToLogInActivity();
        }
    }

    private void bindView() {
        this.handler = new Handler();

        this.buttonsLayout = findViewById(R.id.buttons_layout);
        this.participantNamesLayout = findViewById(R.id.participant_names_layout);
        this.participantResultsLayout = findViewById(R.id.participant_results_layout);
        this.timeView = findViewById(R.id.time_view);
        this.start = findViewById(R.id.start_time_btn);
        this.reset = findViewById(R.id.reset_btn);
        this.endIterationButton = findViewById(R.id.end_iteration_btn);

        this.buttonsLayout.post(new Runnable(){
            @Override public void run(){ setParticipantsView(); }
        });
        this.start.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { startClicked(); }
        });
        this.reset.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { resetClicked(); }
        });
        this.endIterationButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { endIterationClicked(view); }
        });
    }


    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void initIteration() throws Exception {
        this.currentParticipants = this.selectedCompetition.getCurrentParticipants();
        this.allParticipants = this.selectedCompetition.getParticipants();

        setParticipantsView();
        resetClicked();
    }

    private void endIterationClicked(View view) {
        if(isValid()) {
            try {
                resetTimer();
                this.selectedCompetition.setCurrentParticipants(this.currentParticipants);
                this.selectedCompetition.setAllParticipants(this.allParticipants);

                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;

                JSONObject data = new JSONObject();
                data.put("urlSuffix", "/setCompetitionResults");
                data.put("httpMethod", "POST");
                data.put("competition", this.selectedCompetition.getJSON_Object().toString());

                showProgressDialog("שומר תוצאות...");

                this.jsonAsyncTaskPost.execute(data.toString());
            }
            catch (Exception e) {
                hideProgressDialog();
                showToast("שגיאה ביצירת הבקשה למערכת, נסה שוב");
                System.out.println("ViewCompetitionActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private boolean isValid() {
        for(int i = 0; i < this.currentParticipants.size(); i++) {
            if(this.currentParticipants.get(i).getScore() == null) {
                showToast("לא סימנת תוצאה עבור כל המתמודדים");
                return false;
            }
        }
        return true;
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void resetClicked() {
        resetTimer();

        for(int i = 0; i < this.currentParticipants.size(); i++){
            Participant participant = this.currentParticipants.get(i);
            participant.setListviewIndex(i);

            TextView resultView = findViewById(participant.getListviewIndex());
            resultView.setText("00:00");
        }
    }

    private void resetTimer() {
        this.millisecondTime = 0L;
        this.startTime = 0L;
        this.timeBuff = 0L;
        this.updateTime = 0L;
        this.seconds = 0;
        this.minutes = 0;
        this.milliSeconds = 0;
        this.timeView.setText("00:00");
        this.start.setText("התחל");
        this.handler.removeCallbacks(this.runnable);
    }

    private void startClicked() {
        String mode = (String) this.start.getText();
        switch(mode) {
            case "התחל": {
                this.startTime = SystemClock.uptimeMillis();
                this.handler.postDelayed(this.runnable, 0);
                this.reset.setEnabled(false);
                this.start.setText("עצור");
                break;
            }
            case "עצור": {
                this.timeBuff += this.millisecondTime;
                this.handler.removeCallbacks(this.runnable);
                this.reset.setEnabled(true);
                this.start.setText("המשך");
                this.endIterationButton.setEnabled(true);
                break;
            }
            case "המשך": {
                this.startTime = SystemClock.uptimeMillis();
                this.handler.postDelayed(this.runnable, 0);
                this.reset.setEnabled(false);
                this.start.setText("עצור");
                break;
            }
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
            button.setHeight(50);
            button.setText("עצור");
            button.setTag(participant.getUid());
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
            if(participant.getUid() == view.getTag()) {
                selectedParticipant = participant;
            }
        }

        Double score = this.seconds + (60 * this.minutes) + (0.001 * this.milliSeconds);
        selectedParticipant.setScore(score.toString());

        TextView resultView = findViewById(selectedParticipant.getListviewIndex());
        String result = "";

        if(this.minutes > 0 && this.minutes < 10) {
            result += /*"0" +*/ this.minutes + ":";
        }
        if(seconds < 10) {
            result += /*"0" + */(this.seconds + ":" + this.milliSeconds);
        }
        else {
            result += (this.seconds + ":" + this.milliSeconds);
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
        if(result != null) {
            try {
                showToast("התוצאות נשמרו בהצלחה");
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(dataObj.get("type").equals("resultsMap")) {
                    dataObj.remove("type");
                    switchToViewResultsActivity(dataObj);
                }
                else if(dataObj.get("type").equals("newIteration")){
                    this.selectedCompetition = new Competition(dataObj);
                    initIteration();
                }
            }
            catch(Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            showToast("שגיאה בשמירת התוצאות במערכת, נסה שוב");
        }
        hideProgressDialog();
    }

    private void switchToViewResultsActivity(JSONObject dataObj) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("competitionResults", dataObj.toString());
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }
}
