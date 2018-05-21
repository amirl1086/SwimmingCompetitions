package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class ViewCompetitionsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DateUtils dateUtils;
    private Competition selectedCompetition;
    private ArrayList<Competition> competitions;
    private CompetitionAdapter competitionsListAdapter;
    private Boolean isAscending;
    private int lastSelectedFilter;
    private ListView listView;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competitions);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            this.listView = findViewById(R.id.competitions_list);
            this.lastSelectedFilter = R.id.name_sort;
            this.isAscending = true;
            this.dateUtils = new DateUtils();

            setUpSidebar();

            JSONObject data = new JSONObject();
            try {
                data.put("urlSuffix", "/getCompetitions");
                data.put("httpMethod", "GET");
                JSONObject currentUserJson = this.currentUser.getJSON_Object();

                if(this.currentUser.getType().equals("student")) {
                    data.put("filters", "age");
                }
                data.put("currentUser", currentUserJson);
            }
            catch (JSONException e) {
                showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
            }

            showProgressDialog("טוען תחרויות...");

            JSON_AsyncTask jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
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
        int itemClicked = item.getItemId();
        switch(item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.back_to_home: {
                switchToHomePageActivity();
                return true;
            }
            case R.id.add_new_competition: {
                switchToCreateNewCompetitionActivity();
                return true;
            }
            case R.id.name_sort: case R.id.ages_sort: case R.id.date_sort: case R.id.style_sort: {
                sortCompetitionsByField(itemClicked);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortCompetitionsByField(final int fieldName) {
        if(this.lastSelectedFilter == fieldName) {
            this.isAscending = !this.isAscending;
        }
        else {
            this.isAscending = true;
            this.lastSelectedFilter = fieldName;
        }

        Collections.sort(this.competitions, new Comparator<Object>(){

            @Override
            public int compare(Object a, Object b) {
                Competition competitionA = (Competition) a;
                Competition competitionB = (Competition) b;

                switch(fieldName) {
                    case R.id.name_sort: {
                        return isAscending ?
                            competitionB.getName().toLowerCase().compareTo(competitionA.getName().toLowerCase()) :
                            competitionA.getName().toLowerCase().compareTo(competitionB.getName().toLowerCase());
                    }
                    case R.id.ages_sort: {
                        if(Integer.valueOf(competitionA.getFromAge()) > Integer.valueOf(competitionB.getFromAge())) {
                            return isAscending ? 1 : -1;
                        }
                        else if(Integer.valueOf(competitionA.getFromAge()) < Integer.valueOf(competitionB.getFromAge())) {
                            return isAscending ? -1 : 1;
                        }
                    }
                    case R.id.date_sort: {
                        if(dateUtils.stringToCalendar(competitionA.getActivityDate()).getTimeInMillis() > dateUtils.stringToCalendar(competitionB.getActivityDate()).getTimeInMillis()) {
                            return isAscending ? 1 : -1;
                        }
                        else if(dateUtils.stringToCalendar(competitionA.getActivityDate()).getTimeInMillis() < dateUtils.stringToCalendar(competitionB.getActivityDate()).getTimeInMillis()) {
                            return isAscending ? -1 : 1;
                        }
                    }
                    case R.id.style_sort: {
                        return isAscending ?
                                competitionB.getSwimmingStyle().toLowerCase().compareTo(competitionA.getSwimmingStyle().toLowerCase()) :
                                competitionA.getSwimmingStyle().toLowerCase().compareTo(competitionB.getSwimmingStyle().toLowerCase());
                    }
                    default:
                        return 0;
                }
            }
        });

        this.competitionsListAdapter = new CompetitionAdapter(this, R.layout.competition_list_item, this.competitions);
        this.listView.setAdapter(this.competitionsListAdapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCompetition = competitions.get(position);
                switchToViewCompetitionActivity();
            }
        });

        this.competitionsListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, R.id.name_sort, Menu.NONE, R.string.name_sort_text);
        menu.add(Menu.NONE, R.id.date_sort, Menu.NONE, R.string.date_sort_text);
        menu.add(Menu.NONE, R.id.style_sort, Menu.NONE, R.string.style_sort_text);
        menu.add(Menu.NONE, R.id.ages_sort, Menu.NONE, R.string.ages_sort_text);

        if(this.currentUser.getType().equals("coach")) {
            getMenuInflater().inflate(R.menu.coach_competitions_tool_bar_menu, menu);
        }
        else if(this.currentUser.getType().equals("student") || this.currentUser.getType().equals("parent")) {
            getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        }

        return true;
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("רשימת התחרויות");
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

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {
                    JSONObject dataObj = response.getJSONObject("data");
                    this.competitions = new ArrayList<>();

                    Iterator<String> competitionIds = dataObj.keys();
                    while (competitionIds.hasNext()) {
                        String currentId = competitionIds.next();
                        JSONObject currentCompetition = new JSONObject(dataObj.get(currentId).toString());
                        this.competitions.add(new Competition(currentId, currentCompetition));
                    }

                    sortCompetitionsByField(R.id.name_sort);
                }
                else {
                    showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
                }
            }
        }
        catch (JSONException e) {
            showToast("שגיאה ביצירה של רשימת התחרויות, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void switchToCreateNewCompetitionActivity() {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToHomePageActivity() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionActivity() {
        Intent intent = new Intent(this, ViewCompetitionActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
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
