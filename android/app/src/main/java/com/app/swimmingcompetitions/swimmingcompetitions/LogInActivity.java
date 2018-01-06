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

    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;

    private Button registerButton;
    private EditText logInMail;
    private EditText logInPassword;

    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInMail   = (EditText)findViewById(R.id.edit_email);
        logInPassword   = (EditText)findViewById(R.id.edit_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    public void firebaseLogIn(final View view) {
        String logInMailText = logInMail.getText().toString();
        String logInPasswordText = logInPassword.getText().toString();

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        try {
            logInData.put("urlSuffix", "/logIn");
            logInData.put("httpMethod", "POST");
            logInData.put("email", logInMailText);
            logInData.put("password", logInPasswordText);
        }
        catch (JSONException e) {
            showToast("LogInActivity firebaseLogIn: Error creating JSONObject");
        }

        jsonAsyncTaskPost.execute(logInData.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject dataObj = response.getJSONObject("data");
            if(response != null && response.getBoolean("success")) {
                switchToMainMenuActivity(dataObj);
            }
            else {
                showToast("LogInActivity processFinish: Error loging in");
            }
        }
        catch (JSONException e) {
            showToast("LogInActivity processFinish: Error parsing JSONObject");
        }

    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToMainMenuActivity(JSONObject user) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("currentUser", user.toString());
        startActivity(intent);
    }

    public void switchToPreRegisterActivity(final View view) {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        startActivity(intent);
    }

}


