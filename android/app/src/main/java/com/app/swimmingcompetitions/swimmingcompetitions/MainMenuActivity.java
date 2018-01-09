package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends AppCompatActivity {

    /*public Button personalResultsBtn;
    public Button viewCompetitionsBtn;
    public Button realTimeBtn;
    public Button statisticsBtn;
    public Button imagesAndPicturesBtn;
    public Button settingsBtn;*/

    public Button setNewCompetitionBtn;
    public Button editCompetitionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setNewCompetitionBtn = (Button)  findViewById(R.id.set_new_competition_btn);
        editCompetitionBtn = (Button)  findViewById(R.id.edit_competition_btn);

        JSONObject currentUser;
        Intent intent = getIntent();
        try {
            currentUser = new JSONObject(intent.getStringExtra("currentUser"));
            String type = currentUser.getString("type");
            if(!type.equals("coach")) {
                setNewCompetitionBtn.setVisibility(View.GONE);
                editCompetitionBtn.setVisibility(View.GONE);
            }
        }
        catch (JSONException e) {
            showToast("MainMenuActivity onCreate: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void openNewCompetitionActivity(View view) {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        startActivity(intent);
    }
}
