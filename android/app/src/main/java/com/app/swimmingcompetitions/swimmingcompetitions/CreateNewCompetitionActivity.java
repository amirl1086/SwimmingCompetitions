package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class CreateNewCompetitionActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private Competition newCompetition;
    private Competition selectedCompetition;
    private Boolean isTimePickerOpen;

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
    private NumberPicker fromAgePicker;
    private NumberPicker toAgePicker;
    private Button addSaveCompetition;

    private String competitionNameText;
    private String activityDateText;
    private String activityTimeText;
    private String swimmingStyleText;
    private int numOfParticipantsNum;
    private int iterationLengthNum;
    private int fromAge;
    private int toAge;
    private String activityDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_competition);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            bindView();
            setupSpinner();

            if(intent.hasExtra("editMode")) {
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
                setUpEditMode();
            }

            setUpSidebar();
        }
        else {
            switchToLogInActivity();
        }

    }

    public void addNewCompetition(View view) {
        if(isValid()) {
            try {
                showProgressDialog("שומר את פרטי התחרות...");

                JSONObject data = new JSONObject();
                data.put("urlSuffix", "/setNewCompetition");
                data.put("httpMethod", "POST");
                data.put("name", this.competitionNameText);
                data.put("activityDate", this.activityDate);
                data.put("numOfParticipants", this.numOfParticipantsNum);
                data.put("length", this.iterationLengthNum);
                data.put("swimmingStyle", this.swimmingStyleText);
                data.put("fromAge", String.valueOf(this.fromAge));
                data.put("toAge", String.valueOf(this.toAge));
                if(this.selectedCompetition != null) {
                    data.put("id", this.selectedCompetition.getId());
                }

                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;
                this.jsonAsyncTaskPost.execute(data.toString());
            }
            catch(Exception e) {
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
                System.out.println("MyPersonalInformationActivity Exception " + e.getStackTrace());
            }
        }
    }

    private boolean isValid() {
        DateUtils dateUtils = new DateUtils();
        this.numOfParticipantsNum = this.numOfParticipants.getValue();
        this.iterationLengthNum = this.iterationLength.getValue();

        this.competitionNameText = this.competitionName.getText().toString();
        if(this.competitionNameText.isEmpty()) {
            this.competitionName.setError("חובה למלא את שם התחרות");
            return false;
        }

        this.activityDateText = this.dateView.getText().toString();
        this.activityTimeText = this.timeView.getText().toString();
        if(this.activityDateText.isEmpty() || this.activityTimeText.isEmpty()) {
            this.dateView.setError("חובה לבחור תאריך ושעה");
            this.timeView.setError("חובה לבחור שעה");
            return false;
        }

        this.activityDate = this.activityDateText + " " + this.activityTimeText;
        Calendar calendar = dateUtils.stringToCalendar(this.activityDate);
        Calendar today = Calendar.getInstance();
        if(calendar.getTimeInMillis() < today.getTimeInMillis()) {
            this.timeView.setError("תאריך שגוי");
            return false;
        }


        this.swimmingStyleText = this.spinner.getSelectedItem().toString();
        if(this.swimmingStyleText.equals("בחר סגנון שחייה")) {
            TextView errorText = (TextView) this.spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("חובה לבחור סגנון שחייה");
            return false;
        }

        this.fromAge = this.fromAgePicker.getValue();
        this.toAge = this.toAgePicker.getValue();

/*
        this.newCompetition = new Competition(id, this.competitionNameText, activityDateText + " " + activityTimeText ,
                swimmingStyleText, numOfParticipantsNum, this.fromAge, this.toAge, iterationLengthNum);*/

        return true;
    }

    private void setUpEditMode() {
        DateUtils dateUtils = new DateUtils();

        Calendar competitionDate = dateUtils.dateToCalendar(new Date(this.selectedCompetition.getActivityDate()));
        showDate(competitionDate.get(Calendar.YEAR), competitionDate.get(Calendar.MONTH) + 1, competitionDate.get(Calendar.DAY_OF_MONTH));
        showTime(competitionDate.get(Calendar.HOUR_OF_DAY), competitionDate.get(Calendar.MINUTE));

        this.competitionName.setText(this.selectedCompetition.getName());
        this.fromAgePicker.setValue(Integer.valueOf(this.selectedCompetition.getFromAge()));
        this.toAgePicker.setValue(Integer.valueOf(this.selectedCompetition.getToAge()));
        this.iterationLength.setValue(Integer.valueOf(this.selectedCompetition.getLength()));
        this.numOfParticipants.setValue(this.selectedCompetition.getNumOfParticipants());

        String swimmingStyle = this.selectedCompetition.getSwimmingStyle();
        for(int i = 0; i < this.swimmingStyles.length; i++) {
            if(this.swimmingStyles[i].equals(swimmingStyle)) {
                this.spinner.setSelection(i);
                break;
            }
        }

        this.addSaveCompetition.setText("אישור");
    }

    private void bindView() {
        this.competitionName = findViewById(R.id.competition_list_item_name);

        //set up datepickers
        this.isTimePickerOpen = false;
        this.dateView = findViewById(R.id.competition_date);
        this.timeView = findViewById(R.id.competition_time);
        this.addSaveCompetition = findViewById(R.id.add_save_competition_btn);
        this.calendar = Calendar.getInstance();
        this.year = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.hours = this.calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = this.calendar.get(Calendar.MINUTE);

        //set up number pickers
        this.fromAgePicker = findViewById(R.id.from_age);
        this.toAgePicker = findViewById(R.id.to_age);

        this.fromAgePicker.setMinValue(4);
        this.fromAgePicker.setMaxValue(18);

        this.fromAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                toAgePicker.setMinValue(newVal);
            }
        });

        this.toAgePicker.setMinValue(4);
        this.toAgePicker.setMaxValue(18);

        this.iterationLength = findViewById(R.id.iteration_length);
        this.iterationLength.setMinValue(1);
        this.iterationLength.setMaxValue(1000);

        this.numOfParticipants = findViewById(R.id.num_of_participants);
        this.numOfParticipants.setMinValue(1);
        this.numOfParticipants.setMaxValue(12);
    }


    private void setupSpinner() {
        this.spinner = findViewById(R.id.swimming_style_spinner);
        this.spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, swimmingStyles) {

            @Override
            public boolean isEnabled(int position){
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        this.spinnerListAdapter.setDropDownViewResource(R.layout.spinner_item);
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
                    dateView.setError(null);
                    timeView.setError(null);
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

    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                showToast("התחרות נשמרה בהצלחה");
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                this.selectedCompetition = new Competition(dataObj);

                switchToViewCompetitionsActivity();
                hideProgressDialog();
            }
            catch (Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("RegisterActivity Exception " + e.getLocalizedMessage() + ", " + e.getMessage());
            }
        }
        else {
            showToast("שגיאה בשמירת התחרות במערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.back_to_home: {
                switchToHomePageActivity();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if(this.selectedCompetition != null) {
            toolbar.setTitle("ערוך את פרטי התחרות");
        }
        else {
            toolbar.setTitle("צור תחרות חדשה");
        }
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        if(this.currentUser.getType().equals("parent") || this.currentUser.getType().equals("coach")) {
            this.navigationView.inflateMenu(R.menu.parent_home_side_bar_menu);
        }
        else if(this.currentUser.getType().equals("student")) {
            this.navigationView.inflateMenu(R.menu.student_home_side_bar_menu);
        }

        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.competitions_nav_item: {
                        switchToViewCompetitionsActivity();
                        break;
                    }
                    case R.id.personal_results_nav_item: {
                        switchToViewResultsActivity();
                        break;
                    }
                    case R.id.statistics_nav_item: {
                        switchToViewStatisticsActivity();
                        break;
                    }
                    case R.id.real_time_nav_item: {
                        switchToViewInRealTimeActivity();
                        break;
                    }
                    case R.id.my_personal_info_nav_item: {
                        switchToMyPersonalInformationActivity();
                        break;
                    }
                    case R.id.my_children_nav_item: {
                        switchToMyChildrenActivity();
                        break;
                    }
                    case R.id.change_email_nav_item: {
                        switchToChangeEmailActivity();
                        break;
                    }
                    case R.id.change_password_nav_item: {
                        switchToChangePasswordActivity();
                        break;
                    }
                    case R.id.log_out_nav_item: {
                        logOut();
                        break;
                    }
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    private void switchToViewInRealTimeActivity() {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void switchToViewStatisticsActivity() {
        Intent intent = new Intent(this, ViewStatisticsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionsActivity() {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewResultsActivity() {
        Intent intent = new Intent(this, ViewPersonalResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMyPersonalInformationActivity() {
        Intent intent = new Intent(this, MyPersonalInformationActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMyChildrenActivity() {
        Intent intent = new Intent(this, MyChildrenActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToChangePasswordActivity() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToChangeEmailActivity() {
        Intent intent = new Intent(this, ChangeEmailActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void logOut() {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}
