package com.swimming.amirl.swimmimg_competitions;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class HomePageActivity extends LoadingDialog {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent();
        if(intent.hasExtra("currentUser")) {
            this.currentUser = (User) intent.getSerializableExtra("currentUser");
            this.mAuth = FirebaseAuth.getInstance();
            this.fbUser = this.mAuth.getCurrentUser();
            RelativeLayout tokenButton = findViewById(R.id.token_btn);

            TextView header = findViewById(R.id.home_page_header);
            header.setText("שלום " + this.currentUser.getFirstName() + " " + this.currentUser.getLastName());

            if(!this.currentUser.getType().equals("coach")) {
                tokenButton.setVisibility(View.GONE);
            }

            if(this.currentUser.getType().isEmpty()) {
                switchToGoogleRegisterActivity();
            }
            else {
                setUpSidebar();
            }
        }
        else {
            switchToLogInActivity();
        }
    }

    public void displayToken(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView showText = new TextView(this);
        showText.setText("העתק את המפתח ושלח אותו לנרשמים \n\n" + currentUser.getToken());
        showText.setTextIsSelectable(true);
        showText.setTextSize(18);
        showText.setGravity(Gravity.CENTER);
        showText.setPadding(0, 20, 0, 0);
        showText.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // Copy the Text to the clipboard
                ClipboardManager manager =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                manager.setText(currentUser.getToken());
                // Show a message:
                Toast.makeText(v.getContext(), "המפתח הועתק לטבלת ההעתקות (Clipboard)",
                        Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });
        builder.setView(showText);
        builder.setPositiveButton("סגור", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {} });
        builder.show();
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onResume() {
        super.onResume();
        if(this.fbUser == null) {
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
                        switchToViewCompetitionsActivity(null);
                        break;
                    }
                    case R.id.personal_results_nav_item: {
                        switchToViewResultsActivity(null);
                        break;
                    }
                    case R.id.statistics_nav_item: {
                        switchToViewStatisticsActivity(null);
                        break;
                    }
                    case R.id.real_time_nav_item: {
                        switchToViewInRealTimeActivity(null);
                        break;
                    }
                    case R.id.media_nav_item: {
                        switchToViewMediaActivity(null);
                        break;
                    }
                    case R.id.settings_nav_item: {
                        switchToMySettingsActivity(null);
                        break;
                    }
                    case R.id.log_out_nav_item: {
                        logOut(null);
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

    public void switchToViewInRealTimeActivity(View v) {
        Intent intent = new Intent(this, ViewInRealTimeActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void switchToGoogleRegisterActivity() {
        Intent intent = new Intent(this, PreRegisterActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToMySettingsActivity(View v) {
        Intent intent = new Intent(this, MySettingsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewMediaActivity(View v) {
        Intent intent = new Intent(this, ViewMediaActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewStatisticsActivity(View v) {
        Intent intent = new Intent(this, ViewStatisticsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewCompetitionsActivity(View v) {
        Intent intent = new Intent(this, ViewCompetitionsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void switchToViewResultsActivity(View v) {
        Intent intent = new Intent(this, ViewPersonalResultsActivity.class);
        intent.putExtra("currentUser", this.currentUser);
        startActivity(intent);
    }

    public void logOut(View v) {
        this.mAuth.signOut();
        switchToLogInActivity();
    }
}

