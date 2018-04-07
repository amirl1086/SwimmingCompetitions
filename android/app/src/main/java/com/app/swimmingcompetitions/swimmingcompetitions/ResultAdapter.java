package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends ArrayAdapter<JSONObject> {

    private Context mContext;
    private int mResource;
    private List<JSONObject> personalResults = new ArrayList<>();
    private String[] places = {"1 - ", ", 2 - ", ", 3 - "};

    public ResultAdapter(Context context, int resource, ArrayList<JSONObject> list) {
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
            listItem = LayoutInflater.from(mContext).inflate(R.layout.result_list_item, parent, false);
        }

        TextView ages = listItem.findViewById(R.id.result_list_item_ages);
        TextView malesHeader = listItem.findViewById(R.id.result_list_item_males);
        TextView femalesHeader = listItem.findViewById(R.id.result_list_item_females);

        TextView itemMales1 = listItem.findViewById(R.id.result_list_item_males_1);
        TextView itemFemales1 = listItem.findViewById(R.id.result_list_item_females_1);
        TextView itemMales2 = listItem.findViewById(R.id.result_list_item_males_2);
        TextView itemFemales2 = listItem.findViewById(R.id.result_list_item_females_2);
        TextView itemMales3 = listItem.findViewById(R.id.result_list_item_males_3);
        TextView itemFemales3 = listItem.findViewById(R.id.result_list_item_females_3);

        ArrayList<TextView> itemMales = new ArrayList<>();
        itemMales.add(itemMales1);
        itemMales.add(itemMales2);
        itemMales.add(itemMales3);

        ArrayList<TextView> itemFemales = new ArrayList<>();
        itemFemales.add(itemFemales1);
        itemFemales.add(itemFemales2);
        itemFemales.add(itemFemales3);


        try {
            JSONObject currentResult = personalResults.get(position);

            JSONArray malesResultsJson = new JSONArray(currentResult.getString("males"));
            JSONArray femalesResultsJson = new JSONArray(currentResult.getString("females"));

            if (malesResultsJson.length() > 0 || femalesResultsJson.length() > 0) {

                Participant participant = null;
                if(malesResultsJson.length() > 0) {
                    participant = new Participant(malesResultsJson.getJSONObject(0));
                    setTextView(malesHeader, "בנים:", 24, Color.BLACK, Gravity.CENTER, Typeface.BOLD);

                }
                if(femalesResultsJson.length() > 0) {
                    participant = new Participant(femalesResultsJson.getJSONObject(0));
                    setTextView(femalesHeader, "בנות:", 24, Color.BLACK, Gravity.CENTER, Typeface.BOLD);
                }

                String currentAge = dateUtils.getAge(participant.getBirthDate());
                setTextView(ages, "גילאי " + currentAge, 28, Color.BLACK, Gravity.CENTER, Typeface.BOLD_ITALIC);


                for (int i = 0; i < malesResultsJson.length(); i++) {
                    Participant maleParticipant = new Participant(femalesResultsJson.getJSONObject(i));
                    setTextView(itemMales.get(i), maleParticipant.toString(), 24, Color.BLACK, Gravity.CENTER, Typeface.NORMAL);
                }

                for (int i = 0; i < femalesResultsJson.length(); i++) {
                    Participant femaleParticipant = new Participant(femalesResultsJson.getJSONObject(i));
                    setTextView(itemFemales.get(i), femaleParticipant.toString(), 24, Color.BLACK, Gravity.CENTER, Typeface.NORMAL);
                }
            }
        }
        catch (JSONException e) {

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
