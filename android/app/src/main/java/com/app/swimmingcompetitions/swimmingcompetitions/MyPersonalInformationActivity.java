package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.Calendar;

public class MyPersonalInformationActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private EditText firstName;
    private EditText lastName;

    private TextView dateView;
    private int year, month, day;

    private Spinner genderSpinner;
    private String genderText;
    private String firstNameText;
    private String lastNameText;
    private String birthDateText;
    private String[] genders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_personal_information);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            setUpSidebar();
            bindView();
        }
        else {
            switchToLogInActivity();
        }
    }

    private void bindView() {
        this.firstName = findViewById(R.id.edit_first_name);
        this.lastName = findViewById(R.id.edit_last_name);
        this.genderSpinner = findViewById(R.id.edit_gender);
        this.dateView = findViewById(R.id.birth_date_view);
        Button birthDateButton = findViewById(R.id.edit_birth_date);

        this.genders = new String[]{"בחר מגדר", "זכר", "נקבה"};

        if(!this.currentUser.getType().equals("student")) {
            birthDateButton.setVisibility(View.GONE);
            this.genderSpinner.setVisibility(View.GONE);
            this.dateView.setVisibility(View.GONE);
        }
        else {
            initParticipantUser();
        }

        this.firstName.setText(this.currentUser.getFirstName());
        this.lastName.setText(this.currentUser.getLastName());
        this.dateView.setText(this.currentUser.getBirthDate());

        String gender = this.currentUser.getGender().equals("male") ? "זכר" : "נקבה";
        for(int i = 0; i < this.genders.length; i++) {
            if(this.genders[i].equals(gender)) {
                this.genderSpinner.setSelection(i);
                break;
            }
        }
    }

    private void initParticipantUser() {
        ArrayAdapter<String> genderSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

            @Override
            public boolean isEnabled(int position) {
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

        genderSpinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        this.genderSpinner.setAdapter(genderSpinnerListAdapter);

        this.dateView = findViewById(R.id.birth_date_view);
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);

        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    public void updateUserDetails(View view) {
        if(isValid()) {
            try {
                showProgressDialog("מעדכן את הפרטים שלך...");

                JSONObject data = new JSONObject();
                data.put("urlSuffix", "/updateFirebaseUser");
                data.put("httpMethod", "POST");
                data.put("uid", this.currentUser.getUid());
                data.put("type", this.currentUser.getType());
                data.put("firstName", this.firstNameText);
                data.put("lastName", this.lastNameText);
                data.put("birthDate", this.birthDateText);
                data.put("gender", this.genderText);


                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;
                this.jsonAsyncTaskPost.execute(data.toString());
            }
            catch (Exception e) {
                hideProgressDialog();
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
                System.out.println("MyPersonalInformationActivity Exception " + e.getStackTrace());
            }
        }
    }

    @Override
    public void processFinish(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                this.currentUser = new User(dataObj);
                showToast("הפרטים עודכנו בהצלחה");
                switchToHomePageActivity();
            }
            catch (Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("MyPersonalInformationActivity Exception " + e.getStackTrace());
            }
        }
        else {
            showToast("שגיאה בעדכון הפרטים במערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private boolean isValid() {
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

        this.genderText = "";
        this.birthDateText = "";

        if (this.currentUser.getType().equals("student")) {

            this.birthDateText = dateView.getText().toString();
            int participantAge = dateUtils.getAgeByDate(this.birthDateText);
            if(participantAge < 4) {
                this.dateView.setError("הגיל המינימלי להשתתפות הוא 4");
                return false;
            }
            else if(participantAge > 18) {
                this.dateView.setError("הגיל המינימלי להשתתפות הוא 18");
                return false;
            }
            else {
                this.dateView.setError(null);
            }

            this.genderText = this.genderSpinner.getSelectedItem().toString();
            if(this.genderText.equals("בחר מגדר")) {
                TextView errorText = (TextView) this.genderSpinner.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("חובה לבחור מגדר");
                return false;
            }
            this.genderText = this.genderSpinner.getSelectedItem().toString().equals("זכר") ? "male" : "female";
        }

        return true;
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        toolbar.setTitle("שנה את הפרטים האישיים שלך");
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
                    case R.id.media_nav_item: {
                        switchToViewMediaActivity();
                        break;
                    }
                    case R.id.settings_nav_item: {
                        switchToMySettingsActivity();
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

    public void switchToViewInRealTimeActivity() {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMySettingsActivity() {
        Intent intent = new Intent(this, MySettingsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewMediaActivity() {
        Intent intent = new Intent(this, ViewMediaActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewStatisticsActivity() {
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

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void logOut() {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}
