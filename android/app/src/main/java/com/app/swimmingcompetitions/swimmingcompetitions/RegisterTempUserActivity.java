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

public class RegisterTempUserActivity extends LoadingDialog implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private EditText firstName;
    private EditText lastName;
    private EditText gender;
    private Button birthDateButton;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private Competition selectedCompetition;
    private User currentUser;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerListAdapter;
    private String[] genders;
    private String firstNameText;
    private String lastNameText;
    private String birthDateText;
    private String genderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_temp_user);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            //set up spinner picker for swimming style
            this.genders = new String[]{"בחר מגדר", "זכר", "נקבה"};
            this.spinner = findViewById(R.id.temp_register_gender);
            this.spinnerListAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, genders) {

                @Override
                public boolean isEnabled(int position){
                    return position != 0;
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

            this.firstName = findViewById(R.id.temp_register_first_name);
            this.lastName = findViewById(R.id.temp_register_last_name);
            this.birthDateButton = findViewById(R.id.temp_register_birth_date);

            this.dateView = findViewById(R.id.temp_birth_date_view);
            this.calendar = Calendar.getInstance();
            this.year = calendar.get(Calendar.YEAR);

            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
        }
        else {
            switchToLogInActivity();
        }

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

    public void registerTempUser(View view) {
        if(isValid()) {
            JSONObject registerData = new JSONObject();

            try {
                registerData.put("urlSuffix", "/joinToCompetition");
                registerData.put("httpMethod", "POST");
                registerData.put("firstName", this.firstNameText);
                registerData.put("lastName", this.lastNameText);
                registerData.put("gender", this.genderText);
                registerData.put("birthDate", this.birthDateText);
                registerData.put("competitionId", this.selectedCompetition.getId());
            }
            catch (JSONException e) {
                showToast("RegisterTempUserActivity registerTempUser: Error creating JSONObject");
            }

            showProgressDialog("מוסיף מתחרה חדש...");

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;

            this.jsonAsyncTaskPost.execute(registerData.toString());
        }
    }

    public Boolean isValid() {
        DateUtils dateUtils = new DateUtils();
        this.firstNameText = this.firstName.getText().toString();
        if(this.firstNameText.isEmpty()) {
            this.firstName.setError("חובה למלא שם פרטי");
            return false;
        }

        this.lastNameText = this.lastName.getText().toString();
        if(this.lastNameText.isEmpty()) {
            this.lastName.setError("חובה למלא שם משפחה");
            return false;
        }
        this.birthDateText = this.dateView.getText().toString();
        int participantAge = dateUtils.getAgeByDate(this.birthDateText);

        int competitionFromAge = Integer.valueOf(this.selectedCompetition.getFromAge());
        int competitionToAge = Integer.valueOf(this.selectedCompetition.getToAge());

        if(participantAge < competitionFromAge) {
            showToast("הגיל המינימלי להשתתפות הוא " + competitionFromAge);
            return false;
        }
        if(participantAge > competitionToAge) {
            showToast("הגיל המקסימלי להשתתפות הוא " + competitionToAge);
            return false;
        }
        String selectedOptions = this.spinner.getSelectedItem().toString();
        if(selectedOptions.equals("בחר מגדר")) {
            showToast("חובה לבחור מגדר");
            return false;
        }
        this.genderText = this.spinner.getSelectedItem().toString().equals("זכר") ? "male" : "female";
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONObject dataObj = response.getJSONObject("data");
            if (response.getBoolean("success")) {
                hideProgressDialog();
                switchToViewCompetitionActivity(dataObj);
            }
            else {
                showToast("RegisterTempUserActivity processFinish: Error registering temporary user");
            }
        } catch (JSONException e) {
            showToast("RegisterTempUserActivity processFinish: Error parsing JSONObject");
        }
    }

    public void switchToViewCompetitionActivity(JSONObject newParticipant) {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        intent.putExtra("newParticipant", newParticipant.toString());
        startActivity(intent);
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
