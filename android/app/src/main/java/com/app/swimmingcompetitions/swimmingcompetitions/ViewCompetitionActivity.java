package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class ViewCompetitionActivity extends LoadingDialog implements HttpAsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;

    private JSON_AsyncTask jsonAsyncTaskPost;
    private String currentCallout;

    private Competition selectedCompetition;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private Button startCompetitionBtn;
    private Button registerEditBtn;
    private Button registerOtherUserBtn;
    private DateUtils dateUtils;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")  && intent.hasExtra("selectedCompetition")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            bindView();
            setUpSidebar();

            try {
                ArrayList<Participant> participants = this.selectedCompetition.getParticipants();

                if(intent.hasExtra("newParticipant")) {
                    handleNewParticipantAdded(participants, intent.getStringExtra("newParticipant"));
                }

                switch(this.currentUser.getType()) {
                    case "coach": {
                        handleCouchView();
                        break;
                    }
                    case "student": {
                        handleStudentView(participants);
                        break;
                    }
                    case "parent": {
                        handleParentView();
                        break;
                    }
                }

                if(this.dateUtils.isDatePassed(this.selectedCompetition.getActivityDate())) {
                    this.registerEditBtn.setText("צפה בתוצאות");
                    this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCompetitionResults();
                        }
                    });
                    this.registerOtherUserBtn.setVisibility(View.GONE);

                    if(this.currentUser.getType().equals("coach")) {
                        this.startCompetitionBtn.setVisibility(View.GONE);
                    }
                }

            }
            catch (Exception e) {
                hideProgressDialog();
                showToast("שגיאה באתחול התחרות, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    private void getCompetitionResults() {
        try {
            JSONObject data = new JSONObject();
            //get competitions list set up action params
            this.currentCallout = "getPersonalResults";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "GET");
            data.put("competition", this.selectedCompetition.getJSON_Object().toString());

            showProgressDialog("טוען תוצאות...");

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch(Exception e) {
            hideProgressDialog();
            showToast("שגיאה בשליחת הבקשה למערכת, נסה לאתחל את האפליקציה");
            System.out.println("ViewCompetitionActivity getCompetitionResults Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }

    }

    private void handleParentView() {
        this.registerOtherUserBtn.setText("רשום מתחרה אחר");
    }

    private void handleStudentView(ArrayList<Participant> participants) {
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

    private void handleCouchView() {
        this.registerEditBtn.setText("ערוך תחרות");
        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToCreateNewCompetitionActivityEditMode();
            }
        });
        this.registerOtherUserBtn.setText("רשום מתחרה אחר");
    }

    private void handleNewParticipantAdded(ArrayList<Participant> participants, String participantJson) throws Exception {
        showToast("המתחרה נוסף בהצלחה");
        JSONObject newParticipantJson = new JSONObject(participantJson);
        Participant newParticipant = new Participant(newParticipantJson.getString("uid"), newParticipantJson);

        participants.add(newParticipant);
        this.selectedCompetition.setAllParticipants(participants);
    }

    private void bindView() {
        this.registerEditBtn = findViewById(R.id.register_edit_btn);
        this.registerOtherUserBtn = findViewById(R.id.register_temporary_user_btn);
        this.startCompetitionBtn = findViewById(R.id.start_competition);

        this.dateUtils = new DateUtils();
        Calendar calendar;

        TextView competitionName = findViewById(R.id.competition_name);
        TextView date = findViewById(R.id.date_of_competition);
        TextView distance = findViewById(R.id.distance_of_competition);
        TextView style = findViewById(R.id.style_of_competition);
        TextView ages = findViewById(R.id.ages_range_of_competition);
        TextView participantsForIteration = findViewById(R.id.num_of_participants_for_competition);
        date.setText(this.dateUtils.getCompleteDate(this.selectedCompetition.getActivityDate()));
        competitionName.setText(selectedCompetition.getName());
        distance.setText(selectedCompetition.getLength());
        style.setText(selectedCompetition.getSwimmingStyle());
        ages.setText(selectedCompetition.getAgesString());
        participantsForIteration.setText(String.valueOf(selectedCompetition.getNumOfParticipants()));
    }

    private void cancelRegistration() {
        JSONObject data = new JSONObject();
        try {
            this.currentCallout = "cancelRegistration";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());

            showProgressDialog("מבטל רישום לתחרות...");

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה בביטול הרישום לתחרות, נסה לאתחל את האפליקציה");
            System.out.println("ViewCompetitionActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    public void initCompetitionForIterations(View view) {
        JSONObject data = new JSONObject();
        //get competitions list set up action params
        try {
            this.currentCallout = "initCompetitionForIterations";
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "GET");
            data.put("competitionId", this.selectedCompetition.getId());
            showProgressDialog("מאתחל תחרות למקצים...");

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה באתחול התחרות למקצים, נסה לאתחל את האפליקציה");
            System.out.println("ViewCompetitionActivity initCompetitionForIterations Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
    }

    public void joinToCompetition() {
        try {
            this.currentCallout = "joinToCompetition";

            JSONObject data = new JSONObject();
            data.put("urlSuffix", "/" + this.currentCallout);
            data.put("httpMethod", "POST");
            data.put("firstName", this.currentUser.getFirstName());
            data.put("lastName", this.currentUser.getLastName());
            data.put("birthDate", this.currentUser.getBirthDate());
            data.put("gender", this.currentUser.getGender());
            data.put("uid", this.currentUser.getUid());
            data.put("competitionId", this.selectedCompetition.getId());
            showProgressDialog("נרשם לתחרות...");

            jsonAsyncTaskPost = new JSON_AsyncTask();
            jsonAsyncTaskPost.delegate = this;
            jsonAsyncTaskPost.execute(data.toString());
        }
        catch (Exception e) {
            hideProgressDialog();
            showToast("שגיאה בהרשמה לתחרות, נסה לאתחל את האפליקציה");
            System.out.println("ViewCompetitionActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace: " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override public void processFinish(String result) {
        if(result != null) {
            try {
                JSONObject response = new JSONObject(result);
                JSONObject dataObj = response.getJSONObject("data");
                if(response.getBoolean("success")) {
                    switch (this.currentCallout) {
                        case "initCompetitionForIterations": {
                            handleInitCompetitionResult(dataObj);
                            break;
                        }
                        case "getPersonalResults": {
                            switchToViewCompetitionResultsActivity(dataObj);
                            break;
                        }
                        case "joinToCompetition": {
                            handleJoinToCompetitionResult();
                            break;
                        }
                        case "cancelRegistration": {
                            handleCancelRegistrationResult();
                            break;
                        }
                    }
                }
                else if(dataObj.getString("message").equals("no_results")){
                    showToast("התחרות הסתיימה ללא תוצאות, אנא פנה למאמן לפרטים נוספים");
                }
            }
            catch (Exception e) {
                showToast("שגיאה בקריאת התשובה מהמערכת, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionActivity processFinish Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            showToast("שגיאה בעיבוד הבקשה במערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private void handleInitCompetitionResult(final JSONObject dataObj) throws Exception {

        if(dataObj.get("type").equals("resultsMap")) {
            showToast("התחרות הנוכחית כבר הסתיימה");
            startCompetitionBtn.setText("צפה בתוצאות");

            startCompetitionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToViewCompetitionResultsActivity(dataObj);
                }
            });

        }
        else if(dataObj.get("type").equals("newIteration")) {
            this.selectedCompetition = new Competition(dataObj);

            if (this.selectedCompetition.getParticipants().size() == 0) {
                showToast("עדיין לא נרשמו מתחרים לתחרות זו");
            }
            else {
                switchToIterationsActivity();
            }
        }
    }

    private void handleJoinToCompetitionResult() {
        this.registerEditBtn.setText("בטל רישום");
        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRegistration();
            }
        });
        showToast("נרשמת לתחרות בהצלחה");
    }

    private void handleCancelRegistrationResult() {
        this.registerEditBtn.setText("הרשם לתחרות");
        this.registerEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinToCompetition();
            }
        });
        showToast("הרישום הוסר בהצלחה");
    }


    @Override public void onResume() {
        super.onResume();
        if(this.fbUser == null) {
            switchToLogInActivity();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
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

    private void switchToViewCompetitionResultsActivity(JSONObject dataObj) {
        Intent intent = new Intent(this, ViewCompetitionResultsActivity.class);

        intent.putExtra("competitionResults", dataObj.toString());
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);

        startActivity(intent);
    }

    public void switchToRegisterUserToCompetitionActivity(View view) {
        Intent intent = new Intent(this, PreCompetitionRegisterActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        intent.putExtra("selectedCompetition", this.selectedCompetition);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
