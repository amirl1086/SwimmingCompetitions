package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    private int year, month, day;
    private NumberPicker iterationLength;
    private NumberPicker numOfParticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_competition);

        competitionName = (EditText) findViewById(R.id.competition_name);

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
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public void addNewCompetition(View view) {
        String competitionNameText = competitionName.getText().toString();
        String activityDateText = dateView.getText().toString();
        String swimmingStyleText = spinner.getSelectedItem().toString();
        int numOfParticipantsNum = numOfParticipants.getValue();
        int iterationLengthNum = iterationLength.getValue();

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject data = new JSONObject();

        //set up action params
        try {
            data.put("urlSuffix", "/setNewCompetition");
            data.put("httpMethod", "POST");
            data.put("name", competitionNameText);
            data.put("activityDate", activityDateText);
            data.put("swimmingStyle", swimmingStyleText);
            data.put("numOfParticipants", numOfParticipantsNum);
            data.put("length", iterationLengthNum);
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
                String name = dataObj.getString("name");
                String activityDate = dataObj.getString("activityDate");
                String swimmingStyle = dataObj.getString("swimmingStyle");
                Integer numOfParticipants = dataObj.getInt("numOfParticipants");
                Integer length = dataObj.getInt("length");

                newCompetition = new Competition(id, name, activityDate, swimmingStyle, numOfParticipants, length);

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
        intent.putExtra("newCompetition", newCompetition);
        startActivity(intent);
    }
}
