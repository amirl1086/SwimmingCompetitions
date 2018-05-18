package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends LoadingDialog {

    private User currentUser;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setUpSidebar();

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
        }
        else {
            switchToLogInActivity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSidebar() {
        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("תפריט ראשי");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.competitions_nav_item: {
                        openViewCompetitionsActivity();
                        break;
                    }
                    case R.id.personal_results_nav_item: {
                        openViewResultsActivity();
                        break;
                    }
                    case R.id.statistics_nav_item: {
                        openViewStatisticsActivity();
                        break;
                    }
                    case R.id.real_time_nav_item: {
                        openViewInRealTimeActivity();
                        break;
                    }
                    case R.id.my_personal_info_nav_item: {
                        openMyPersonalInformationActivity();
                        break;
                    }
                    case R.id.my_children_nav_item: {
                        openMyChildrenActivity();
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

    private void openViewInRealTimeActivity() {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void openViewStatisticsActivity() {
        Intent intent = new Intent(this, ViewStatisticsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void openViewCompetitionsActivity() {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openViewResultsActivity() {
        Intent intent = new Intent(this, ViewPersonalResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openMyPersonalInformationActivity() {
        Intent intent = new Intent(this, MyPersonalInformationActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openMyChildrenActivity() {
        Intent intent = new Intent(this, MyChildrenActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void logOut() {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}
