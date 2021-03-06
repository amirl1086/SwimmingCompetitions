package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class ViewInRealTimeActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private Competition competition;

    private ListView listView;
    private LiveResultsAdapter liveResultAdapter;
    private ArrayList<PersonalResult> liveResults;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_in_real_time);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();
            this.db = FirebaseDatabase.getInstance();
            this.liveResults = new ArrayList<>();
            this.listView = findViewById(R.id.live_results_list);

            getCompetitionInProgress();
            setUpSidebar();
        }
        else {
            switchToLogInActivity();
        }
    }

    private void getCompetitionInProgress() {
        try {
            JSONObject data = new JSONObject();
            showProgressDialog("טוען תוצאות בזמן אמת...");

            data.put("urlSuffix", "/getCompetitionInProgress");
            data.put("httpMethod", "GET");

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה");
            System.out.println("ViewInRealTimeActivity getCompetitionInProgress Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);
                if(response.getBoolean("success")) {
                    JSONObject dataObj = response.getJSONObject("data");

                    Iterator<String> competitionIds = dataObj.keys();
                    String competitionId = competitionIds.next();
                    this.competition = new Competition(dataObj.getJSONObject(competitionId));
                    showToast("נא המתן לתוצאות");
                    setUpLiveView();
                }
                else {
                    hideProgressDialog();
                    showToast("אין כרגע תחרות שמתקיימת");
                }
            }
            catch (Exception e) {
                showToast("שגיאה ביצירה של רשימת התוצאות, נסה לאתחל את האפליקציה");
                System.out.println("ViewInRealTimeActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בשליפת התחרויות מהמערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();

    }

    private void setUpLiveView() {
        DatabaseReference personalResultsRef = db.getReference("personalResults/" + this.competition.getId());

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                PersonalResult currPersonalResult = dataSnapshot.getValue(PersonalResult.class);
                addResultToLiveList(currPersonalResult);
                hideProgressDialog();
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        personalResultsRef.addChildEventListener(childEventListener);
    }

    private void addResultToLiveList(PersonalResult value) {
        this.liveResults.add(value);

        Collections.sort(this.liveResults, new Comparator<Object>(){

            @Override
            public int compare(Object a, Object b) {
                PersonalResult resultA = (PersonalResult) a;
                PersonalResult resultB = (PersonalResult) b;

                try {
                    return resultB.getTimeStamp().compareTo(resultA.getTimeStamp());
                }
                catch (Exception e) {
                    showToast("שגיאה במיון התוצאות, נסה לאתחל את האפליקציה");
                    System.out.println("ViewInRealTimeActivity addResultToLiveList Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                    e.printStackTrace();
                    return 0;
                }

            }
        });

        this.liveResultAdapter = new LiveResultsAdapter(this, R.layout.live_result_list_item, this.liveResults);
        this.listView.setAdapter(this.liveResultAdapter);
        this.liveResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(this.currentUser.getType().equals("coach")) {
            getMenuInflater().inflate(R.menu.coach_competitions_tool_bar_menu, menu);
        }
        else if(this.currentUser.getType().equals("student") || this.currentUser.getType().equals("parent")) {
            getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.back_to_home: {
                switchToHomePageActivity();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("צפה במקצים בזמן אמת");
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        if(this.currentUser.getType().equals("parent") || this.currentUser.getType().equals("coach")) {
            this.navigationView.inflateMenu(R.menu.parent_home_side_bar_menu);
        }
        else if(this.currentUser.getType().equals("student")) {
            this.navigationView.inflateMenu(R.menu.student_home_side_bar_menu);
        }

        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
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
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
