package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class ChangeEmailActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private EditText email;
    private EditText emailConfirmation;
    private String emailText;
    private String emailConfirmationText;
    private JSON_AsyncTask jsonAsyncTaskPost;

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
                    if (task.isSuccessful()) {
                        updateUserDetails();
                    }
                    else {
                        hideProgressDialog();
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        switch (errorCode) {

                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                showToast("כתובת האימייל שנבחרה נמצאת בשימוש, אנא בחר כתובת שונה");
                                break;

                            case "ERROR_REQUIRES_RECENT_LOGIN":
                                showToast("לא בוצע חיבור מאובטח מזה זמן מה, אנא התנתק והתחבר מחדש כדי לבצע פעולה זו");
                                break;

                            default:
                                showToast("שינוי כתובת האימייל נכשל, אנא התנתק והתחבר מחדש כדי לבצע פעולה זו");
                                break;

                        }
                    }
                }
            })/*.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //System.out.println(e.);
                    e.getCause();
                    e.getMessage();

                }
            })*/;
        }
    }

    private void updateUserDetails() {
        try {
            JSONObject data = new JSONObject();
            data.put("urlSuffix", "/updateFirebaseUser");
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());
            data.put("email", this.emailText);
            data.put("firstName", this.currentUser.getFirstName());
            data.put("lastName", this.currentUser.getLastName());
            data.put("type", this.currentUser.getType());

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה שוב");
            System.out.println("LogInActivity processFinish Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
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

        this.emailConfirmationText = this.emailConfirmation.getText().toString();
        if(this.emailConfirmationText.isEmpty()) {
            this.emailConfirmation.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(this.emailConfirmationText)) {
            this.emailConfirmation.setError("כתובת אימייל שגוייה");
            return false;
        }
        if(!this.emailText.equals(this.emailConfirmationText)) {
            this.emailConfirmation.setError("כתובת אימייל לא תואמת");
            return false;
        }
        return true;
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                if(response.getBoolean("success")) {
                    showToast("שינוי האימייל התבצע בהצלחה!");
                }
            }
            catch (Exception e) {
                showToast("שגיאה בשינוי כתובת המייל, נסה שוב");
                System.out.println("ViewCompetitionsActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשינוי כתובת המייל, נסה שוב");
        }
        hideProgressDialog();
    }
}
