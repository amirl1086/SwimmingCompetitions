package com.swimming.amirl.swimmimg_competitions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
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

            JSONArray malesResultsJson = new JSONArray(currentResult.getString("males"));
            JSONArray femalesResultsJson = new JSONArray(currentResult.getString("females"));
            ArrayList<JSONObject> malesResults = new ArrayList<>();
            ArrayList<JSONObject> femalesResults = new ArrayList<>();
            String currentAge = "";

            if (malesResultsJson.length() > 0 || femalesResultsJson.length() > 0) {
                if(malesResultsJson.length() > 0) {

                    Participant participant = new Participant(malesResultsJson.getJSONObject(0));
                    malesHeader.setText("בנים");
                    currentAge = String.valueOf(dateUtils.getAgeByDate(participant.getBirthDate()));
                    ages.setText( "גילאי " + currentAge);


                    TextView malesListView = listItem.findViewById(R.id.males_participants);
                    StringBuilder participantsStr = new StringBuilder();
                    for (int i = 0; i < malesResultsJson.length(); i++) {
                        malesResults.add(malesResultsJson.getJSONObject(i));
                        Participant currParticipant = new Participant(malesResultsJson.getJSONObject(i));
                        participantsStr.append(i + 1).append(". ").append(currParticipant.getFirstName()).append(" ").append(currParticipant.getLastName()).append(" - ").append(currParticipant.getScore()).append(" שניות").append("\n");
                    }
                    malesListView.setText(participantsStr);
                }
                else {
                    malesHeader.setVisibility(View.GONE);
                }
                if(femalesResultsJson.length() > 0) {
                    Participant participant = new Participant(femalesResultsJson.getJSONObject(0));
                    femalesHeader.setText("בנות");

                    if(currentAge.isEmpty()) {
                        currentAge = String.valueOf(dateUtils.getAgeByDate(participant.getBirthDate()));
                        ages.setText("גילאי " + currentAge);
                    }

                    TextView femalesListView = listItem.findViewById(R.id.females_participants);
                    StringBuilder participantsStr = new StringBuilder();
                    for (int i = 0; i < femalesResultsJson.length(); i++) {
                        femalesResults.add(femalesResultsJson.getJSONObject(i));
                        Participant currParticipant = new Participant(femalesResultsJson.getJSONObject(i));
                        participantsStr.append(i + 1).append(". ").append(currParticipant.getFirstName()).append(" ").append(currParticipant.getLastName()).append(" - ").append(currParticipant.getScore()).append(" שניות").append("\n");
                    }
                    femalesListView.setText(participantsStr);
                }
                else {
                    femalesHeader.setVisibility(View.GONE);
                }
            }
            else {
                ages.setVisibility(View.GONE);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return listItem;
    }
}
