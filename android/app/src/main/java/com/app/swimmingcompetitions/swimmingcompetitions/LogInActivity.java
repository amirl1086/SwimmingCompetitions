package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private EditText logInMail;
    private EditText logInPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInMail = findViewById(R.id.edit_email);
        logInPassword = findViewById(R.id.edit_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.currentUser != null) {
            switchToMainMenuActivity();
        }
    }

    public void firebaseLogIn(final View view) {
        String logInMailText = logInMail.getText().toString();
        String logInPasswordText = logInPassword.getText().toString();

        JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        try {
            logInData.put("urlSuffix", "/logIn");
            logInData.put("httpMethod", "POST");
            logInData.put("email", logInMailText);
            logInData.put("password", logInPasswordText);
        }
        catch (JSONException e) {
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("מבצע כניסה...");

        jsonAsyncTaskPost.execute(logInData.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject userData = response.getJSONObject("data");
                this.currentUser = new User(userData);

                switchToMainMenuActivity();
            }
            else {
                showToast("שגיאה בכניסה למערכת, נסה שוב");
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
        }
        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToPreRegisterActivity(final View view) {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        startActivity(intent);
    }

}


