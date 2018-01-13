package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    private ArrayAdapter competitionsListAdapter;
    private ArrayList<Competition> competitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competitions);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            currentUser = (User) intent.getSerializableExtra("currentUser");

            JSONObject data = new JSONObject();
            //get competitions list set up action params
            try {
                data.put("urlSuffix", "/getCompetitions");
                data.put("httpMethod", "GET");
                JSONObject currentUserJson = currentUser.getJSON_Object();
                data.put("currentUser", currentUserJson);
            } catch (JSONException e) {
                showToast("ViewCompetitionsActivity getCompetitions: Error creating JSONObject");
            }

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
        }
        else if(intent.hasExtra("newCompetition")) {

        }




    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {
                    ArrayList<String> displayCompetitions = new ArrayList<String>();
                    competitions = new ArrayList<Competition>();

                    if(response.get("data") != null) {
                        JSONObject dataObj = response.getJSONObject("data");
                        Iterator<String> competitionIds = dataObj.keys();

                        while (competitionIds.hasNext()) {
                            String currentId = (String) competitionIds.next();
                            JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());
                            String name = currentCompetition.getString("name");
                            String activityDate = currentCompetition.getString("activityDate");
                            String swimmingStyle = currentCompetition.getString("swimmingStyle");
                            int numOfParticipants = currentCompetition.getInt("numOfParticipants");
                            int length = currentCompetition.getInt("length");

                            Competition currCompetition = new Competition(currentId, name, activityDate, swimmingStyle, numOfParticipants, length);
                            competitions.add(currCompetition);
                        }

                        //set up list view
                        for (int i = 0; i < competitions.size(); i++) {
                            Competition currCompetition = competitions.get(i);
                            displayCompetitions.add("תחרות " + currCompetition.getName() + "\n" +
                                    ",מתקיימת בתאריך: " + currCompetition.getActivityDate() + "\n" +
                                    ",סגנון שחייה: " + currCompetition.getSwimmingStyle() + "\n" +
                                    ",מספר משתתפים למקצה: " + currCompetition.getNumOfParticipants() + "\n" +
                                    ",אורך המקצים: " + currCompetition.getLength() + "\n");
                        }
                    }

                    ListView listView = (ListView) findViewById(R.id.competitions_list);
                    competitionsListAdapter = new ArrayAdapter<String>(this, R.layout.competition_list_item, R.id.competition_item, displayCompetitions);
                    listView.setAdapter(competitionsListAdapter);

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

    public void openCreateNewCompetitionActivity(View view) {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("User", currentUser);
        startActivity(intent);
    }
}
