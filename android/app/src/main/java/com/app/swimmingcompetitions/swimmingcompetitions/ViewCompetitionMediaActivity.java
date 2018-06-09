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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class ViewCompetitionMediaActivity extends LoadingDialog implements HttpAsyncResponse  {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private JSON_AsyncTask jsonAsyncTaskPost;
    private RetrieveBitmapTask retrieveBitmapTask;

    private Competition selectedCompetition;
    private ArrayList<JSONObject> mediaList;
    private GridView imageGrid;

    private FirebaseStorage storage;
    private Uri currentPictureUri;

    private static final int TOTAL_IMAGE_WIDTH = 50;
    private static final int TOTAL_IMAGE_HEIGHT = 50;
    private static final int ACTION_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private String currentCallout;
    private ImageAdapter imageAdapter;
    private ArrayList<String> urlList;


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

            this.urlList = new ArrayList<>();
            this.mediaList = new ArrayList<>();

            this.storage = FirebaseStorage.getInstance("gs://firebase-swimmingcompetitions.appspot.com");
            this.imageGrid = findViewById(R.id.media_list);

            this.imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    try {
                        if(mediaList.get(position).getString("contentType").equals("video/mp4")) {
                            openViewVideoActivity(position);
                        }
                        else if(mediaList.get(position).getString("contentType").equals("image/jpeg")) {
                            openViewImageActivity(position);
                        }
                    }
                    catch (Exception e) {
                        showToast("שגיאה בטעינת הקובץ, נסה לאתחל את האפליקציה");
                        System.out.println("ViewCompetitionsActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                        e.printStackTrace();
                    }
                }
            });

            setUpSidebar();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            this.imageGrid.post(new Runnable(){
                @Override public void run(){
                    getMediaByCompetitionId();
                }
            });
        }
        else {
            switchToLogInActivity();
        }
    }

    private void openViewVideoActivity(int position) throws Exception{
        Intent intent = new Intent(this, WatchVideoActivity.class);
        intent.putExtra("videoUrl", this.mediaList.get(position).getString("url"));
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void openViewImageActivity(int position) throws Exception {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("imageUrl", this.mediaList.get(position).getString("url"));
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void getMediaByCompetitionId() {
        try {
            JSONObject data = new JSONObject();
            showProgressDialog("טוען את התמונות והסרטונים של התחרות שנבחרה...");

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
    }

    private void handleNewMediaAdded(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                if (response.getBoolean("success")) {
                    showToast("הקובץ עלה בהצלחה לענן");
                    JSONObject dataObj = response.getJSONObject("data");

                    this.mediaList.add(dataObj);

                    this.imageAdapter = new ImageAdapter(this, R.layout.media_item, this.mediaList);
                    this.imageGrid.setAdapter(imageAdapter);
                    this.imageAdapter.notifyDataSetChanged();
                }
                else {
                    showToast("שגיאה בהעלאת הקובץ לענן, נסה שוב");
                }
            }
            catch (Exception e) {
                showToast("שגיאה בעיבוד התשובה מהשרת, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionMediaActivity handleNewMediaAdded Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשמירת התמונה במערכת, נסה לאתחל את האפליקציה");
        }
        hideProgressDialog();
    }

    private void handleMediaReceived(String result) {
        int totalWidth = this.imageGrid.getWidth();
        int pictureWidth = TOTAL_IMAGE_WIDTH, pictureHeight = TOTAL_IMAGE_HEIGHT;

        if(totalWidth < TOTAL_IMAGE_WIDTH * 3) {
            pictureWidth = totalWidth / 3;
        }
        else {
            for(int i = TOTAL_IMAGE_WIDTH; i < totalWidth; i += 150) {
                if(totalWidth < (i + 150) * 3) {
                    pictureWidth = i;
                    pictureHeight = i;
                    break;
                }
            }
        }

        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");

                if(response.getBoolean("success")) {
                    Iterator<String> mediaIds = dataObj.keys();
                    while(mediaIds.hasNext()) {
                        JSONObject currMedia = new JSONObject(dataObj.get(mediaIds.next()).toString());
                        currMedia.put("width", pictureWidth);
                        currMedia.put("height", pictureHeight);
                        this.mediaList.add(currMedia);
                    }

                    this.imageAdapter = new ImageAdapter(this, R.layout.media_item, this.mediaList);
                    this.imageGrid.setAdapter(this.imageAdapter);
                    this.imageAdapter.notifyDataSetChanged();
                }
                else {
                    if(dataObj.getString("message").equals("no_media")) {
                        showToast("לא קיימים תמונות וסרטונים עבור התחרות שנבחרה");
                    }
                }
            }
            catch (Exception e) {
                showToast("שגיאה בעיבוד התמונות והסרטונים, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionMediaActivity handleMediaReceived Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשליפת התמונות והסרטונים מהמערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private void launchVideoCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
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
                this.currentPictureUri = FileProvider.getUriForFile(this, "com.app.swimmingcompetitions.swimmingcompetitions.fileprovider", photoFile);
                getBaseContext().grantUriPermission("com.app.swimmingcompetitions.swimmingcompetitions", this.currentPictureUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.currentPictureUri);
                startActivityForResult(takePictureIntent, ACTION_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws Exception {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
                case REQUEST_VIDEO_CAPTURE: {//in case user took a picture
                    uploadVideoToFirebaseStorage(data.getData());
                    break;
                }
            }
        }
    }

    public void uploadVideoToFirebaseStorage(Uri videoUri) {
        showProgressDialog("מעלה את הסרטון לענן...");

        StorageReference mainStorageRef = this.storage.getReference();
        final StorageReference videosRef = mainStorageRef.child("videos/" + videoUri.getLastPathSegment());
        UploadTask uploadTask = videosRef.putFile(videoUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("שגיאה בהעלאת הסרטון לענן, נסה שוב");
                System.out.println("ViewCompetitionMediaActivity uploadVideoToFirebaseStorage Exception \nMessage: " + e.getMessage() + "\nStack Trace: ");
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getFileURL(videosRef, taskSnapshot.getMetadata().getContentType());
            }
        });
    }

    public void uploadImageToFirebaseStorage() {
        showProgressDialog("מעלה את התמונה לענן...");

        System.out.println("this.currentUri" + this.currentPictureUri);
        StorageReference mainStorageRef = this.storage.getReference();
        final StorageReference picturesRef = mainStorageRef.child("pics/" + this.currentPictureUri.getLastPathSegment());
        UploadTask uploadTask = picturesRef.putFile(this.currentPictureUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("שגיאה בהעלאת התמונה לענן, נסה שוב");
                System.out.println("ViewCompetitionMediaActivity uploadImageToFirebaseStorage Exception \nMessage: " + e.getMessage() + "\nStack Trace: ");
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getFileURL(picturesRef, taskSnapshot.getMetadata().getContentType());
            }
        });
    }

    private void getFileURL(StorageReference ref, final String contentType) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addNewMediaToDatabase(uri, contentType);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("שגיאה בהעלאת הקובץ לענן, נסה שוב");
                System.out.println("ViewMediaActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace: ");
                e.printStackTrace();
            }
        });
    }

    private void addNewMediaToDatabase(Uri uri, String contentType) {
        try {
            JSONObject data = new JSONObject();

            this.currentCallout = "addNewMedia";

            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("competitionId", this.selectedCompetition.getId());
            data.put("url", uri.toString());
            data.put("contentType", contentType);

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
            System.out.println("ViewCompetitionMediaActivity addNewMediaToDatabase Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
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
        toolbar.setTitle("תמונות וסרטונים");
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
                    case R.id.media_nav_item: {
                        switchToViewMediaActivity();
                        break;
                    }
                    case R.id.settings_nav_item: {
                        switchToMySettingsActivity();
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

    public void switchToViewInRealTimeActivity() {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMySettingsActivity() {
        Intent intent = new Intent(this, MySettingsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewMediaActivity() {
        Intent intent = new Intent(this, ViewMediaActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewStatisticsActivity() {
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

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void logOut() {
        this.mAuth.signOut();
        switchToLogInActivity();
    }

}
