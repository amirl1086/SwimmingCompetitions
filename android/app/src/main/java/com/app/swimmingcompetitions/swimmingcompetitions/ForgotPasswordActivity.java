package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends LoadingDialog {

    private EditText logInMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.logInMail = findViewById(R.id.edit_email);
    }

    public void changePassword(View view) {
        String logInMailText = this.logInMail.getText().toString();

        if(logInMailText.isEmpty()) {
            this.logInMail.setError("אנא הכנס כתובת אימייל");
            return;
        }
        if(!isValidEmail(logInMailText)) {
            this.logInMail.setError("כתובת אימייל שגוייה");
            return;
        }

        showProgressDialog("מאמת את כתובת האימייל...");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(logInMailText)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToast("נשלחו לך המשך הנחיות לכתובת האימייל על מנת לאפס את הסיסמא");
                        switchToLogInActivity();
                    }
                    else {
                        showToast("כתובת האימייל לא נמצאה במערכת, נסה שוב");
                    }
                    hideProgressDialog();
                }
            });

    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, 2).show();
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

}
