package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class ViewCompetitionMediaActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private JSON_AsyncTask jsonAsyncTaskPost;

    private Competition selectedCompetition;
    private ArrayList<JSONObject> mediaList;

    private FirebaseStorage storage;
    private Uri currentUri;
    private ImageView mImageView;
    private static final int ACTION_IMAGE_CAPTURE = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private String currentCallout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_media);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

            this.mImageView = findViewById(R.id.image);
            this.storage = FirebaseStorage.getInstance("gs://firebase-swimmingcompetitions.appspot.com");

            setUpSidebar();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            getMediaByCompetitionId();
        }
        else {
            switchToLogInActivity();
        }
    }

    private void getMediaByCompetitionId() {
        try {
            JSONObject data = new JSONObject();
            showProgressDialog("טוען את התמונות והסרטונים...");

            this.currentCallout = "getMediaByCompetitionId";

            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "GET");
            data.put("competitionId", this.selectedCompetition.getId());

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
            System.out.println("ViewCompetitionsActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String result) {
        if(this.currentCallout.equals("getMediaByCompetitionId")) {
            handleMediaReceived(result);
        }
        else if(this.currentCallout.equals("addNewMedia")) {
            handleNewMediaAdded(result);
        }

        hideProgressDialog();
    }

    private void handleNewMediaAdded(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");
                if (response.getBoolean("success")) {
                    showToast("הקובץ עלה בהצלחה לענן");
                } else {
                    showToast("שגיאה בהעלאת הקובץ לענן, נסה שוב");
                }
            } catch (Exception e) {
                showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionMediaActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשליפת התחרויות מהמערכת, נסה לאתחל את האפליקציה");
        }
    }

    private void handleMediaReceived(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(response.getBoolean("success")) {
                    this.mediaList = new ArrayList<>();

                    Iterator<String> mediaIds = dataObj.keys();
                    while(mediaIds.hasNext()) {
                        this.mediaList.add(new JSONObject(dataObj.get(mediaIds.next()).toString()));
                        System.out.println("currentMedia: " + dataObj.get(mediaIds.next()).toString());
                    }
                }
                else {
                    if(dataObj.getString("message").equals("no_media")) {
                        showToast("לא קיימים תמונות וסרטונים עבור התחרות שנבחרה");
                    }
                }
            }
            catch (Exception e) {
                showToast("שגיאה בטעינת התמונות והסרטונים, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionsActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשליפת התמונות והסרטונים מהמערכת, נסה לאתחל את האפליקציה");
        }
    }

    private File createImageFile() throws Exception {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName, // prefix
                ".jpg", // suffix
                storageDir // directory
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_IMAGE_CAPTURE: {//in case user took a picture
                    uploadImageToFirebaseStorage();
                    break;
                }
            }
        }
    }

    public void uploadImageToFirebaseStorage() {
        showProgressDialog("מעלה את התמונה לענן...");
        System.out.println("this.currentUri" + this.currentUri);
        StorageReference mainStorageRef = this.storage.getReference();
        final StorageReference pictureRef = mainStorageRef.child("pics/" + this.currentUri.getLastPathSegment());
        UploadTask uploadTask = pictureRef.putFile(this.currentUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("שגיאה בהעלאת התמונה לענן, נסה שוב");
                System.out.println("ViewMediaActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace: ");
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getFileURL(pictureRef, taskSnapshot.getMetadata().getContentType());
            }
        });
    }

    private void getFileURL(StorageReference pictureRef, final String type) {
        pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addNewMediaToDatabase(uri, type);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("שגיאה בהעלאת התמונה לענן, נסה שוב");
                System.out.println("ViewMediaActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace: ");
                e.printStackTrace();
            }
        });
    }

    private void addNewMediaToDatabase(Uri uri, String type) {
        try {
            JSONObject data = new JSONObject();

            this.currentCallout = "addNewMedia";

            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("competitionId", this.selectedCompetition.getId());
            data.put("url", uri.toString());
            data.put("type", type);

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
            System.out.println("ViewCompetitionsActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.back_to_home: {
                switchToHomePageActivity();
                return true;
            }
            case R.id.photo_camera: {
                launchCamera();
                return true;
            }
            case R.id.video_camera: {
                launchVideoCamera();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchVideoCamera() {

    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            }
            catch (Exception e) {
                showToast("שגיאה בהפעלת המצלמה, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionMediaActivity launchCamera Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                this.currentUri = FileProvider.getUriForFile(this,
                        "com.app.swimmingcompetitions.swimmingcompetitions.fileprovider", photoFile);

                getBaseContext().grantUriPermission("com.app.swimmingcompetitions.swimmingcompetitions", this.currentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.currentUri);
                startActivityForResult(takePictureIntent, ACTION_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.competition_media_tool_bar_menu, menu);
        return true;
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("בחר מדיה או הוסף");
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        if (this.currentUser.getType().equals("parent") || this.currentUser.getType().equals("coach")) {
            this.navigationView.inflateMenu(R.menu.parent_home_side_bar_menu);
        } else if (this.currentUser.getType().equals("student")) {
            this.navigationView.inflateMenu(R.menu.student_home_side_bar_menu);
        }

        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.competitions_nav_item: {
                        switchToViewCompetitionsActivity();
                        break;
                    }
                    case R.id.personal_results_nav_item: {
                        switchToViewResultsActivity();
                        break;
                    }
                    case R.id.statistics_nav_item: {
                        switchToViewStatisticsActivity();
                        break;
                    }
                    case R.id.real_time_nav_item: {
                        switchToViewInRealTimeActivity();
                        break;
                    }
                    case R.id.my_personal_info_nav_item: {
                        switchToMyPersonalInformationActivity();
                        break;
                    }
                    case R.id.my_children_nav_item: {
                        switchToMyChildrenActivity();
                        break;
                    }
                    case R.id.change_email_nav_item: {
                        switchToChangeEmailActivity();
                        break;
                    }
                    case R.id.media_nav_item: {
                        switchToViewMediaActivity();
                        break;
                    }
                    case R.id.change_password_nav_item: {
                        switchToChangePasswordActivity();
                        break;
                    }
                    case R.id.log_out_nav_item: {
                        logOut();
                        break;
                    }
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void switchToViewMediaActivity() {
        Intent intent = new Intent(this, ViewMediaActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    private void switchToViewInRealTimeActivity() {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void switchToViewStatisticsActivity() {
        Intent intent = new Intent(this, ViewStatisticsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionsActivity() {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewResultsActivity() {
        Intent intent = new Intent(this, ViewPersonalResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMyPersonalInformationActivity() {
        Intent intent = new Intent(this, MyPersonalInformationActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMyChildrenActivity() {
        Intent intent = new Intent(this, MyChildrenActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToChangePasswordActivity() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToChangeEmailActivity() {
        Intent intent = new Intent(this, ChangeEmailActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void logOut() {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}

