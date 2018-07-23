package com.swimming.amirl.swimmimg_competitions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class ParticipantsSelectionActivity extends AppCompatActivity {

    private User currentUser;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private ParticipantAdapter participantListAdapter;

    private Competition selectedCompetition;
    private ArrayList<Participant> participants;
    private ArrayList<Participant> selectableParticipants;
    private ArrayList<Participant> selectedParticipants;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_selection);

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("selectedCompetition")) {
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");
                this.mAuth = FirebaseAuth.getInstance();
                this.fbUser = this.mAuth.getCurrentUser();
                this.selectedCompetition = (Competition) intent.getSerializableExtra("selectedCompetition");
                this.participants = this.selectedCompetition.getParticipants();
                this.selectableParticipants = new ArrayList<>();

                for(int i = 0; i < this.participants.size(); i++) {
                    if(this.participants.get(i).isCompeted().equals("false")) {
                        this.participants.get(i).setSelected(false);
                        selectableParticipants.add(this.participants.get(i));
                    }
                }

                this.listView = findViewById(R.id.participants_list);

                this.participantListAdapter = new ParticipantAdapter(this, R.layout.participant_list_item, this.selectableParticipants);
                this.listView.setAdapter(this.participantListAdapter);

                this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        selectedParticipants.add(selectableParticipants.get(position));
                    }
                });

            }
            catch (Exception e) {
                showToast("שגיאה באתחול רשימת המתחרים, נסה לאתחל את האפליקציה");
                System.out.println("ParticipantsSelectionActivity onCreate Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        else {
            switchToLogInActivity();
        }

    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void switchToLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
