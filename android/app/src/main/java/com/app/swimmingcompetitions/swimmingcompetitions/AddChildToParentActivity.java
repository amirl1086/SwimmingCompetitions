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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;


public class AddChildToParentActivity extends LoadingDialog implements HttpAsyncResponse {

    private TextView dateView;
    private EditText email;
    private int year, month, day;
    private User currentUser;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private String currentCallout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_to_parent);
        Intent intent = getIntent();

        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");

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

    public void addChildToParent(View view) {
        String birthDateText = this.dateView.getText().toString();
        String eMailText = this.email.getText().toString();

        try {
            showProgressDialog("מאמת את המידע...");

            JSONObject registerData = new JSONObject();
            registerData.put("urlSuffix", "/addChildToParent");
            registerData.put("httpMethod", "POST");
            registerData.put("uid", this.currentUser.getUid());
            registerData.put("email", eMailText);
            registerData.put("birthDate", birthDateText);

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(registerData.toString());

        }
        catch (JSONException e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
            System.out.println("AddChildToParentActivity addChildToParent Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(response.getBoolean("success")) {
                    showToast("התאמה נמצאה, ההרשמה בוצעה בהצלחה");
                    switchToMyChildrenActivityActivity();
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
            catch (Exception e) {
                showToast("שגיאה בטעינת המידע מהמערכת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            showToast("שגיאה בהרשמה למערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void switchToMyChildrenActivityActivity() {
        Intent googleRegIntent = new Intent(this, MyChildrenActivity.class);
        googleRegIntent.putExtra("currentUser", this.currentUser);
        startActivity(googleRegIntent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
