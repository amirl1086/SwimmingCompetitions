package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionsActivity extends AppCompatActivity implements AsyncResponse {

    private User currentUser = null;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private ListView listView;

    private CompetitionAdapter competitionsListAdapter;
    private ArrayList<Competition> competitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competitions);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            JSONObject data = new JSONObject();
            //get competitions list set up action params
            try {
                data.put("urlSuffix", "/getCompetitions");
                data.put("httpMethod", "GET");
                JSONObject currentUserJson = this.currentUser.getJSON_Object();
                data.put("currentUser", currentUserJson);
            } catch (JSONException e) {
                showToast("ViewCompetitionsActivity getCompetitions: Error creating JSONObject");
            }

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
        }
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {
                    ArrayList<String> displayCompetitions = new ArrayList<String>();
                    competitions = new ArrayList<Competition>();

                    if (response.get("data") != null) {
                        JSONObject dataObj = response.getJSONObject("data");
                        Iterator<String> competitionIds = dataObj.keys();

                        while (competitionIds.hasNext()) {
                            String currentId = competitionIds.next();
                            JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());

                            competitions.add(new Competition(currentId, currentCompetition));
                        }
                    }

                    competitionsListAdapter = new CompetitionAdapter(this, competitions);
                    this.listView = (ListView) findViewById(R.id.competitions_list);
                    this.listView.setAdapter(competitionsListAdapter);

                    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Competition selectedCompetition = competitions.get(position);
                            switchToViewCompetitionActivity(selectedCompetition);
                        }

                    });

                } else {
                    showToast("ViewCompetitionsActivity processFinish: Error loging in");
                }
            }
        } catch (JSONException e) {
            showToast("ViewCompetitionsActivity processFinish: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToCreateNewCompetitionActivity(View view) {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionActivity(Competition competition) {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("selectedCompetition", competition);
        startActivity(intent);
    }
}
