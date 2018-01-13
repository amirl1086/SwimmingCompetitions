package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends AppCompatActivity implements AsyncResponse {

    private User currentUser = null;

    private Button personalResultsBtn;
    private Button viewCompetitionsBtn;
    private Button realTimeBtn;
    private Button statisticsBtn;
    private Button imagesAndPicturesBtn;
    private Button settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void openViewCompetitionsActivity(View view) {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            if (response != null && response.getBoolean("success")) {
                JSONObject userData = response.getJSONObject("data");

                String uid = userData.getString("uid");
                String firstName = userData.getString("firstName");
                String lastName = userData.getString("lastName");
                String birthDate = userData.getString("birthDate");
                String email = userData.getString("email");
                String gender = userData.getString("gender");
                String type = userData.getString("type");

                currentUser = new User(uid, firstName, lastName, birthDate, email, gender, type);
            } else {
                showToast("LogInActivity processFinish: Error loging in");
            }
        } catch (JSONException e) {
            showToast("LogInActivity processFinish: Error parsing JSONObject");
        }
    }
}
