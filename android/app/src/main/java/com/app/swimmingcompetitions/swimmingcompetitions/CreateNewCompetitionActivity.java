package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class CreateNewCompetitionActivity extends LoadingDialog implements AsyncResponse {

    private Competition newCompetition;
    private Competition selectedCompetition;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private Boolean isTimePickerOpen;
    private User currentUser;

    private String[] swimmingStyles = new String[]{"בחר סגנון שחייה", "חזה", "גב", "פרפר", "חתירה"};
    private ArrayAdapter spinnerListAdapter;
    private Spinner spinner;

    private EditText competitionName;
    private TextView dateView;
    private TextView timeView;

    private Calendar calendar;
    private int year, month, day, minutes, hours;
    private NumberPicker iterationLength;
    private NumberPicker numOfParticipants;
    private NumberPicker fromAge;
    private NumberPicker toAge;
    private Button addSaveCompetition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_competition);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
        }

        this.isTimePickerOpen = false;
        this.competitionName = findViewById(R.id.competition_list_item_name);

        //set up datepickers
        this.dateView = findViewById(R.id.competition_date);
        this.timeView = findViewById(R.id.competition_time);
        this.addSaveCompetition = findViewById(R.id.add_save_competition_btn);
        this.calendar = Calendar.getInstance();
        this.year = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.hours = this.calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = this.calendar.get(Calendar.MINUTE);
        //showDate(year, this.month + 1, this.day);

        //set up number pickers
        this.fromAge = findViewById(R.id.from_age);
        this.toAge = findViewById(R.id.to_age);
        this.fromAge.setMinValue(4);
        this.fromAge.setMaxValue(18);
        this.toAge.setMinValue(4);
        this.toAge.setMaxValue(18);

        this.iterationLength = findViewById(R.id.iteration_length);
        this.iterationLength.setMinValue(1);
        this.iterationLength.setMaxValue(1000);

        this.numOfParticipants = findViewById(R.id.num_of_participants);
        this.numOfParticipants.setMinValue(1);
        this.numOfParticipants.setMaxValue(12);

        //set up spinner picker for swimming style
        setupSpinner();

        if(intent.hasExtra("editMode")) {
            DateUtils dateUtils = new DateUtils();
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.competitionName.setText(this.selectedCompetition.getName());
            Calendar competitionDate = dateUtils.dateToCalendar(new Date(this.selectedCompetition.getActivityDate()));
            showDate(competitionDate.get(Calendar.YEAR), competitionDate.get(Calendar.MONTH) + 1, competitionDate.get(Calendar.DAY_OF_MONTH));
            showTime(competitionDate.get(Calendar.HOUR_OF_DAY), competitionDate.get(Calendar.MINUTE));
            this.fromAge.setValue(Integer.valueOf(this.selectedCompetition.getFromAge()));
            this.toAge.setValue(Integer.valueOf(this.selectedCompetition.getToAge()));
            this.iterationLength.setValue(Integer.valueOf(this.selectedCompetition.getLength()));
            this.numOfParticipants.setValue(this.selectedCompetition.getNumOfParticipants());

            String swimmingStyle = this.selectedCompetition.getSwimmingStyle();
            for(int i = 0; i < this.swimmingStyles.length; i++) {
                if(this.swimmingStyles[i].equals(swimmingStyle)) {
                    this.spinner.setSelection(i);
                    break;
                }
            }

            this.addSaveCompetition.setText("שמור שינויים");
        }
    }

    private void setupSpinner() {
        this.spinner = findViewById(R.id.swimming_style_spinner);

        this.spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, swimmingStyles) {

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
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            if(!isTimePickerOpen) {
                showDate(year, month + 1, day);
                showTimePicker();
            }
        }
    };

    private void showTimePicker() {
        this.isTimePickerOpen = true;
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                    showTime(hourOfDay, minutes);
                    isTimePickerOpen = false;
                }
            }, this.hours, this.minutes, false);
        timePickerDialog.show();
    }

    private void showDate(int year, int month, int day) {
        this.dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    private void showTime(int hourOfDay, int minutes) {
        this.timeView.setText(new StringBuilder().append(hourOfDay).append(":").append(minutes));
    }

    public void addNewCompetition(View view) {
        DateUtils dateUtils = new DateUtils();
        String competitionNameText = this.competitionName.getText().toString();
        String activityDateText = this.dateView.getText().toString();
        String activityTimeText = this.timeView.getText().toString();
        String swimmingStyleText = this.spinner.getSelectedItem().toString();
        Date selectedDatetime = dateUtils.createNewDate(activityDateText, activityTimeText);

        int numOfParticipantsNum = this.numOfParticipants.getValue();
        int iterationLengthNum = this.iterationLength.getValue();
        int fromAge = this.fromAge.getValue();
        int toAge = this.toAge.getValue();

        String id = "";
        if(this.selectedCompetition != null) {
            id = this.selectedCompetition.getId();
        }
        this.newCompetition = new Competition(id, competitionNameText, selectedDatetime.toString(), swimmingStyleText, numOfParticipantsNum, fromAge, toAge, iterationLengthNum);
        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject data = null;

        showProgressDialog("שומר תחרות...");
        //set up action params
        try {
            data = this.newCompetition.getJSON_Object();
            data.put("urlSuffix", "/setNewCompetition");
            data.put("httpMethod", "POST");
        } catch (JSONException e) {
            showToast("LogInActivity firebaseLogIn: Error creating JSONObject");
        }

        //call the server
        jsonAsyncTaskPost.execute(data.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                String id = dataObj.getString("id");
                this.newCompetition.setId(id);

                switchToViewCompetitionsActivity();
            }
            else {
                showToast("CreateNewCompetitionActivity processFinish: Error saving competition");
            }
            hideProgressDialog();
        } catch (JSONException e) {
            showToast("LogInActivity processFinish: Error parsing JSONObject");
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void switchToViewCompetitionsActivity() {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }
}
