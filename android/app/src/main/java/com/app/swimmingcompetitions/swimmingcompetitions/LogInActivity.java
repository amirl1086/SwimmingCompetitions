package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private EditText logInMail;
    private EditText logInPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        this.logInMail = findViewById(R.id.edit_email);
        this.logInPassword = findViewById(R.id.edit_password);

        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.fbUser != null) {
            switchToMainMenuActivity();
        }
        this.fbUser = this.mAuth.getCurrentUser();
    }

    public void firebaseLogIn(final View view) {
        String logInMailText = this.logInMail.getText().toString();
        String logInPasswordText = this.logInPassword.getText().toString();

        showProgressDialog("מבצע כניסה...");

        this.mAuth.signInWithEmailAndPassword(logInMailText, logInPasswordText)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        fbUser = mAuth.getCurrentUser();
                        fbUser.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        logInWithIdToken(task.getResult().getToken());
                                        // ...
                                    } else {
                                        // Handle error -> task.getException();
                                    }
                                }
                            });
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        showToast("ההתחברות נכשלה");
                    }
                }
            });

        /*JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
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

        jsonAsyncTaskPost.execute(logInData.toString());*/
    }

    private void logInWithIdToken(String token) {
        JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        try {
            logInData.put("urlSuffix", "/logIn");
            logInData.put("httpMethod", "POST");
            logInData.put("idToken", token);
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


