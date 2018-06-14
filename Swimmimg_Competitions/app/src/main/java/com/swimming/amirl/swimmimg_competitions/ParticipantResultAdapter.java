package com.swimming.amirl.swimmimg_competitions;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParticipantResultAdapter extends ArrayAdapter<JSONObject> {

    private Context mContext;
    private int mResource;
    private List<JSONObject> personalResults = new ArrayList<>();

    public ParticipantResultAdapter(Context context, int resource, ArrayList<JSONObject> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.personalResults = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.participant_result_list_item, parent, false);
        }

        try {
            JSONObject currentParticipant = personalResults.get(position);
            Participant participant = new Participant(currentParticipant);
            TextView participantName = listItem.findViewById(R.id.participant_name);
            participantName.setText(participant.getFirstName() + " " + participant.getLastName());
            System.out.println("ParticipantResultAdapter " + position + " " + currentParticipant);
        }
        catch (JSONException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        return listItem;
    }
}
