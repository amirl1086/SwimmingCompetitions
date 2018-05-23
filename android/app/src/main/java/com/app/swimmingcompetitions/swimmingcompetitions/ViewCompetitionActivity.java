package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewCompetitionActivity extends LoadingDialog implements AsyncResponse {

    private JSON_AsyncTask jsonAsyncTaskPost;
    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private String currentCallout;
    private Competition selectedCompetition;
    private Button startCompetitionBtn;
    private JSONObject dataObj;
    private Button registerEditBtn;
    private Button registerOtherUserBtn;
    private TextView competitionName;
    private TextView participantsForIteration;
    private TextView ages;
    private TextView style;
    private TextView distance;
    private TextView time;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")  && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            bindView();
            setUpSidebar();

            try {
                ArrayList<Participant> participants = this.selectedCompetition.getParticipants();

                if(intent.hasExtra("newParticipant")) {
                    showToast("המתחרה נוסף בהצלחה");
                    JSONObject newParticipantJson = new JSONObject(intent.getStringExtra("newParticipant"));
                    Participant newParticipant = new Participant(newParticipantJson.getString("uid"), newParticipantJson);

                    participants.add(newParticipant);
                    this.selectedCompetition.setAllParticipants(participants);
                }

                if(this.currentUser.getType().equals("coach")) {
                    this.registerEditBtn.setText("ערוך תחרות");
                    this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switchToCreateNewCompetitionActivityEditMode();
                        }
                    });
                    this.registerOtherUserBtn.setText("רשום מתחרה");
                }
                else if(this.currentUser.getType().equals("student")) {
                    this.startCompetitionBtn.setVisibility(View.INVISIBLE);

                    if(this.selectedCompetition.isCurrentUserRegistered(this.currentUser, participants)) {
                        this.registerEditBtn.setText("בטל רישום לתחרות");

                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelRegistration();
                            }
                        });
                    }
                    else {
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinToCompetition();
                            }
                        });
                    }
                }
                else if(this.currentUser.getType().equals("parent")) {
                    this.registerOtherUserBtn.setText("רשום מתחרה");
                }
            }
            catch (JSONException e) {
                showToast("שגיאה באתחול התחרות, נסה לאתחל את האפליקציה");
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    private void bindView() {
        DateUtils dateUtils = new DateUtils();
        Calendar calendar;

        this.competitionName = findViewById(R.id.competition_name);
        this.date = findViewById(R.id.date_of_competition);
        this.time = findViewById(R.id.time_of_competition);
        this.distance = findViewById(R.id.distance_of_competition);
        this.style = findViewById(R.id.style_of_competition);
        this.ages = findViewById(R.id.ages_range_of_competition);
        this.participantsForIteration = findViewById(R.id.num_of_participants_for_competition);

        this.registerEditBtn = findViewById(R.id.register_edit_btn);
        this.registerOtherUserBtn = findViewById(R.id.register_temporary_user_btn);
        this.startCompetitionBtn = findViewById(R.id.start_competition);

        calendar = dateUtils.dateToCalendar(new Date(this.selectedCompetition.getActivityDate()));
        this.date.setText(dateUtils.getDate(calendar));
        competitionName.setText(selectedCompetition.getName());
        time.setText(dateUtils.getTime(calendar));
        distance.setText(selectedCompetition.getLength());
        style.setText(selectedCompetition.getSwimmingStyle());
        ages.setText(selectedCompetition.getAgesString());
        participantsForIteration.setText(String.valueOf(selectedCompetition.getNumOfParticipants()));
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
        toolbar.setTitle("");
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

    private void cancelRegistration() {
        JSONObject data = new JSONObject();
        try {
            this.currentCallout = "cancelRegistration";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("מבטל רישום לתחרות...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    public void switchToRegisterUserToCompetitionActivity(View view) {
        Intent intent = new Intent(this, PreCompetitionRegisterActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void switchToCreateNewCompetitionActivityEditMode() {
        Intent intent = new Intent(this, CreateNewCompetitionActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("editMode", true);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void switchToIterationsActivity() {
        Intent intent = new Intent(this, IterationsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void initCompetitionForIterations(View view) {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "initCompetitionForIterations";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "GET");
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("מאתחל תחרות למקצים...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    public void joinToCompetition() {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "joinToCompetition";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("firstName", this.currentUser.getFirstName());
            data.put("lastName", this.currentUser.getLastName());
            data.put("birthDate", this.currentUser.getBirthDate());
            data.put("gender", this.currentUser.getGender());
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());
        }
        catch (JSONException e) {
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
        }

        showProgressDialog("נרשם לתחרות...");

        jsonAsyncTaskPost = new JSON_AsyncTask();
        jsonAsyncTaskPost.delegate = this;
        jsonAsyncTaskPost.execute(data.toString());
    }

    @Override
    public void processFinish(String result) {
        try {
            if (result != null) {
                JSONObject response = new JSONObject(result);

                if (response.getBoolean("success")) {
                    if(this.currentCallout.equals("initCompetitionForIterations")) {
                        this.dataObj = response.getJSONObject("data");

                        if (dataObj.get("type").equals("resultsMap")) {
                            this.dataObj.remove("type");
                            showToast("התחרות הנוכחית כבר הסתיימה");
                            startCompetitionBtn.setText("צפה בתוצאות");
                            startCompetitionBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switchToViewCompetitionResultsActivity();
                                }
                            });
                        }
                        else if (this.dataObj.get("type").equals("newIteration")) {
                            this.dataObj.remove("type");
                            this.selectedCompetition = new Competition(this.dataObj);
                            if (this.selectedCompetition.getParticipants().size() == 0) {
                                showToast("לא קיימים מתחרים לתחרות, נסה תחילה לרשום מתחרים");
                            }
                            else {
                                switchToIterationsActivity();
                            }
                        }
                    }
                    else if(this.currentCallout.equals("joinToCompetition")) {
                        this.registerEditBtn.setText("בטל רישום");
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelRegistration();
                            }
                        });
                        showToast("נרשמת לתחרות בהצלחה");
                    }
                    else if(this.currentCallout.equals("cancelRegistration")) {
                        this.registerEditBtn.setText("הרשם לתחרות");
                        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinToCompetition();
                            }
                        });
                        showToast("הרישום הוסר בהצלחה");
                    }
                }
                else {
                    showToast("ViewCompetitionsActivity processFinish: Error loging in");
                }
            }
        }
        catch (JSONException e) {
            showToast("ViewCompetitionsActivity processFinish: Error parsing JSONObject");
        }

        hideProgressDialog();
    }

    private void switchToViewCompetitionResultsActivity() {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);

        intent.putExtra("competitionResults", this.dataObj.toString());
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);

        startActivity(intent);
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
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
