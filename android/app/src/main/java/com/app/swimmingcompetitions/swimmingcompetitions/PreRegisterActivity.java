package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

public class PreRegisterActivity extends AppCompatActivity {

    private JSONObject userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);

        Intent intent = getIntent();
        if(intent.hasExtra("userData")) {
            try {
                this.userData = new JSONObject(intent.getStringExtra("userData"));
            }
            catch (Exception e) {
                showToast("שגיאה באתחול מסך ההרשמה, נסה לאתחל את האפליקציה");
                System.out.println("LogInActivity logInWithIdToken Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToRegisterActivity(final View view) {
        String buttonName = view.getId() == R.id.parent_btn ? "parent" : "student";

        if(this.userData != null) {
            Intent googleRegIntent = new Intent(this, GoogleRegisterActivity.class);
            googleRegIntent.putExtra("userData", this.userData.toString());
            googleRegIntent.putExtra("registerType", buttonName);
            startActivity(googleRegIntent);
        }
        else {
            Intent regIntent = new Intent(this, RegisterActivity.class);
            regIntent.putExtra("registerType", buttonName);
            startActivity(regIntent);
        }
    }
}
