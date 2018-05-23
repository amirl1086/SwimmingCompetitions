package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class MyPersonalInformationActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private String registerType;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private EditText firstName;
    private EditText lastName;

    private TextView dateView;
    private int year, month, day;

    private Spinner genderSpinner;
    private Spinner typeSpinner;

    private String genderText;
    private String typeText;
    private String firstNameText;
    private String lastNameText;
    private String birthDateText;

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
                initParticipantUser();
            }

            this.firstName.setText(this.currentUser.getFirstName());
            this.lastName.setText(this.currentUser.getLastName());
            this.dateView.setText(this.currentUser.getBirthDate());
        }
        else {
            switchToLogInActivity();
        }
    }

    private void initParticipantUser() {
        String[] genders = new String[]{"בחר מגדר", "זכר", "נקבה"};
        String[] types = new String[]{"בחר סוג משתמש", "חניך", "הורה"};

        ArrayAdapter<String> genderSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

            @Override
            public boolean isEnabled(int position) {
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
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
                tv.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
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

    public void updateUserDetails(View view) {
        if(isValid()) {
            try {
                JSONObject data = new JSONObject();
                data.put("urlSuffix", "/updateUserDetails");
                data.put("httpMethod", "POST");
                data.put("uid", this.currentUser.getUid());
                data.put("firstName", this.firstNameText);
                data.put("lastName", this.lastNameText);
                data.put("birthDate", this.birthDateText);
                data.put("gender", this.genderText);
                data.put("type", this.typeText);

                showProgressDialog("טוען תחרויות...");

                JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
                jsonAsyncTaskPost.delegate = this;
                jsonAsyncTaskPost.execute(data.toString());
            }
            catch (Exception e) {
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


            }
            catch (Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("MyPersonalInformationActivity Exception " + e.getStackTrace());
            }
        }
        else {
            showToast("שגיאה בשמירת הפרטים במערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private boolean isValid() {
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

        if (this.registerType.equals("student")) {

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
        toolbar.setTitle("בחר את סוג המתחרה");
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
            public boolean onNavigationItemSelected(MenuItem menuItem) {
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
