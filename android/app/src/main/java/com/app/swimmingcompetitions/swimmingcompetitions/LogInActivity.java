package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends AppCompatActivity implements AsyncResponse {

    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;

    private Button logInButton;
    private EditText logInMail;
    private EditText logInPassword;

    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    public void firebaseLogIn(final View view) {
        logInButton = (Button)findViewById(R.id.log_in_btn);
        logInMail   = (EditText)findViewById(R.id.edit_email);
        logInPassword   = (EditText)findViewById(R.id.edit_password);

        String logInMailText = logInMail.getText().toString();
        String logInPasswordText = logInPassword.getText().toString();

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;


        firebaseAuth.signInWithEmailAndPassword(logInMailText, logInPasswordText)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();

                        jsonAsyncTaskPost.execute("POST", "/addNewUser", "userId", currentUser.getUid());

                        switchToMainMenuActivity();
                    } else {
                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                }
            });
    }

    @Override
    public void processFinish(String result) {
        System.out.println("result " + result);
        if(result != null && !result.isEmpty()) {
            try {
                JSONObject resultObj = new JSONObject(result);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void switchToRegisterActivity() {
        //Intent intent = new Intent(this, RegisterActivity.class);
        //startActivity(intent);
    }

    public void firebaseRegister(View view) {
        logInButton = (Button) findViewById(R.id.log_in_btn);
        logInMail   = (EditText) findViewById(R.id.edit_email);
        logInPassword   = (EditText) findViewById(R.id.edit_password);

        String logInMailText = logInMail.getText().toString();
        String logInPasswordText = logInMail.getText().toString();


        this.firebaseAuth.createUserWithEmailAndPassword(logInMailText, logInPasswordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}
