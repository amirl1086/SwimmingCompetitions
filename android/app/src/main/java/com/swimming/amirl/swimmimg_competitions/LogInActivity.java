package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends LoadingDialog implements View.OnClickListener, HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private String currentCallout;
    private EditText logInMail;
    private EditText logInPassword;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String logInMailText;
    private String logInPasswordText;
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 100;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(this);

        for (int i = 0; i < googleSignInButton.getChildCount (); i++) {
            View v = googleSignInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("כניסה עם גוגל");
                break;
            }
        }

        this.logInMail = findViewById(R.id.edit_email);
        this.logInPassword = findViewById(R.id.edit_password);

        this.mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("197819058733-tgr1h1654aqi9k7a22slkhotlqdvjt49.apps.googleusercontent.com")
                .requestEmail()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    @Override public void onClick(View v) {
        googleSignIn();
    }
    @Override public void onBackPressed() { }
    @Override public void onResume() {
        super.onResume();
        redirectUser();
    }

    private void redirectUser() {
        // check if user is signed in (non-null) and update UI accordingly.
        this.fbUser = mAuth.getCurrentUser();
        if(this.fbUser != null) {
            try {
                showProgressDialog("מתחבר לחשבונך מחדש...");
                this.currentCallout = "getUser";
                this.jsonAsyncTaskPost = new JSON_AsyncTask();
                this.jsonAsyncTaskPost.delegate = this;
                JSONObject logInData = new JSONObject();
                logInData.put("urlSuffix", "/" + this.currentCallout);
                logInData.put("httpMethod", "GET");
                logInData.put("currentUserUid", this.fbUser.getUid());
                this.jsonAsyncTaskPost.execute(logInData.toString());
            }
            catch (JSONException e) {
                hideProgressDialog();
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
                System.out.println("LogInActivity logInWithIdToken Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
    }

    @Override public void processFinish(String result) {
        switch (this.currentCallout) {
            case "getUser": {
                handleGetUser(result);
                break;
            }
            case "logIn": {
                handleLogIn(result);
                break;
            }
        }

        hideProgressDialog();
    }

    private void handleGetUser(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                if (response.getBoolean("success")) {
                    JSONObject userData = response.getJSONObject("data");
                    this.currentUser = new User(userData);
                    switchToHomePageActivity();
                }
                else {
                    showToast("שגיאה בשחזור המידע שלך, נסה להתחבר מחדש");
                }
            }
            catch(Exception e) {
                showToast("שגיאה בשחזור המידע שלך, נסה להתחבר מחדש");
                System.out.println("LogInActivity handleGetUser Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשחזור המידע שלך, נסה לאתחל את האפליקציה");
        }
    }


    private void handleLogIn(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject userData = response.getJSONObject("data");
                this.currentUser = new User(userData);
                if(this.currentUser.getType().isEmpty()) {
                    switchToGoogleRegisterActivity();
                }
                else {
                    switchToHomePageActivity();
                }
            }
            catch(Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("LogInActivity processFinish Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בכניסה למערכת, נסה לאתחל את האפליקציה");
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public Boolean isValid() {
        this.logInMailText = this.logInMail.getText().toString();
        this.logInPasswordText = this.logInPassword.getText().toString();

        if(logInMailText.isEmpty()) {
            this.logInMail.setError("חובה למלא כתובת אימייל");
            return false;
        }
        if(!isValidEmail(logInMailText)) {
            this.logInMail.setError("כתובת אימייל שגוייה");
            return false;
        }
        if(logInPasswordText.isEmpty()) {
            this.logInPassword.setError("חובה למלא סיסמא");
            return false;
        }
        if(logInPasswordText.length() < 6) {
            this.logInPassword.setError("אורך סיסמא מינימלי 6 תווים");
            return false;
        }
        return true;
    }

    public void logIn(final View view) {
        if(isValid()) {
            showProgressDialog("מבצע אימות...");
            this.mAuth.signInWithEmailAndPassword(this.logInMailText, this.logInPasswordText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        fbUser = mAuth.getCurrentUser();
                        logInWithFirebase();
                    }
                    else {
                        hideProgressDialog();
                        showToast("שם משתמש או סיסמא לא נכונים, נסה שוב");
                    }
                }
            });
        }
    }

    private void logInWithFirebase() {
        this.fbUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
                    logInWithIdToken(task.getResult().getToken());
                }
                else {
                    hideProgressDialog();
                    showToast("ההתחברות נכשלה, נסה שוב");
                    System.out.println("LogInActivity getIdToken Exception:\n" + task.getException());
                }
            }
        });
    }

    private void logInWithIdToken(String token) {
        try {
            this.currentCallout = "logIn";
            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            JSONObject logInData = new JSONObject();
            logInData.put("urlSuffix", "/" + this.currentCallout);
            logInData.put("httpMethod", "POST");
            logInData.put("idToken", token);
            this.jsonAsyncTaskPost.execute(logInData.toString());
        }
        catch (JSONException e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
            System.out.println("LogInActivity logInWithIdToken Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e) {
                hideProgressDialog();
                showToast("הכניסה באמצעות גוגל נכשלה, נסה שוב");
                System.out.println("LogInActivity onActivityResult Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgressDialog("מבצע כניסה באמצעות גוגל...");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    fbUser = mAuth.getCurrentUser();
                    logInWithFirebase();
                }
                else {
                    hideProgressDialog();
                    showToast("הכניסה באמצעות גוגל נכשלה, נסה שוב");
                    System.out.println("LogInActivity firebaseAuthWithGoogle Exception:\n" + task.getException());
                }
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToGoogleRegisterActivity() {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToPreRegisterActivity(final View view) {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        startActivity(intent);
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void switchToForgotPasswordActivity(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void openUserGuide(View view) {
        Intent intent = new Intent(this, UserGuideActivity.class);
        intent.putExtra("url", "http://gahp.net/wp-content/uploads/2017/09/sample.pdf");
        startActivity(intent);
    }
}

