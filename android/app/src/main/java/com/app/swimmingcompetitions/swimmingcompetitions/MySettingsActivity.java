package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MySettingsActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);

        Intent intent = getIntent();
        if(!intent.hasExtra("currentUser")) {
            switchToLogInActivity();
        }

        this.currentUser = (User) intent.getSerializableExtra("currentUser");
    }

    public void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void openMyPersonalInformationActivity(View view) {
        Intent intent = new Intent(this, MyPersonalInformationActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openMyChildrenActivity(View view) {
        Intent intent = new Intent(this, MyChildrenActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openMyMessagesActivity(View view) {
        Intent intent = new Intent(this, MyMessagesActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }
}
