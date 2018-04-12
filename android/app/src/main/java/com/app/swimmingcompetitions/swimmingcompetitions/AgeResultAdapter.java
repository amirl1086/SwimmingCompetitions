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
import java.util.ArrayList;
import java.util.List;

public class AgeResultAdapter extends ArrayAdapter<JSONObject> {

    private Context mContext;
    private int mResource;
    private List<JSONObject> personalResults = new ArrayList<>();

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

            if (malesResultsJson.length() > 0 || femalesResultsJson.length() > 0) {
                if(malesResultsJson.length() > 0) {
                    ListView malesListView = listItem.findViewById(R.id.males_participants);
                    setTextView(malesHeader, "בנים", 24, Color.BLACK, Gravity.CENTER, Typeface.BOLD);
                    for (int i = 0; i < malesResultsJson.length(); i++) {
                        malesResults.add(malesResultsJson.getJSONObject(i));
                    }
                    ParticipantResultAdapter resultsListAdapter = new ParticipantResultAdapter(this.mContext, R.layout.participant_result_list_item, malesResults);
                    malesListView.setAdapter(resultsListAdapter);
                }
                if(femalesResultsJson.length() > 0) {
                    ListView femalesListView = listItem.findViewById(R.id.females_participants);
                    setTextView(femalesHeader, "בנות", 24, Color.BLACK, Gravity.CENTER, Typeface.BOLD);
                    for (int i = 0; i < femalesResultsJson.length(); i++) {
                        femalesResults.add(femalesResultsJson.getJSONObject(i));
                    }
                    ParticipantResultAdapter resultsListAdapter = new ParticipantResultAdapter(this.mContext, R.layout.participant_result_list_item, femalesResults);
                    femalesListView.setAdapter(resultsListAdapter);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return listItem;
    }

    private TextView setTextView(TextView textView, String text, int textSize, int color, int alignment, int fontStyle) {
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(color);
        textView.setTypeface(null, fontStyle);
        textView.setGravity(alignment);
        return textView;
    }
}
