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


public class RegisterActivity extends LoadingDialog implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private EditText firstName;
    private EditText lastName;
    private EditText eMail;
    private EditText password;
    private EditText phoneNumber;
    private EditText passwordConfirmation;
    private TextView dateView;
    private int year, month, day;
    private String registerType;
    private Spinner spinner;
    private Competition selectedCompetition;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.registerType = "student";
        }
        else {
            this.registerType = intent.getStringExtra("registerType");

        }
        this.spinner = findViewById(R.id.register_gender);
        this.firstName = findViewById(R.id.register_first_name);
        this.lastName = findViewById(R.id.register_last_name);
        this.eMail = findViewById(R.id.register_email);
        this.password = findViewById(R.id.register_password);
        this.phoneNumber = findViewById(R.id.mobile_phone_number);
        this.passwordConfirmation = findViewById(R.id.register_password_confirmation);
        Button birthDateButton = findViewById(R.id.register_birth_date);

        if (this.registerType.equals("parent")) {
            birthDateButton.setVisibility(View.GONE);
            this.spinner.setVisibility(View.GONE);
        }
        else { //initialize date picker for date of birth
            initParticipantUser();
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
        String phoneNumberText = this.phoneNumber.getText().toString();
        String passwordConfirmationText = this.passwordConfirmation.getText().toString();

        JSONObject registerData = new JSONObject();

        try {
            registerData.put("urlSuffix", "/addNewUser");
            registerData.put("httpMethod", "POST");
            registerData.put("email", eMailText);
            registerData.put("phoneNumber", phoneNumberText);
            registerData.put("password", passwordText);
            registerData.put("passwordConfirmation", passwordConfirmationText);
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
            showToast("שגיאה בתהליך ההרשמה, נסה לאתחל את האפליקציה");
        }

        this.jsonAsyncTaskPost = new JSON_AsyncTask();
        this.jsonAsyncTaskPost.delegate = this;

        showProgressDialog("נרשם למערכת...");

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
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(this.currentUser != null) {
                    showToast("המתחרה נוסף בהצלחה");
                    switchToViewCompetitionActivity(dataObj);
                }
                else {
                    showToast("החשבון נוצר בהצלחה");
                    User newUser = new User(dataObj);
                    switchToMainMenuActivity(newUser);
                }
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

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToMainMenuActivity(User currentUser) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionActivity(JSONObject newUser) {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        intent.putExtra("newParticipant", newUser.toString());
        startActivity(intent);
    }

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

}
