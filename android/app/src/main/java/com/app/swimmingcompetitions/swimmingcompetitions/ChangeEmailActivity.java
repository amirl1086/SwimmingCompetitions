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


public class ChangeEmailActivity extends LoadingDialog {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private EditText email;
    private EditText emailConfirmation;
    private String emailText;
    private String emailConfirmationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();


            this.email = findViewById(R.id.register_email);
            this.emailConfirmation = findViewById(R.id.register_email_confirmation);

        }
        else {
            switchToLogInActivity();
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void changeEmail(View view) {
        if(isValid()) {
            showProgressDialog("שינוי האימייל מתבצע...");
            this.fbUser.updateEmail(this.emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        showToast("שינוי האימייל התבצע בהצלחה!");
                    }
                    else {
                        showToast("שינוי האימייל נכשל, נסה לאתמחל את האפליקציה");
                    }
                }
            });
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean isValid() {
        this.emailText = this.email.getText().toString();
        if(this.emailText.isEmpty()) {
            this.email.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.emailText)) {
            this.email.setError("כתובת אימייל שגוייה");
            return false;
        }

        if(this.emailConfirmationText.isEmpty()) {
            this.emailConfirmation.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.emailConfirmationText)) {
            this.emailConfirmation.setError("כתובת אימייל שגוייה");
            return false;
        }
        if(!this.emailText.equals(this.emailConfirmationText)) {
            this.emailConfirmation.setError("סיסמא לא תואמת");
            return false;
        }
        return true;
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
