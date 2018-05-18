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
            switchToMainMenuActivity();
        }
    }

    public void logIn(final View view) {
        String logInMailText = this.logInMail.getText().toString();
        String logInPasswordText = this.logInPassword.getText().toString();

        showProgressDialog("מבצע כניסה...");

        this.mAuth.signInWithEmailAndPassword(logInMailText, logInPasswordText)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    fbUser = mAuth.getCurrentUser();
                    logInWithFirebase();
                }
                else {
                    showToast("ההתחברות נכשלה, נסה שוב");
                }
                }
            });
    }

    private void logInWithFirebase() {
        this.fbUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
            if(task.isSuccessful()) {
                logInWithIdToken(task.getResult().getToken());
            }
            else {
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
                showToast("הכניסה באמצעות גוגל נכשלה, נסה שוב");
                hideProgressDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    fbUser = mAuth.getCurrentUser();
                    logInWithFirebase();
                }
                else {
                    showToast("הכניסה באמצעות גוגל נכשלה, נסה שוב");
                }
            }
        });
    }

    @Override
    public void processFinish(String result) {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                JSONObject userData = response.getJSONObject("data");
                this.currentUser = new User(userData);
                if(this.currentUser.getBirthDate().isEmpty()) {
                    switchToGoogleRegisterActivity();
                }
                else {
                    switchToMainMenuActivity();
                }
            }
            else {
                showToast("שגיאה בכניסה למערכת, נסה שוב");
            }
        }
        catch (JSONException e) {
            showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
        }
        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
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
        showProgressDialog("מבצע כניסה...");

        Intent signInIntent = this.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
