package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IterationsActivity extends AppCompatActivity {

    private User currentUser;
    private Competition selectedCompetition;
    ArrayList<Participant> currentParticipants;

    private TextView timeView;
    private Button start, reset, endIterationButton;

    private long millisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private int Seconds, Minutes, MilliSeconds ;
    private Handler handler;

    private LinearLayout buttonsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iterations);

        DateUtils dateUtils = new DateUtils();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

            this.buttonsLayout = (LinearLayout) findViewById(R.id.buttons_layout);

            int totalWidth = this.buttonsLayout.getWidth();
            int totalHeight = this.buttonsLayout.getHeight();
            int numOfParticipants = Integer.valueOf(this.selectedCompetition.getNumOfParticipants());

            this.currentParticipants = this.selectedCompetition.getNewParticipants();

            for(int i = 0; i < numOfParticipants; i++){
                Button button = new Button(this);
                button.setWidth(totalWidth / numOfParticipants);
                Participant participant = currentParticipants.get(i);
                button.setText(participant.getFirstName() + participant.getLastName());
                button.setTag(participant.getId());

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Participant selectedParticipant = null;
                        for(Participant participant : currentParticipants) {
                            if(participant.getId() == view.getTag()) {
                                selectedParticipant = participant;
                            }
                        }

                        selectedParticipant.setCompeted(true);
                        selectedParticipant.setScore((double) (Seconds + (60 * Minutes) + (1000 * MilliSeconds)));
                    }
                });

                this.buttonsLayout.addView(button);
            }
        }

        this.timeView = (TextView)findViewById(R.id.time_view);
        this.start = (Button)findViewById(R.id.start_time_btn);
        this.reset = (Button)findViewById(R.id.reset_btn);
        this.endIterationButton = (Button)findViewById(R.id.end_iteration_btn);

        this.handler = new Handler();



        this.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mode = (String) start.getText();
                if(mode.equals("התחל")) {
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    reset.setEnabled(false);
                    start.setText("עצור");
                    endIterationButton.setEnabled(false);
                }
                else if(mode.equals("עצור")) {
                    TimeBuff += millisecondTime;
                    handler.removeCallbacks(runnable);
                    reset.setEnabled(true);
                    start.setText("המשך");
                }
                else if(mode.equals("המשך")) {
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    reset.setEnabled(false);
                    start.setText("עצור");
                }

            }
        });

        this.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                millisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;
                timeView.setText("00:00:00");
                start.setText("התחל");
                endIterationButton.setEnabled(true);
            }
        });

        this.endIterationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public Runnable runnable = new Runnable() {
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + millisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            timeView.setText("" + Minutes + ":" + String.format("%02d", Seconds) + ":" + String.format("%03d", MilliSeconds));
            handler.postDelayed(this, 0);
        }
    };
}
