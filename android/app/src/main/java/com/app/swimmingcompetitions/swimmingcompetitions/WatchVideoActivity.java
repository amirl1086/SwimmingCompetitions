package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WatchVideoActivity extends LoadingDialog {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private VideoView videoView;
    private int position;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("videoUrl")) {
            showProgressDialog("טוען את הסרטון...");

            Uri uri = Uri.parse(intent.getStringExtra("videoUrl"));
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            this.position = 0;
            this.videoView = findViewById(R.id.video_view);

            try {
                mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView); // set the videoView that acts as the anchor for the MediaController.
                mediaController.setMediaPlayer(videoView);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
            }
            catch (Exception e) {
                showToast("שגיאה בטעינת הסרטון, נסה לאתחל את האפליקציה");
                System.out.println("LogInActivity processFinish Exception, \nMassage:" + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }

            // when the video file ready for playback.
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (position == 0) {
                        videoView.start();
                    }
                    else {
                        videoView.seekTo(position);
                    }

                    // when video Screen change size.
                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mediaController.setAnchorView(videoView); // re-set the videoView that acts as the anchor for the MediaController
                        }
                    });

                    hideProgressDialog();
                }
            });

        }
        else {
            switchToLogInActivity();
        }
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    // when you change direction of phone, this method will be called. it store the state of video (current position)
    @Override public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition()); // store current position.
        videoView.pause();
    }


    // after rotating the phone. This method is called.
    @Override public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("CurrentPosition"); // get saved position.
        videoView.seekTo(position);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
