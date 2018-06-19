package com.swimming.amirl.swimmimg_competitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class GoogleRegisterActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private EditText firstName;
    private EditText token;
    private String tokenText;
    private EditText lastName;
    private TextView dateView;
    private int year, month, day;
    private String registerType;
    private Spinner spinner;
    private String firstNameText;
    private String lastNameText;
    private String genderText;
    private String birthDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_register);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");

                this.spinner = findViewById(R.id.register_gender);
                this.firstName = findViewById(R.id.register_first_name);
                this.firstName.setText(this.currentUser.getFirstName());
                this.lastName = findViewById(R.id.register_last_name);
                this.lastName.setText(this.currentUser.getLastName());
                this.registerType = intent.getStringExtra("registerType");
                this.token = findViewById(R.id.register_token);
                this.dateView = findViewById(R.id.birth_date_view);
                Button birthDateButton = findViewById(R.id.register_birth_date);

                if (this.registerType.equals("parent")) {
                    birthDateButton.setVisibility(View.GONE);
                    this.spinner.setVisibility(View.GONE);
                    this.dateView.setVisibility(View.GONE);
                }
                else {
                    initParticipantUser();
                }
            }
            catch(Exception e) {
                showToast("שגיאה ביצירת עמוד ההרשמה של גוגל, נסה לאתחל את האפליקציה");
                System.out.println("GoogleRegisterActivity onCreate Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            switchToLogInActivity();
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


        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);

        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                this.currentUser = new User(dataObj);
                switchToMainMenuActivity();
            }
            else {
                showToast("שגיאה בהרשמה למערכת, נסה לאתחל את האפליקציה");
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
            System.out.println("GoogleRegisterActivity processFinish Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }

        hideProgressDialog();
    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void updateFirebaseUser(View view) {
        if(isValid()) {
            JSONObject registerData = new JSONObject();
            try {
                registerData.put("urlSuffix", "/updateFirebaseUser");
                registerData.put("httpMethod", "POST");
                registerData.put("uid", this.currentUser.getUid());
                registerData.put("firstName", this.firstNameText);
                registerData.put("lastName", this.lastNameText);
                registerData.put("token", this.tokenText);
                registerData.put("gender", this.genderText);
                registerData.put("birthDate", this.birthDateText);
                registerData.put("type", this.registerType);

                if(this.currentUser != null) {
                    registerData.put("joinToCompetition", true);
                }

                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;

                showProgressDialog("נרשם למערכת...");

                this.jsonAsyncTaskPost.execute(registerData.toString());
            }
            catch (JSONException e) {
                showToast("שגיאה בתהליך שמירת הפרטים, נסה לאתחל את האפליקציה");
                System.out.println("GoogleRegisterActivity updateFirebaseUser Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {
        this.firstNameText = this.firstName.getText().toString();
        this.lastNameText = this.lastName.getText().toString();

        if (this.registerType.equals("student")) {
            this.birthDateText = dateView.getText().toString();
            this.genderText = spinner.getSelectedItem().toString();
        }
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

        if (this.registerType.equals("student")) {

            this.birthDateText = dateView.getText().toString();
            int participantAge = dateUtils.getAgeByDate(this.birthDateText);
            if(participantAge < 4) {
                this.dateView.setError("הגיל המינימלי להשתתפות הוא 4");
                return false;
            }
            else if(participantAge > 18) {
                this.dateView.setError("הגיל המקסימלי להשתתפות הוא 18");
                return false;
            }
            else {
                this.dateView.setError(null);
            }

            this.genderText = this.spinner.getSelectedItem().toString();
            if(this.genderText.equals("בחר מגדר")) {
                TextView errorText = (TextView) this.spinner.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("חובה לבחור מגדר");
                return false;
            }
            this.genderText = this.spinner.getSelectedItem().toString().equals("זכר") ? "male" : "female";
        }

        this.tokenText = this.token.getText().toString();
        if(this.tokenText.isEmpty()) {
            this.token.setError("חובה למלא מפתח מוצר, פנה למאמן לקבלתו");
            return false;
        }
        if(this.tokenText.length() != 12) {
            this.token.setError("אורך מפתח המוצר חייב להיות 12 תווים");
            return false;
        }

        return true;
    }


    public void setDate(View view) {
        showDialog(R.integer.dialog_id);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showDate(int year, int month, int day) {
        StringBuilder str = new StringBuilder();

        if(day < 10) {
            str.append("0");
        }
        str.append(day);
        str.append("/");

        if(month < 10) {
            str.append("0");
        }
        str.append(month);
        str.append("/");

        str.append(year);

        this.dateView.setText(str);
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
}
