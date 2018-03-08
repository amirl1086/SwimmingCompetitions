package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class RegisterActivity extends AppCompatActivity implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;

    private EditText firstName;
    private EditText lastName;
    private EditText eMail;
    private EditText password;
    private EditText passwordConfirmation;
    private Button birthDateButton;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private String registerType;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerListAdapter;
    private String[] genders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();
        this.registerType = extras.getString("registerType");

        this.firstName = findViewById(R.id.register_first_name);
        this.lastName = findViewById(R.id.register_last_name);
        this.birthDateButton = findViewById(R.id.register_birth_date);

        //set up spinner picker for swimming style
        this.genders = new String[]{"בחר מין", "זכר", "נקבה"};
        this.spinner = findViewById(R.id.register_gender);
        this.spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        this.spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        this.spinner.setAdapter(spinnerListAdapter);

        if (this.registerType.equals("parent")) {
            this.birthDateButton.setVisibility(View.GONE);
            this.spinner.setVisibility(View.GONE);
        } else { //initialize date picker for date of birth
            this.dateView = findViewById(R.id.birth_date_view);
            this.calendar = Calendar.getInstance();
            this.year = calendar.get(Calendar.YEAR);

            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
        }

        this.eMail = findViewById(R.id.register_email);
        this.password = findViewById(R.id.register_password);
        this.passwordConfirmation = findViewById(R.id.register_password_confirmation);
    }


    public void createFirebaseUser(View view) {
        String firstNameText = this.firstName.getText().toString();
        String lastNameText = this.lastName.getText().toString();
        String genderText = "", birthDateText = "";

        if (this.registerType.equals("student")) {
            birthDateText = dateView.getText().toString();
            genderText = spinner.getSelectedItem().toString();
        }

        String eMailText = this.eMail.getText().toString();
        String passwordText = this.password.getText().toString();
        String passwordConfirmationText = this.passwordConfirmation.getText().toString();

        JSONObject registerData = new JSONObject();

        try {
            registerData.put("urlSuffix", "/addNewUser");
            registerData.put("httpMethod", "POST");
            registerData.put("email", eMailText);
            registerData.put("password", passwordText);
            registerData.put("passwordConfirmation", passwordConfirmationText);
            registerData.put("firstName", firstNameText);
            registerData.put("lastName", lastNameText);
            registerData.put("gender", genderText);
            registerData.put("birthDate", birthDateText);
            registerData.put("type", this.registerType);
        } catch (JSONException e) {
            showToast("RegisterActivity, createFirebaseUser: Error creating JSONObject");
        }

        this.jsonAsyncTaskPost = new JSON_AsyncTask();
        this.jsonAsyncTaskPost.delegate = this;

        this.jsonAsyncTaskPost.execute(registerData.toString());
    }

    public void setDate(View view) {
        showDialog(R.integer.dialog_id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.integer.dialog_id) {
            return new DatePickerDialog(this, this.myDateListener, this.year, this.month, this.day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            showDate(year, month + 1, day);
        }
    };

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject dataObj = response.getJSONObject("data");
            if (response.getBoolean("success")) {
                User currentUser = new User(dataObj);
                switchToMainMenuActivity(currentUser);
            } else {
                showToast("LogInActivity processFinish: Error registering");
            }
        } catch (JSONException e) {
            showToast("RegisterActivity, processFinish: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToMainMenuActivity(User currentUser) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

}
