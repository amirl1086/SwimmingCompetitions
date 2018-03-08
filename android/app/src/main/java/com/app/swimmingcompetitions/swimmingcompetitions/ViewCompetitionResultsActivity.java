package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends AppCompatActivity {

    private User currentUser;
    private ListView listView;
    private ResultAdapter resultsListAdapter;
    private ArrayList<JSONObject> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_results);

        this.listView = findViewById(R.id.results_list_items);
        this.results = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("competitionResults")) {
            JSONObject dataObj = null;
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");

                dataObj = new JSONObject(intent.getStringExtra("competitionResults"));
                System.out.println(intent.getStringExtra("competitionResults"));
                Iterator<String> agesKeys = dataObj.keys();

                while (agesKeys.hasNext()) {
                    String currentAge = agesKeys.next();
                    JSONObject currentResult = new JSONObject(dataObj.get(currentAge).toString());

                    results.add(currentResult);







/*                    JSONArray malesResults = new JSONArray(currentResult.getString("males"));
                    JSONArray femalesResults = new JSONArray(currentResult.getString("females"));

                    if(malesResults.length() > 0 || femalesResults.length() > 0) {
                        ArrayList<Participant> maleParticipants = new ArrayList<>();
                        ArrayList<Participant> femaleParticipants = new ArrayList<>();

                        LinearLayout currentLayout = new LinearLayout(this);
                        TextView ageText = new TextView(this);
                        ageText.setText("עבור גילאי " + currentAge + ":");
                        currentLayout.addView(ageText);

                        for(int i = 0; i < malesResults.length(); i++) {
                            Object maleResult = malesResults.get(i);

                            maleParticipants.add(new Participant(new JSONObject(malesResults.get(i).toString())));
                        }

                        for(int i = 0; i < femalesResults.length(); i++) {
                            femaleParticipants.add(new Participant(new JSONObject(femalesResults.get(i).toString())));
                        }

                        TextView maleResults = new TextView(this);
                        TextView femaleResults = new TextView(this);
                        String maleResultsStr = "";
                        String femaleResultsStr = "";

                        if(maleParticipants.size() > 0) {
                            maleResultsStr += "בנים: ";
                            for (int i = 0; i < maleParticipants.size(); i++) {
                                maleResultsStr += (places[i] + maleParticipants.get(0).toString());
                            }
                            maleResults.setText(maleResultsStr);
                            currentLayout.addView(maleResults);
                        }

                        if(femaleParticipants.size() > 0) {
                            femaleResultsStr += "בנות: ";
                            for (int i = 0; i < femaleParticipants.size(); i++) {
                                femaleResultsStr += (places[i] + femaleParticipants.get(0).toString());
                            }
                            femaleResults.setText(femaleResultsStr);
                            currentLayout.addView(femaleResults);
                        }
                        this.resultsGrid.addView(currentLayout);
                    }*/
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

                this.resultsListAdapter = new ResultAdapter(this, results);
                this.listView.setAdapter(this.resultsListAdapter);


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
