package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;


public class ViewCompetitionsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser = null;
    private Competition selectedCompetition;
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
            }
            catch (JSONException e) {
                showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה ");
            }

            showProgressDialog("טוען תחרויות...");

            JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
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
                    JSONObject dataObj = response.getJSONObject("data");
                    this.competitions = new ArrayList<>();

                    Iterator<String> competitionIds = dataObj.keys();
                    while (competitionIds.hasNext()) {
                        String currentId = competitionIds.next();
                        JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());
                        this.competitions.add(new Competition(currentId, currentCompetition));
                    }

                    CompetitionAdapter competitionsListAdapter = new CompetitionAdapter(this, R.layout.competition_list_item, competitions);
                    ListView listView = findViewById(R.id.competitions_list);
                    listView.setAdapter(competitionsListAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            selectedCompetition = competitions.get(position);
                            switchToViewCompetitionActivity();
                        }
                    });
                }
                else {
                    showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
                }
            }
        }
        catch (JSONException e) {
            showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }


    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToCreateNewCompetitionActivity(View view) {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionActivity() {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }
}
