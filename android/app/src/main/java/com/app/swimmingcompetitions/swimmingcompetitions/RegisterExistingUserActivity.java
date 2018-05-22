package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterExistingUserActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Competition selectedCompetition;
    private TextView dateView;
    private EditText email;
    private int year, month, day;
    private String birthDateText;
    private String emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_existing_user);

        Intent intent = getIntent();

        if (intent.hasExtra("currentUser")  && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            this.email = findViewById(R.id.child_email);
            this.dateView = findViewById(R.id.birth_date_view);
            Calendar calendar = Calendar.getInstance();
            this.year = calendar.get(Calendar.YEAR);

            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
        }
        else {
            switchToLogInActivity();
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void addExistingUserToCompetition(View view) {
        if(isValid()) {
            JSONObject registerData = new JSONObject();

            try {
                registerData.put("urlSuffix", "/addExistingUserToCompetition");
                registerData.put("httpMethod", "POST");
                registerData.put("competitionId", this.selectedCompetition.getId());
                registerData.put("email", this.emailText);
                registerData.put("birthDate", this.birthDateText);
            }
            catch (JSONException e) {
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
            }

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;

            showProgressDialog("מאמת את המידע...");

            this.jsonAsyncTaskPost.execute(registerData.toString());
        }
    }

    public Boolean isValid() {
        DateUtils dateUtils = new DateUtils();

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

        this.emailText = this.email.getText().toString();
        if(this.emailText.isEmpty()) {
            this.email.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.emailText)) {
            this.email.setError("כתובת אימייל שגוייה");
            return false;
        }
        return true;
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
    public void processFinish(String result) {
        try {
            if(result == null) {
                showToast("התאמה נמצאה, ההרשמה בוצעה בהצלחה");
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");
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

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

}