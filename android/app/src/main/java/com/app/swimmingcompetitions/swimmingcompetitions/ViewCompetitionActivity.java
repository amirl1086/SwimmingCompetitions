package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class ViewCompetitionActivity extends AppCompatActivity {

    private User currentUser = null;
    private Competition selectedCompetition;

    private Button registerEditBtn;
    private Button registerTempUserBtn;

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
            participantsForIteration.setText(selectedCompetition.getNumOfParticipants());
        }
    }

    public void switchToIterationsActivity(final View view) {
        Intent intent = new Intent(this, IterationsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }
}
