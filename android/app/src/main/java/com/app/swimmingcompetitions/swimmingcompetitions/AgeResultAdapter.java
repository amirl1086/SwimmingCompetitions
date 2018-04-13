package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgeResultAdapter extends ArrayAdapter<JSONObject> {

    private Context mContext;
    private int mResource;
    private List<JSONObject> personalResults;

    public AgeResultAdapter(Context context, int resource, ArrayList<JSONObject> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.personalResults = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.age_result_list_item, parent, false);
        }

        TextView ages = listItem.findViewById(R.id.result_list_item_ages);
        TextView malesHeader = listItem.findViewById(R.id.result_list_item_males);
        TextView femalesHeader = listItem.findViewById(R.id.result_list_item_females);


        try {
            JSONObject currentResult = personalResults.get(position);
            System.out.println("AgeResultAdapter POSITION " + position + " " + currentResult);

            JSONArray malesResultsJson = new JSONArray(currentResult.getString("males"));
            JSONArray femalesResultsJson = new JSONArray(currentResult.getString("females"));
            ArrayList<JSONObject> malesResults = new ArrayList<>();
            ArrayList<JSONObject> femalesResults = new ArrayList<>();
            String currentAge = "";

            if (malesResultsJson.length() > 0 || femalesResultsJson.length() > 0) {
                if(malesResultsJson.length() > 0) {
                    Participant participant = new Participant(malesResultsJson.getJSONObject(0));
                    malesHeader.setText("בנים");
                    currentAge = dateUtils.getAge(participant.getBirthDate());
                    System.out.println("participant.getBirthDate()) " + participant.getBirthDate() + " " + new Date(participant.getBirthDate()));
                    System.out.println("malesResultsJson AGE " + currentAge);
                    ages.setText( "גילאי " + currentAge);


                    TextView malesListView = listItem.findViewById(R.id.males_participants);
                    StringBuilder participantsStr = new StringBuilder();
                    for (int i = 0; i < malesResultsJson.length(); i++) {
                        malesResults.add(malesResultsJson.getJSONObject(i));
                        Participant currParticipant = new Participant(malesResultsJson.getJSONObject(i));
                        participantsStr.append(i + 1).append(". ").append(currParticipant.getScore()).append(", ").append(currParticipant.getFirstName()).append(" ").append(currParticipant.getLastName()).append("\n");
                    }
                    malesListView.setText(participantsStr);
                    /*ParticipantResultAdapter resultsListAdapter = new ParticipantResultAdapter(this, R.layout.participant_result_list_item, malesResults);
                    malesListView.setAdapter(resultsListAdapter);*/
                }
                else {
                    malesHeader.setVisibility(View.GONE);
                }
                if(femalesResultsJson.length() > 0) {
                    Participant participant = new Participant(femalesResultsJson.getJSONObject(0));
                    femalesHeader.setText("בנות");

                    if(currentAge.isEmpty()) {
                        System.out.println("participant.getBirthDate()) " + participant.getBirthDate() + " " + new Date(participant.getBirthDate()));
                        currentAge = dateUtils.getAge(participant.getBirthDate());
                        System.out.println("femalesResultsJson AGE " + currentAge);
                        ages.setText( "גילאי " + currentAge);
                    }

                    TextView femalesListView = listItem.findViewById(R.id.females_participants);
                    StringBuilder participantsStr = new StringBuilder();
                    for (int i = 0; i < femalesResultsJson.length(); i++) {
                        femalesResults.add(femalesResultsJson.getJSONObject(i));
                        Participant currParticipant = new Participant(femalesResultsJson.getJSONObject(i));
                        participantsStr.append(i + 1).append(". ").append(currParticipant.getScore()).append(", ").append(currParticipant.getFirstName()).append(" ").append(currParticipant.getLastName()).append("\n");
                    }
                    femalesListView.setText(participantsStr);
                    /*ParticipantResultAdapter resultsListAdapter = new ParticipantResultAdapter(ViewCompetitionResultsActivity.this, R.layout.participant_result_list_item, femalesResults);
                    femalesListView.setAdapter(resultsListAdapter);*/
                }
                else {
                    femalesHeader.setVisibility(View.GONE);
                }
            }
            else {
                ages.setVisibility(View.GONE);
            }
        }
        catch (JSONException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return listItem;
    }

    private void setTextView(TextView textView, String text, int textSize, int color, int alignment, int fontStyle) {
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(color);
        textView.setTypeface(null, fontStyle);
        textView.setGravity(alignment);
    }
}
