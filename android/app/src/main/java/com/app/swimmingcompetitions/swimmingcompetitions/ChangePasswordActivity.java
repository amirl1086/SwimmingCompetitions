package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordActivity extends LoadingDialog {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private EditText password;
    private EditText passwordConfirmation;
    private String passwordText;
    private String passwordConfirmationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();


            this.password = findViewById(R.id.register_password);
            this.passwordConfirmation = findViewById(R.id.register_password_confirmation);

        }
        else {
            switchToLogInActivity();
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void changePassword(View view) {
        if(isValid()) {
            showProgressDialog("שינוי סיסמא מתבצע...");
            this.fbUser.updatePassword(this.passwordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        showToast("שינוי הסיסמא התבצע בהצלחה!");
                    }
                    else {
                        showToast("הסיסמא שהזנת לא חוקית, השינוי נכשל");
                    }
                }
            });
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean isValid() {
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
        return true;
    }
}
