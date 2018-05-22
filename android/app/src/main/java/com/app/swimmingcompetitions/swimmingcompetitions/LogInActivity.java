package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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


public class LogInActivity extends LoadingDialog implements View.OnClickListener, AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private EditText logInMail;
    private EditText logInPassword;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String logInMailText;
    private String logInPasswordText;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(this);

        for (int i = 0; i < googleSignInButton.getChildCount (); i++) {
            View v = googleSignInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("כניסה באמצעות גוגל");
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

        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        googleSignIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.fbUser != null) {
            switchToHomePageActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.fbUser != null) {
            switchToHomePageActivity();
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
                    showProgressDialog("מבצע כניסה...");
                    showToast("ההתחברות נכשלה, נסה שוב");
                }
            }
        });
    }

    private void logInWithIdToken(String token) {
        JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        JSONObject logInData = new JSONObject();

        try {
            logInData.put("urlSuffix", "/logIn");
            logInData.put("httpMethod", "POST");
            logInData.put("idToken", token);
        }
        catch (JSONException e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
        }

        jsonAsyncTaskPost.execute(logInData.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgressDialog("מבצע כניסה...");

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
                }
            }
        });
    }

    @Override
    public void processFinish(String result) {
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
            catch (Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            showToast("שגיאה בטעינת המידע מהשרת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
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
        Intent signInIntent = this.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void switchToForgotPasswordActivity(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
