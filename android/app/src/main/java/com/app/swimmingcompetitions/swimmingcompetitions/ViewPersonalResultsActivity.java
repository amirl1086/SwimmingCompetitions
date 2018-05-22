package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewPersonalResultsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
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
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            JSONObject data = new JSONObject();
            //get competitions list set up action params
            try {
                data.put("urlSuffix", "/getCompetitions");
                data.put("httpMethod", "GET");
                JSONObject currentUserJson = this.currentUser.getJSON_Object();
                data.put("currentUser", currentUserJson.toString());

                if(this.currentUser.getType().equals("student")) {
                    data.put("filters", "uid, isDone");
                }
                else {
                    data.put("filters", "isDone");
                }

                jsonAsyncTaskPost = new JSON_AsyncTask();
                jsonAsyncTaskPost.delegate = this;
                jsonAsyncTaskPost.execute(data.toString());
            }
            catch (JSONException e) {
                hideProgressDialog();
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");
                Iterator<String> competitionIds = dataObj.keys();
                this.competitions = new ArrayList<>();

                while (competitionIds.hasNext()) {
                    String currentId = competitionIds.next();
                    JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());

                    this.competitions.add(new Competition(currentId, currentCompetition));
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
            catch (Exception e) {
                showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
                System.out.println("ViewPersonalResultsActivity Exception " + e.getStackTrace());
            }
        }
        else {
            showToast("שגיאה בשליפת התחרויות מהמערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void switchToViewCompetitionResultsActivity(Competition competition) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", competition);
        startActivity(intent);
    }
}
