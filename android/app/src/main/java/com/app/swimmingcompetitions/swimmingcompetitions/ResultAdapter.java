package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by amirl on 3/7/2018.
 */

public class ResultAdapter extends ArrayAdapter<JSONObject> {
    private Context mContext;
    private List<JSONObject> personalResults = new ArrayList<>();
    private String[] places = {"1 - ", ", 2 - ", ", 3 - "};

    public ResultAdapter(@NonNull Context context, @LayoutRes ArrayList<JSONObject> list) {
        super(context, 0, list);
        this.mContext = context;
        personalResults = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.result_list_item, parent, false);
        }

        TextView ages = listItem.findViewById(R.id.result_list_item_ages);
        TextView malesHeader = listItem.findViewById(R.id.result_list_item_males);
        TextView malesResults = listItem.findViewById(R.id.result_list_item_males_results);
        TextView femalesHeader = listItem.findViewById(R.id.result_list_item_females);
        TextView femalesResults = listItem.findViewById(R.id.result_list_item_females_results);

        try {
            JSONObject currentResult = personalResults.get(position);

            JSONArray malesResultsJson = new JSONArray(currentResult.getString("males"));
            JSONArray femalesResultsJson = new JSONArray(currentResult.getString("females"));

            if (malesResultsJson.length() > 0 || femalesResultsJson.length() > 0) {
                ArrayList<Participant> maleParticipants = new ArrayList<>();
                ArrayList<Participant> femaleParticipants = new ArrayList<>();


                for (int i = 0; i < malesResultsJson.length(); i++) {
                    String firstName = malesResultsJson.getJSONObject(i).getString("firstName");
                    Participant currParticipant = new Participant(malesResultsJson.getJSONObject(i));
                    maleParticipants.add(currParticipant);
                }

                for (int i = 0; i < femalesResultsJson.length(); i++) {
                    femaleParticipants.add(new Participant(new JSONObject(femalesResultsJson.get(i).toString())));
                }

                String maleResultsStr = "";
                String femaleResultsStr = "";

                if (maleParticipants.size() > 0) {
                    String currentAge = dateUtils.getAge(maleParticipants.get(0).getBirthDate());
                    ages.setText("עבור גילאי " + currentAge + ":");

                    malesHeader.setText("בנים: ");
                    for (int i = 0; i < maleParticipants.size(); i++) {
                        maleResultsStr += (places[i] + maleParticipants.get(i).toString());
                    }
                    malesResults.setText(maleResultsStr);
                }
                else {
                    malesHeader.setVisibility(View.GONE);
                    malesResults.setVisibility(View.GONE);
                }

                if (femaleParticipants.size() > 0) {
                    String currentAge = dateUtils.getAge(femaleParticipants.get(0).getBirthDate());
                    femalesHeader.setText("בנות: ");

                    ages.setText("עבור גילאי " + currentAge + ":");
                    for (int i = 0; i < femaleParticipants.size(); i++) {
                        femaleResultsStr += (places[i] + femaleParticipants.get(i).toString());
                    }
                    femalesResults.setText(femaleResultsStr);
                }
                else {
                    femalesResults.setVisibility(View.GONE);
                    femalesHeader.setVisibility(View.GONE);
                }

            }
            else {
                femalesResults.setVisibility(View.GONE);
                femalesHeader.setVisibility(View.GONE);
                malesHeader.setVisibility(View.GONE);
                malesResults.setVisibility(View.GONE);
                ages.setVisibility(View.GONE);
            }
        }
        catch (JSONException e) {

        }

        return listItem;
    }
}
