package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends AppCompatActivity {

    private RelativeLayout resultsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_results);

        this.resultsGrid = findViewById(R.id.results_grid);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("competitionResults")) {
            JSONObject dataObj = null;
            try {
                dataObj = new JSONObject(intent.getStringExtra("competitionResults"));
                Iterator<String> agesKeys = dataObj.keys();

                while (agesKeys.hasNext()) {
                    String currentAge = agesKeys.next();
                    JSONObject currentResult = new JSONObject(dataObj.get(currentAge).toString());
                    JSONArray malesResults = new JSONArray(currentResult.getString("males"));
                    JSONArray femalesResults = new JSONArray(currentResult.getString("females"));

                    ArrayList<Participant> maleParticipants = new ArrayList<>();
                    ArrayList<Participant> femaleParticipants = new ArrayList<>();

                    TextView ageText = new TextView(this);
                    ageText.setText("עבור גילאי " + currentAge + ":");
                    this.resultsGrid.addView(ageText);

                    for(int i = 0; i < malesResults.length(); i++) {
                        Object maleResult = malesResults.get(i);

                        maleParticipants.add(new Participant(new JSONObject(malesResults.get(i).toString())));
                    }

                    for(int i = 0; i < femalesResults.length(); i++) {
                        femaleParticipants.add(new Participant(new JSONObject(femalesResults.get(i).toString())));
                    }

                    TextView maleResults = new TextView(this);
                    TextView femaleResults = new TextView(this);
                    maleResults.setText("בנים: מקום ראשון - " + maleParticipants.get(0).toString() + ", מקום שני - " + maleParticipants.get(1).toString() + ", מקום שלישי - " + maleParticipants.get(2).toString());
                    femaleResults.setText("בנות: מקום ראשון - " + femaleParticipants.get(0).toString() + ", מקום שני - " + femaleParticipants.get(1).toString() + ", מקום שלישי - " + femaleParticipants.get(2).toString());
/*
                    Iterator<String> maleUsersIds = malesResults.keys();
                    Iterator<String> femaleUsersIds = malesResults.keys();

                    while (maleUsersIds.hasNext()) {
                        String currentMaleId = maleUsersIds.next();

                        maleParticipants.add(new Participant(currentMaleId, new JSONObject(malesResults.getString(currentMaleId))));
                    }

                    while (femaleUsersIds.hasNext()) {
                        String currentFemaleId = femaleUsersIds.next();

                        femaleParticipants.add(new Participant(currentFemaleId, new JSONObject(malesResults.getString(currentFemaleId))));
                    }*/

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
