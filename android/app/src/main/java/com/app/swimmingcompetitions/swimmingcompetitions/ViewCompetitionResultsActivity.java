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

                if(intent.hasExtra("competitionResults")) {
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
                else {
                    JSONObject data = new JSONObject();
                    //get competitions list set up action params
                    try {
                        data.put("urlSuffix", "/getPersonalResults");
                        data.put("httpMethod", "GET");
                        data.put("competition", this.selectedCompetition.getJSON_Object().toString());
                    }
                    catch (JSONException e) {
                        showToast("שגיאה בשליפה של התוצאות, נסה לאתחל את האפליקציה");
                    }

                    showProgressDialog("טוען תוצאות...");

                    this.jsonAsyncTaskPost = new JSON_AsyncTask();
                    this.jsonAsyncTaskPost.delegate = this;
                    this.jsonAsyncTaskPost.execute(data.toString());
                }

            }
            catch (JSONException e) {
                showToast("ViewCompetitionResultsActivity onCreate: Error initializing results");
            }
        }
        else {

        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);

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
            catch(Exception e) {
                showToast("שגיאה בטעינת המידע מהשרת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            showToast("שגיאה בטעינת המידע מהשרת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
