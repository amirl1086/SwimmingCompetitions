package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/firebase-swimmingcompetitions.appspot.com/o/user_guide_android.pdf?alt=media&token=e7572308-7d22-4336-b2ef-4346530b7701"));
        startActivity(browserIntent);

        /*String url = "https://firebasestorage.googleapis.com/v0/b/firebase-swimmingcompetitions.appspot.com/o/user_guide_android.pdf?alt=media&token=e7572308-7d22-4336-b2ef-4346530b7701";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/
    }
}

