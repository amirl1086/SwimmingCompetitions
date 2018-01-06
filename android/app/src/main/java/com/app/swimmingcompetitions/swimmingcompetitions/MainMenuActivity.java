package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        JSONObject currentUser;
        Intent intent = getIntent();
        try {
            currentUser = new JSONObject(intent.getStringExtra("currentUser"));
            if(!currentUser.getString("type").equals("coach")) {

            }
        }
        catch (JSONException e) {
            showToast("MainMenuActivity onCreate: Error parsing JSONObject");
        }


    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
