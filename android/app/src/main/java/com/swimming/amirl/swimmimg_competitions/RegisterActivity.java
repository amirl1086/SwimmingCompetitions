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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class RegisterActivity extends LoadingDialog implements HttpAsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText token;
    private EditText passwordConfirmation;
    private TextView dateView;
    private int year, month, day;
    private String registerType;
    private Spinner spinner;
    private Competition selectedCompetition;
    private User currentUser;
    private String firstNameText;
    private String lastNameText;
    private String tokenText;
    private String genderText;
    private String birthDateText;
    private String eMailText;
    private String passwordText;
    private String passwordConfirmationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        TextView header = findViewById(R.id.register_header);
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.registerType = "student";
            header.setText("הוספת מתחרה חדש");
        }
        else {
            header.setText("הרשמה");
            this.registerType = intent.getStringExtra("registerType");
        }
        this.spinner = findViewById(R.id.register_gender);
        this.firstName = findViewById(R.id.register_first_name);
        this.lastName = findViewById(R.id.register_last_name);
        this.email = findViewById(R.id.register_email);
        this.password = findViewById(R.id.register_password);
        this.passwordConfirmation = findViewById(R.id.register_password_confirmation);
        this.dateView = findViewById(R.id.birth_date_view);
        this.token = findViewById(R.id.register_token);
        Button birthDateButton = findViewById(R.id.register_birth_date);

        if (this.registerType.equals("parent")) {
            birthDateButton.setVisibility(View.GONE);
            this.dateView.setVisibility(View.GONE);
            this.spinner.setVisibility(View.GONE);
        }
        else { //initialize date picker for date of birth
            initParticipantUser();
        }

    }

    private void initParticipantUser() {
        String[] genders = new String[]{"בחר מגדר", "זכר", "נקבה"};

        ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders) {

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

        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        this.spinner.setAdapter(spinnerListAdapter);

        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);

        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }


    public void createFirebaseUser(View view) {
        if(isValid()) {
            try {
                showProgressDialog("נרשם למערכת...");
                JSONObject registerData = new JSONObject();
                registerData.put("urlSuffix", "/addNewUser");
                registerData.put("httpMethod", "POST");
                registerData.put("email", this.eMailText);
                registerData.put("password", this.passwordText);
                registerData.put("firstName", this.firstNameText);
                registerData.put("lastName", this.lastNameText);
                registerData.put("gender", this.genderText);
                registerData.put("birthDate", this.birthDateText);
                registerData.put("type", this.registerType);
                registerData.put("token", this.tokenText);
                if(this.currentUser != null) {
                    registerData.put("joinToCompetition", true);
                }
                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;

                this.jsonAsyncTaskPost.execute(registerData.toString());

            }
            catch (JSONException e) {
                hideProgressDialog();
                showToast("שגיאה בתהליך ההרשמה, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }

        }
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

        if (this.registerType.equals("student")) {

            this.birthDateText = dateView.getText().toString();
            if(this.selectedCompetition != null) {
                int participantAge = dateUtils.getAgeByDate(this.birthDateText);

                int competitionFromAge = Integer.valueOf(this.selectedCompetition.getFromAge());
                int competitionToAge = Integer.valueOf(this.selectedCompetition.getToAge());

                if(participantAge < competitionFromAge) {
                    this.dateView.setError("הגיל המינימלי להשתתפות הוא " + competitionFromAge);
                    return false;
                }
                else if(participantAge > competitionToAge) {
                    this.dateView.setError("הגיל המקסימלי להשתתפות הוא " + competitionToAge);
                    return false;
                }
                else {
                    this.dateView.setError(null);
                }
            }
            else {
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

        this.eMailText = this.email.getText().toString();
        if(this.eMailText.isEmpty()) {
            this.email.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.eMailText)) {
            this.email.setError("כתובת אימייל שגוייה");
            return false;
        }

        this.passwordText = this.password.getText().toString();
        if(this.passwordText.isEmpty()) {
            this.password.setError("חובה למלא סיסמא");
            return false;
        }
        if(this.passwordText.length() < 6) {
            this.password.setError("אורך סיסמא מינימלי 6 תווים");
            return false;
        }

        this.passwordConfirmationText = this.passwordConfirmation.getText().toString();
        if(this.passwordConfirmationText.isEmpty()) {
            this.passwordConfirmation.setError("חובה למלא אימות סיסמא");
            return false;
        }
        if(!this.passwordConfirmationText.equals(this.passwordText)) {
            this.passwordConfirmation.setError("סיסמא לא תואמת");
            return false;
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

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
    public void processFinish(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(response.getBoolean("success")) {
                    if(this.currentUser != null) {
                        switchToViewCompetitionActivity(dataObj);
                    }
                    else {
                        showToast("החשבון נוצר בהצלחה");
                        User newUser = new User(dataObj);
                        switchToHomePageActivity(newUser);
                    }
                }
                else {
                    if(dataObj.getString("message").equals("token_dont_match")) {
                        showToast("מפתח המוצר שהזנת שגוי, אנא פנה למאמן לקבלתו");
                    }
                }
            }
            catch (Exception e) {
                hideProgressDialog();
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בהרשמה למערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToHomePageActivity(User currentUser) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionActivity(JSONObject newUser) {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        intent.putExtra("newParticipant", newUser.toString());
        startActivity(intent);
    }

}

