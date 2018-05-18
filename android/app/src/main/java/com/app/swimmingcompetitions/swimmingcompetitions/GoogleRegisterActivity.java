package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class GoogleRegisterActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseAuth mAuth;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private EditText firstName;
    private EditText lastName;
    private TextView dateView;
    private EditText phoneNumber;
    private int year, month, day;
    private String registerType;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_register);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();

            this.spinner = findViewById(R.id.register_gender);
            this.firstName = findViewById(R.id.register_first_name);
            this.firstName.setText(this.currentUser.getFirstName());
            this.lastName = findViewById(R.id.register_last_name);
            this.lastName.setText(this.currentUser.getLastName());
            this.phoneNumber = findViewById(R.id.mobile_phone_number);
            this.registerType = intent.getStringExtra("registerType");
            Button birthDateButton = findViewById(R.id.register_birth_date);

            if (this.registerType.equals("parent")) {
                birthDateButton.setVisibility(View.GONE);
                this.spinner.setVisibility(View.GONE);
            }
            else {
                initParticipantUser();
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    private void initParticipantUser() {
        String[] genders = new String[]{"בחר מין", "זכר", "נקבה"};

        ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        this.spinner.setAdapter(spinnerListAdapter);

        this.dateView = findViewById(R.id.birth_date_view);
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);

        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                this.currentUser = new User(dataObj);
                switchToMainMenuActivity();
            }
            else {
                showToast("שגיאה בהרשמה למערכת, נסה לאתחל את האפליקציה");
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void updateFirebaseUser(View view) {
        String firstNameText = this.firstName.getText().toString();
        String lastNameText = this.lastName.getText().toString();
        String genderText = "", birthDateText = "";

        if (this.registerType.equals("student")) {
            birthDateText = dateView.getText().toString();
            genderText = spinner.getSelectedItem().toString();
        }
        String phoneNumberText = this.phoneNumber.getText().toString();

        JSONObject registerData = new JSONObject();
        try {
            registerData.put("urlSuffix", "/updateFirebaseUser");
            registerData.put("httpMethod", "POST");
            registerData.put("phoneNumber", phoneNumberText);
            registerData.put("uid", this.currentUser.getUid());
            registerData.put("firstName", firstNameText);
            registerData.put("lastName", lastNameText);
            registerData.put("gender", genderText);
            registerData.put("birthDate", birthDateText);
            registerData.put("type", this.registerType);
            if(this.currentUser != null) {
                registerData.put("joinToCompetition", true);
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בתהליך שמירת הפרטים, נסה לאתחל את האפליקציה");
        }

        this.jsonAsyncTaskPost = new JSON_AsyncTask();
        this.jsonAsyncTaskPost.delegate = this;

        showProgressDialog("נרשם למערכת...");

        this.jsonAsyncTaskPost.execute(registerData.toString());
    }


    public void setDate(View view) {
        showDialog(R.integer.dialog_id);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
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
}
