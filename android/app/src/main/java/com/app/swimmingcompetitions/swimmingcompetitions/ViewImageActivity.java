package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class ViewImageActivity extends LoadingDialog implements bitmapAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private RetrieveBitmapTask retrieveBitmapTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("imageUrl")) {
            showProgressDialog("טוען את התמונה מהענן...");
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            this.imageView = findViewById(R.id.image_view);
            try {
                Glide.with(imageView.getContext()).load(intent.getStringExtra("imageUrl")).into(imageView);
            }
            catch(Exception e) {
                showToast("טעינת התמונה נכשלה, נסה לאתחל את האפליקציה");
                System.out.println("ViewImageActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
            finally {
                hideProgressDialog();
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    private void urlImageToBitmap(String imageUrl) {
        this.retrieveBitmapTask = new RetrieveBitmapTask();
        retrieveBitmapTask.delegate = this;
        this.retrieveBitmapTask.execute(imageUrl);
    }

    @Override
    public void processFinish(Bitmap bitmap) {
        this.imageView.setImageBitmap(bitmap);
        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
