package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    private Button logInButton;
    private Button registerButton;
    private EditText logInMail;
    private EditText logInPassword;

    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_log_in);

        logInButton = (Button)findViewById(R.id.log_in_btn);
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

        jsonAsyncTaskPost.execute("/logIn", "POST", "email", logInMailText, "password", logInPasswordText);
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject dataObj = response.getJSONObject("data");
            if(response != null && response.getBoolean("success")) {
                switchToMainMenuActivity(dataObj.getString("uid"));
            }
            else {

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void switchToMainMenuActivity(String userId) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void switchToPreRegisterActivity(final View view) {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        startActivity(intent);
    }

}


