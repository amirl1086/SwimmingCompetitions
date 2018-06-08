package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class LiveResultsAdapter extends ArrayAdapter {

    private Context mContext;
    private int mResource;
    private List<PersonalResult> liveResults;

    public LiveResultsAdapter(Context context, int resource, ArrayList<PersonalResult> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.liveResults = list;
    }

    @NonNull @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.live_result_list_item, parent, false);
        }
        PersonalResult currentResult = this.liveResults.get(position);

        TextView participantName = listItem.findViewById(R.id.participant_name);
        participantName.setText(currentResult.getFirstName() + " " + currentResult.getLastName());

        TextView participantScore = listItem.findViewById(R.id.participant_score);
        participantScore.setText(currentResult.getScore());

        return listItem;
    }
}
