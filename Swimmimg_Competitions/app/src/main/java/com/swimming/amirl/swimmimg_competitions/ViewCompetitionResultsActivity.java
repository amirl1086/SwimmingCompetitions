package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ListView listView;
    private AgeResultAdapter resultsListAdapter;
    private ArrayList<JSONObject> results;
    private JSON_AsyncTask jsonAsyncTaskPost;
    private Competition selectedCompetition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_results);


        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            try {
                this.listView = findViewById(R.id.results_list_items);
                this.results = new ArrayList<>();
                this.currentUser = (User) intent.getSerializableExtra("currentUser");
                this.mAuth = FirebaseAuth.getInstance();
                this.fbUser = this.mAuth.getCurrentUser();
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");

                setUpSidebar();

                if(intent.hasExtra("competitionResults")) {
                    JSONObject competitionResults = new JSONObject(intent.getStringExtra("competitionResults"));
                    Iterator<String> agesKeys = competitionResults.keys();

                    while (agesKeys.hasNext()) {
                        String currentAge = agesKeys.next();
                        JSONObject currentResult = new JSONObject(competitionResults.get(currentAge).toString());
                        this.results.add(currentResult);
                    }

                    this.resultsListAdapter = new AgeResultAdapter(this, R.layout.age_result_list_item, results);
                    this.listView.setAdapter(this.resultsListAdapter);
                }
                else {
                    JSONObject data = new JSONObject();
                    //get competitions list set up action params
                    data.put("urlSuffix", "/getPersonalResults");
                    data.put("httpMethod", "GET");
                    data.put("competition", this.selectedCompetition.getJSON_Object().toString());

                    showProgressDialog("טוען תוצאות...");

                    this.jsonAsyncTaskPost = new JSON_AsyncTask();
                    this.jsonAsyncTaskPost.delegate = this;
                    this.jsonAsyncTaskPost.execute(data.toString());
                }
            }
            catch (Exception e) {
                showToast("שגיאה בשליפת התוצאות מהמערכת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("תוצאות " + this.selectedCompetition.getName());
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


    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                JSONObject response = new JSONObject(result);

                JSONObject dataObj = response.getJSONObject("data");
                Iterator<String> agesKeys = dataObj.keys();

                while (agesKeys.hasNext()) {
                    String currentAge = agesKeys.next();
                    JSONObject currentResult = new JSONObject(dataObj.get(currentAge).toString());
                    this.results.add(currentResult);
                }

                this.resultsListAdapter = new AgeResultAdapter(this, R.layout.age_result_list_item, results);
                this.listView.setAdapter(this.resultsListAdapter);
            }
            catch(Exception e) {
                showToast("שגיאה בטעינת המידע מהשרת, נסה לאתחל את האפליקציה");
            }
        }
        else {
            showToast("שגיאה בטעינת המידע מהשרת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
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
