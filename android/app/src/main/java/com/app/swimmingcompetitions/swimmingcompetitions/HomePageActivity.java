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

        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);

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

    private void setUpSidebar() {


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
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                return true;
            }
        });
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

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void openViewCompetitionsActivity(View view) {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openViewResultsActivity(View view) {
        Intent intent = new Intent(this, ViewPersonalResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void openMySettingsActivity(View view) {
        Intent intent = new Intent(this, MySettingsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void logOut(View view) {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}
