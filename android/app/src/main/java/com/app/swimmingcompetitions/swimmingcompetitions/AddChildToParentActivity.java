package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddChildToParentActivity extends LoadingDialog implements AsyncResponse {

    private TextView dateView;
    private EditText email;
    private int year, month, day;
    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_to_parent);
        Intent intent = getIntent();

        if(!intent.hasExtra("currentUser")) {
            switchToLogInActivity();
        }
        this.currentUser = (User) intent.getSerializableExtra("currentUser");

        this.email = findViewById(R.id.child_email);
        this.dateView = findViewById(R.id.birth_date_view);
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

    public void addChildToParent(View view) {
        String birthDateText = this.dateView.getText().toString();
        String eMailText = this.email.getText().toString();

        JSONObject registerData = new JSONObject();

        try {
            registerData.put("urlSuffix", "/addChildToParent");
            registerData.put("httpMethod", "POST");
            registerData.put("uid", this.currentUser.getUid());
            registerData.put("email", eMailText);
            registerData.put("birthDate", birthDateText);
        }
        catch (JSONException e) {
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
        }

        this.jsonAsyncTaskPost = new JSON_AsyncTask();
        this.jsonAsyncTaskPost.delegate = this;

        showProgressDialog("מאמת את המידע...");

        this.jsonAsyncTaskPost.execute(registerData.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                showToast("התאמה נמצאה, הפעולה הושלמה בהצלחה");
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
}
