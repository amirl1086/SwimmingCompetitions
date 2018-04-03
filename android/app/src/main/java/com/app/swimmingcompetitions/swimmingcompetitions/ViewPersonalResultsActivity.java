package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewPersonalResultsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private ArrayList<Competition> competitions;
    private ListView listView;
    private CompetitionAdapter competitionsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_results);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            JSONObject data = new JSONObject();
            //get competitions list set up action params
            try {
                data.put("urlSuffix", "/getCompetitions");
                data.put("httpMethod", "GET");
                JSONObject currentUserJson = this.currentUser.getJSON_Object();
                data.put("currentUser", currentUserJson.toString());

                if(!this.currentUser.getType().equals("coach")) {
                    data.put("filters", "uid, results, age");
                }
                else {
                    data.put("filters", "results");
                }

            }
            catch (JSONException e) {
                showToast("ViewCompetitionsActivity getCompetitions: Error creating JSONObject");
            }

            showProgressDialog("טוען תחרויות...");

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
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
                    this.competitions = new ArrayList<>();

                    if (response.get("data") != null) {
                        JSONObject dataObj = response.getJSONObject("data");
                        Iterator<String> competitionIds = dataObj.keys();

                        while (competitionIds.hasNext()) {
                            String currentId = competitionIds.next();
                            JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());

                            this.competitions.add(new Competition(currentId, currentCompetition));
                        }
                    }

                    this.competitionsListAdapter = new CompetitionAdapter(this, R.layout.competition_list_item, competitions);
                    this.listView = findViewById(R.id.competitions_list);
                    this.listView.setAdapter(this.competitionsListAdapter);

                    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Competition selectedCompetition = competitions.get(position);
                            switchToViewCompetitionResultsActivity(selectedCompetition);
                        }
                    });
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

    public void switchToViewCompetitionResultsActivity(Competition competition) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("selectedCompetition", competition);
        startActivity(intent);
    }
}
