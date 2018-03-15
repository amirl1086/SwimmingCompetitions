package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewCompetitionResultsActivity extends AppCompatActivity {

    private User currentUser;
    private ListView listView;
    private ResultAdapter resultsListAdapter;
    private ArrayList<JSONObject> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_competition_results);

        this.listView = findViewById(R.id.results_list_items);
        this.results = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("currentUser") && intent.hasExtra("competitionResults")) {
            JSONObject dataObj = null;
            try {
                this.currentUser = (User) intent.getSerializableExtra("currentUser");

                dataObj = new JSONObject(intent.getStringExtra("competitionResults"));
                System.out.println(intent.getStringExtra("competitionResults"));
                Iterator<String> agesKeys = dataObj.keys();

                while (agesKeys.hasNext()) {
                    String currentAge = agesKeys.next();
                    JSONObject currentResult = new JSONObject(dataObj.get(currentAge).toString());
                    results.add(currentResult);
                }

                this.resultsListAdapter = new ResultAdapter(this, results);
                this.listView.setAdapter(this.resultsListAdapter);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
