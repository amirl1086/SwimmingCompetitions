package com.app.swimmingcompetitions.swimmingcompetitions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


public class RegisterActivity extends AppCompatActivity implements AsyncResponse {


    private EditText firstName;
    private EditText lastName;
    private Button birthDateButton;
    private EditText gender;
    private EditText eMail;
    private EditText password;
    private EditText passwordConfirmation;

    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();
        String registerType = extras.getString("registerType");

        firstName = (EditText) findViewById(R.id.register_first_name);
        lastName = (EditText) findViewById(R.id.register_last_name);
        birthDateButton = (Button)  findViewById(R.id.register_birth_date);
        gender = (EditText) findViewById(R.id.register_gender);

        if(registerType.equals("parent")) {
            birthDateButton.setVisibility(View.GONE);
            gender.setVisibility(View.GONE);
        }
        else {
            System.out.println("student: " + registerType);
        }

        eMail = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        passwordConfirmation = (EditText) findViewById(R.id.register_last_name);
    }


    public void createFirebaseUser(View view) {
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();


        String registerMailText = eMail.getText().toString();
        String registerPasswordText = password.getText().toString();

    }

    public void setDate(View view) {

    }

    @Override
    public void processFinish(String output) {
        System.out.println(output);
    }
}
