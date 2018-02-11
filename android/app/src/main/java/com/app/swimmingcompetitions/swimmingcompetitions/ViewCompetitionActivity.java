package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class ViewCompetitionActivity extends AppCompatActivity {

    private User currentUser = null;
    private Competition competition;

    private TextView competitionName;
    private TextView date;
    private TextView time;
    private TextView distance;
    private TextView style;
    private TextView ages;
    private TextView participantsForIteration;
    private Button registerEditBtn;
    private Button registerTempUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);

        DateUtils dateUtils = new DateUtils();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.competition = (Competition) intent.getSerializableExtra("selectedCompetition");

            this.competitionName = (TextView) findViewById(R.id.competition_name);
            this.date = (TextView) findViewById(R.id.date_of_competition);
            this.time = (TextView) findViewById(R.id.time_of_competition);
            this.distance = (TextView) findViewById(R.id.distance_of_competition);
            this.style = (TextView) findViewById(R.id.style_of_competition);
            this.ages = (TextView) findViewById(R.id.ages_range_of_competition);
            this.participantsForIteration = (TextView) findViewById(R.id.num_of_participants_for_competition);
            Calendar calendar = dateUtils.dateToCalendar(this.competition.getActivityDate());

            this.date.setText(dateUtils.getHebrewDate(calendar));
            this.competitionName.setText(competition.getName());
            this.time.setText(dateUtils.getTime(calendar));
            this.distance.setText(competition.getLength());
            this.style.setText(competition.getSwimmingStyle());
            this.ages.setText(competition.getAgesString());
            this.participantsForIteration.setText(competition.getNumOfParticipants());
        }
    }
}
