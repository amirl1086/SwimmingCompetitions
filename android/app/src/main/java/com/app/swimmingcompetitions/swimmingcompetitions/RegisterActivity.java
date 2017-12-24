package com.app.swimmingcompetitions.swimmingcompetitions;

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
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {

    private Button registerButton;
    private EditText registerMail;
    private EditText registerPassword;
    private EditText registerFirstName;
    private EditText registerLastName;

    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void firebaseLogIn(final View view) {
        registerButton = (Button)findViewById(R.id.register_btn);
        registerMail   = (EditText)findViewById(R.id.register_email);
        registerPassword   = (EditText)findViewById(R.id.register_password);
        registerFirstName   = (EditText)findViewById(R.id.register_first_name);
        registerLastName   = (EditText)findViewById(R.id.register_last_name);

        Map<String, List<String>> httpData;
        String logInMailText = registerMail.getText().toString();
        String logInPasswordText = registerPassword.getText().toString();
        String registerFirstNameText = registerFirstName.getText().toString();
        String registerLastNameText = registerLastName.getText().toString();

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;

        jsonAsyncTaskPost.execute("POST", "/addNewUser", "email", logInMailText);

    }

    @Override
    public void processFinish(String output) {
        System.out.println(output);
    }
}
