package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private ListView listView;
    private AgeResultAdapter resultsListAdapter;
    private ArrayList<JSONObject> results;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private Competition selectedCompetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_results);

        this.listView = findViewById(R.id.results_list_items);
        this.results = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

                JSONObject competitionResults = new JSONObject(intent.getStringExtra("competitionResults"));
                Iterator<String> agesKeys = competitionResults.keys();

                while (agesKeys.hasNext()) {
                    String currentAge = agesKeys.next();
                    JSONObject currentResult = new JSONObject(competitionResults.get(currentAge).toString());
                    this.results.add(currentResult);
                }

                this.resultsListAdapter = new AgeResultAdapter(this, R.layout.age_result_list_item, results);
                this.listView.setAdapter(this.resultsListAdapter);
            }
            catch (JSONException e) {
                showToast("ViewCompetitionResultsActivity onCreate: Error initializing results");
            }


            /*JSONObject data = new JSONObject();
            //get competitions list set up action params
            try {
                data.put("urlSuffix", "/getPersonalResults");
                data.put("httpMethod", "GET");
                JSONObject selectedCompetitionJson = this.selectedCompetition.getJSON_Object();
                data.put("competition", selectedCompetitionJson.toString());

            }
            catch (JSONException e) {
                showToast("ViewCompetitionsActivity getCompetitions: Error creating JSONObject");
            }

            showProgressDialog("טוען תחרויות...");

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());*/
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {

                    JSONObject dataObj = response.getJSONObject("data");
                    Iterator<String> agesKeys = dataObj.keys();

                    while (agesKeys.hasNext()) {
                        String currentAge = agesKeys.next();
                        JSONObject currentResult = new JSONObject(dataObj.get(currentAge).toString());
                        this.results.add(currentResult);
                    }

                    this.resultsListAdapter = new AgeResultAdapter(this, R.layout.age_result_list_item, results);
                    this.listView.setAdapter(this.resultsListAdapter);
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
}
