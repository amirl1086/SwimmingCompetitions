package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PreCompetitionRegisterActivity extends AppCompatActivity {

    private User currentUser;
    private Competition selectedCompetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_competition_register);

        Intent intent = getIntent();

        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
        }
    }

    public void switchToRegisterActivity(final View view) {
        Intent intent;
        if(view.getId() == R.id.temp_user_btn) {
            intent = new Intent(this, RegisterTempUserActivity.class);
        }
        else {
            intent = new Intent(this, RegisterActivity.class);
        }
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }
}
