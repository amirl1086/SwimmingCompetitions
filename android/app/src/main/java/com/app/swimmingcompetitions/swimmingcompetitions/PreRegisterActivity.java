package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class PreRegisterActivity extends AppCompatActivity {

    private User currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
        }
    }

    public void switchToRegisterActivity(final View view) {
        String buttonName = view.getId() == R.id.parent_btn ? "parent" : "student";

        if(this.currentUser != null) {
            Intent googleRegIntent = new Intent(this, GoogleRegisterActivity.class);
            googleRegIntent.putExtra("currentUser", this.currentUser);
            googleRegIntent.putExtra("registerType", buttonName);
            startActivity(googleRegIntent);
        }
        else {
            Intent regIntent = new Intent(this, RegisterActivity.class);
            regIntent.putExtra("registerType", buttonName);
            startActivity(regIntent);
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
