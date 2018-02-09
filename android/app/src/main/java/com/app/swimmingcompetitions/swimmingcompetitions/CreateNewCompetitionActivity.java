package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CreateNewCompetitionActivity extends AppCompatActivity implements AsyncResponse {

    private Competition newCompetition;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private String[] swimmingStyles;
    private ArrayAdapter spinnerListAdapter;
    private Spinner spinner;

    private EditText competitionName;
    private TextView dateView;

    private Calendar calendar;
    private int year, month, day, minutes, hours;
    private NumberPicker iterationLength;
    private NumberPicker numOfParticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_competition);

        competitionName = (EditText) findViewById(R.id.competition_list_item_name);

        //set up datepickers
        dateView = (TextView) findViewById(R.id.competition_date);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        //set up number pickers
        iterationLength = findViewById(R.id.iteration_length);
        iterationLength.setMinValue(1);
        iterationLength.setMaxValue(1000);

        numOfParticipants = findViewById(R.id.num_of_participants);
        numOfParticipants.setMinValue(1);
        numOfParticipants.setMaxValue(1000);

        //set up spinner picker
        swimmingStyles = new String[]{"בחר סגנון שחייה", "חזה", "גב", "פרפר", "חתירה"};
        spinner = (Spinner) findViewById(R.id.swimming_style_spinner);
        spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, swimmingStyles);

        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerListAdapter);
    }

    public void setDate(View view) {
        showDialog(R.integer.dialog_id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.integer.dialog_id) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
            showTimePicker();
        }
    };

    private void showTimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        this.hours = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        this.hours = hourOfDay;
                        this.minutes = minute;

                        et_show_date_time.setText(date_time+" "+hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public void addNewCompetition(View view) {
        String competitionNameText = competitionName.getText().toString();
        String activityDateText = dateView.getText().toString();
        String swimmingStyleText = spinner.getSelectedItem().toString();
        int numOfParticipantsNum = numOfParticipants.getValue();
        int iterationLengthNum = iterationLength.getValue();

        this.newCompetition = new Competition("", competitionNameText, activityDateText, swimmingStyleText, numOfParticipantsNum, 5, 8, iterationLengthNum);
        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject data = null;

        //set up action params
        try {
            data = this.newCompetition.getJSON_Object();
        } catch (JSONException e) {
            showToast("LogInActivity firebaseLogIn: Error creating JSONObject");
        }

        //call the server
        jsonAsyncTaskPost.execute(data.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                String id = dataObj.getString("id");
                this.newCompetition.setId(id);

                switchToViewCompetitionsActivity();
            } else {
                showToast("LogInActivity processFinish: Error loging in");
            }
        } catch (JSONException e) {
            showToast("LogInActivity processFinish: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToViewCompetitionsActivity() {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("newCompetition", this.newCompetition);
        startActivity(intent);
    }
}
