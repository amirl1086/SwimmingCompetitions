package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PreRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);
    }

    public void switchToRegisterActivity(final View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        String buttonName = view.getId() == R.id.parent_btn ? "parent" : "student";
        intent.putExtra("registerType", buttonName);
        startActivity(intent);
    }
}
