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

import java.util.Calendar;
import java.util.Date;

public class MyPersonalInformationActivity extends AppCompatActivity {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private EditText firstName;
    private EditText lastName;
    private TextView dateView;
    private int year, month, day;
    private String registerType;
    private Spinner genderSpinner;
    private Spinner typeSpinner;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_personal_information);

        Intent intent = getIntent();
        if(!intent.hasExtra("currentUser")) {
            switchToLogInActivity();
        }

        this.currentUser = (User) intent.getSerializableExtra("currentUser");
        this.firstName = findViewById(R.id.edit_first_name);
        this.lastName = findViewById(R.id.edit_last_name);
        this.genderSpinner = findViewById(R.id.edit_gender);
        this.typeSpinner = findViewById(R.id.edit_type);
        this.dateView = findViewById(R.id.birth_date_view);
        Button birthDateButton = findViewById(R.id.edit_birth_date);

        if(this.currentUser.getType().equals("parent")) {
            birthDateButton.setVisibility(View.GONE);
            this.genderSpinner.setVisibility(View.GONE);
            this.dateView.setVisibility(View.GONE);
        }
        else if(this.currentUser.getType().equals("student")) {

        }


        if (this.currentUser.getType().equals("parent")) {

        }
        else { //initialize date picker for date of birth
            initParticipantUser();
        }

        this.firstName.setText(this.currentUser.getFirstName());
        this.lastName.setText(this.currentUser.getLastName());
        this.dateView.setText(this.currentUser.getBirthDate());
        /*competitionName.setText(selectedCompetition.getName());
        time.setText(dateUtils.getTime(calendar));
        distance.setText(selectedCompetition.getLength());
        style.setText(selectedCompetition.getSwimmingStyle());
        ages.setText(selectedCompetition.getAgesString());
        participantsForIteration.setText(String.valueOf(selectedCompetition.getNumOfParticipants()));*/

    }

    private void initParticipantUser() {
        String[] genders = new String[]{"בחר מין", "זכר", "נקבה"};
        String[] types = new String[]{"בחר סוג רישום", "חניך", "הורה"};

        ArrayAdapter<String> genderSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

            @Override
            public boolean isEnabled(int position) {
                return !(position == 0);
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
        ArrayAdapter<String> typeSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types) {

            @Override
            public boolean isEnabled(int position) {
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0) {
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        genderSpinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        typeSpinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        this.genderSpinner.setAdapter(genderSpinnerListAdapter);
        this.typeSpinner.setAdapter(typeSpinnerListAdapter);

        this.dateView = findViewById(R.id.birth_date_view);
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);

        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    public void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void updateUserDetails(View view) {

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

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }
}
