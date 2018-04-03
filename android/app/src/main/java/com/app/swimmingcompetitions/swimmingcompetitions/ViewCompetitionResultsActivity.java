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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private ListView listView;
    private ResultAdapter resultsListAdapter;
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
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

            JSONObject data = new JSONObject();
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
            this.jsonAsyncTaskPost.execute(data.toString());
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

                    this.resultsListAdapter = new ResultAdapter(this, R.layout.result_list_item, results);
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
