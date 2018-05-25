package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ViewStatisticsActivity extends LoadingDialog implements AsyncResponse {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private JSON_AsyncTask jsonAsyncTaskPost;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private Spinner styleSpinner;
    private String[] swimmingStyles;
    private Spinner lengthsSpinner;
    private String[] lengths;
    private ArrayAdapter<String> styleSpinnerListAdapter;
    private ArrayAdapter<String> lengthsSpinnerListAdapter;

    private ArrayList<Statistic> statistics;
    private String selectedSwimmingStyle;
    private String selectedLength;
    private Calendar maxDate;
    private Calendar minDate;
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();

            bindView();
            setUpSidebar();
            getParticipantStatistics();
        }
        else {
            switchToLogInActivity();
        }
    }

    private void bindView() {
        this.styleSpinner = findViewById(R.id.swimming_style_spinner);
        this.lengthsSpinner = findViewById(R.id.lengths_spinner);
        this.graphView = findViewById(R.id.graph);
    }

    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                this.statistics = new ArrayList<>();
                JSONObject response = new JSONObject(result);
                JSONArray dataList = response.getJSONArray("data");

                for(int i=0; i < dataList.length(); i++) {
                    JSONObject obj = dataList.getJSONObject(i);

                    String score = obj.getString("score");
                    JSONObject competition = obj.getJSONObject("competition");

                    this.statistics.add(new Statistic(Integer.valueOf(score), competition));
                }

                setUpLengths();
                setupStyles();
            }
            catch (Exception e) {
                showToast("שגיאה ביצירה של טבלת הסטטיסטיקות, נסה לאתחל את האפליקציה");
                System.out.println("ViewCompetitionsActivity Exception \nMessage: " + e.getMessage() + "\nStack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            showToast("שגיאה בשליפת התחרויות מהמערכת, נסה לאתחל את האפליקציה");
        }

        hideProgressDialog();
    }

    private void setUpGraphView() {
        final DateUtils dateUtils = new DateUtils();
        Collections.sort(this.statistics, new Comparator<Statistic>() {
            @Override
            public int compare(Statistic s1, Statistic s2) {
                return Float.compare(dateUtils.stringToCalendar(s1.getCompetition().getActivityDate()).getTimeInMillis(), dateUtils.stringToCalendar(s2.getCompetition().getActivityDate()).getTimeInMillis());
            }
        });

        ArrayList<Statistic> selectedStatistics = new ArrayList<>();
        for(int i = 0; i < this.statistics.size(); i++) {
            if(this.statistics.get(i).getCompetition().getSwimmingStyle().equals(this.selectedSwimmingStyle)) {
                selectedStatistics.add(this.statistics.get(i));
            }
        }

        DataPoint[] points = new DataPoint[selectedStatistics.size()];
        for(int i = 0; i < selectedStatistics.size(); i++) {
            points[i] = new DataPoint(i, selectedStatistics.get(i).getScore());
        }

/*        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(3000);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(59);*/

        //enable scrolling
        this.graphView.getViewport().setScrollable(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        this.graphView.addSeries(series);

        this.graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                }
                else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " €";
                }
            }
        });
    }

    private void setUpLengths() {
        Set<String> lengths = new HashSet<>();
        for(int i = 0; i < this.statistics.size(); i++) {
            if(this.statistics.get(i).getCompetition().getSwimmingStyle().equals(this.selectedSwimmingStyle)) {
                lengths.add(this.statistics.get(i).getCompetition().getLength() + " מטרים");
            }
        }

        this.lengths = new String[lengths.size() + 1];
        this.lengths[0] = "בחר מרחק";

        int i = 1;
        for(String length : lengths) {
            this.lengths[i++] = length;
        }

        this.selectedLength = "";
        setupLengthsSpinner();
    }

    private void setupStyles() {
        Set<String> styles = new HashSet<>();
        for(int i = 0; i < this.statistics.size(); i++) {
            styles.add(this.statistics.get(i).getCompetition().getSwimmingStyle());
        }

        this.swimmingStyles = new String[styles.size() + 1];
        this.swimmingStyles[0] = "בחר סגנון שחייה";

        int i = 1;
        for(String style : styles) {
            this.swimmingStyles[i++] = style;
        }
        setupStylesSpinner();
    }

    private void setupStylesSpinner() {
        this.styleSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.swimmingStyles) {

            @Override
            public boolean isEnabled(int position){
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        this.styleSpinnerListAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.styleSpinner.setAdapter(this.styleSpinnerListAdapter);

        this.styleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    selectedSwimmingStyle = swimmingStyles[position];
                    setUpLengths();
                    setUpGraphView();
                    System.out.println("position " + position);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLengthsSpinner() {
        this.lengthsSpinnerListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.lengths) {

            @Override
            public boolean isEnabled(int position){
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        this.lengthsSpinnerListAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.lengthsSpinner.setAdapter(this.lengthsSpinnerListAdapter);

        this.lengthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    selectedLength = lengths[position];
                    setUpGraphView();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void getParticipantStatistics() {
        try {
            showProgressDialog("טוען נתונים...");

            JSONObject data = new JSONObject();
            data.put("urlSuffix", "/getParticipantStatistics");
            data.put("httpMethod", "POST");
            data.put("uid", this.currentUser.getUid());

            this.jsonAsyncTaskPost = new JSON_AsyncTask();
            this.jsonAsyncTaskPost.delegate = this;
            this.jsonAsyncTaskPost.execute(data.toString());
        }
        catch(Exception e) {
            showToast("שגיאה ביצירת הבקשה למערכת, נסה לאתחל את האפליקציה ");
            System.out.println("MyPersonalInformationActivity Exception Stack Trace: " + Arrays.toString(e.getStackTrace()));
        }
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        toolbar.setTitle("צפה בהתקדמות שלך לפי סגנון");
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
