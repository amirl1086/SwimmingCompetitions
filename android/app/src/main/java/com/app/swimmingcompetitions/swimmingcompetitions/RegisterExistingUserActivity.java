package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

public class RegisterExistingUserActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
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

        this.emailText = this.email.getText().toString();
        if(this.emailText.isEmpty()) {
            this.email.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.emailText)) {
            this.email.setError("כתובת אימייל שגוייה");
            return false;
        }

        this.birthDateText = this.dateView.getText().toString();
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
                    showToast("התאמה נמצאה, ההרשמה בוצעה בהצלחה");
                    switchToViewCompetitionActivity(dataObj);
                }
                else {
                    if(dataObj.getString("message").equals("birth_date_dont_match")) {
                        showToast("התאמה לא נמצאה, בדוק את תאריך הלידה");
                    }
                    else if(dataObj.getString("message").equals("no_such_email")) {
                        showToast("התאמה לא נמצאה, בדוק את כתובת האימייל");
                    }
                }
            }
            catch(Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            showToast("שגיאה בהרשמת המשתמש במערכת, נסה לאתחל את האפליקציה");
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

    public void switchToViewCompetitionActivity(JSONObject newParticipant) {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        intent.putExtra("newParticipant", newParticipant.toString());
        startActivity(intent);
    }

}
