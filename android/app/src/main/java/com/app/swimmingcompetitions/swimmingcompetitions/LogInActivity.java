package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends AppCompatActivity implements AsyncResponse {

    private User currentUser = null;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private EditText logInMail;
    private EditText logInPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInMail = (EditText) findViewById(R.id.edit_email);
        logInPassword = (EditText) findViewById(R.id.edit_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            switchToMainMenuActivity();
        }
    }

    public void firebaseLogIn(final View view) {
        String logInMailText = logInMail.getText().toString();
        String logInPasswordText = logInPassword.getText().toString();

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        //set up action params
        try {
            logInData.put("urlSuffix", "/logIn");
            logInData.put("httpMethod", "POST");
            logInData.put("email", logInMailText);
            logInData.put("password", logInPasswordText);
        } catch (JSONException e) {
            showToast("LogInActivity firebaseLogIn: Error creating JSONObject");
        }

        //call the server
        jsonAsyncTaskPost.execute(logInData.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);
                if(response.getBoolean("success")) {
                    JSONObject userData = response.getJSONObject("data");

                    String uid = userData.getString("uid");
                    String firstName = userData.getString("firstName");
                    String lastName = userData.getString("lastName");
                    String birthDate = userData.getString("birthDate");
                    String email = userData.getString("email");
                    String gender = userData.getString("gender");
                    String type = userData.getString("type");

                    currentUser = new User(uid, firstName, lastName, birthDate, email, gender, type);

                    switchToMainMenuActivity();
                }
            } else {
                showToast("LogInActivity processFinish: Error loging in");
            }
        } catch (JSONException e) {
            showToast("LogInActivity processFinish: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToPreRegisterActivity(final View view) {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        startActivity(intent);
    }

}


