package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyChildrenActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_children);

        Intent intent = getIntent();
        if(!intent.hasExtra("currentUser")) {
            switchToLogInActivity();
        }

        this.currentUser = (User) intent.getSerializableExtra("currentUser");

        JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        try {
            logInData.put("urlSuffix", "/getUsersByParentId");
            logInData.put("httpMethod", "POST");
            logInData.put("filter", "parentId");
            logInData.put("value", this.currentUser.getUid());
        }
        catch (JSONException e) {
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("מבצע כניסה...");

        jsonAsyncTaskPost.execute(logInData.toString());
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(String result) {
/*        try {
            if(result != null) {

            }
            else {
                showToast("שגיאה בטעינת המשתמשים");
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
        }*/
        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
