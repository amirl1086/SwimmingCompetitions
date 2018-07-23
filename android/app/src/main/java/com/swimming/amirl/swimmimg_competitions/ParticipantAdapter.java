package com.swimming.amirl.swimmimg_competitions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private List<Participant> participants;
    private Participant currParticipant;

    public ParticipantAdapter(Context context, int resource, ArrayList<Participant> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.participants = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.participant_list_item, parent, false);
        }
        currParticipant = this.participants.get(position);

        TextView participantName = listItem.findViewById(R.id.participant_name);
        participantName.setText(currParticipant.getFirstName() + " " + currParticipant.getLastName());

        //TextView participantAge = listItem.findViewById(R.id.participant_age);
        //participantAge.setText(dateUtils.getAgeByDate(currParticipant.getBirthDate()));

        CheckBox selected = listItem.findViewById(R.id.checkbox_select);

        listItem.setTag(position);
        currParticipant.setSelected(!currParticipant.getSelected());
        selected.setSelected(currParticipant.getSelected());
        return listItem;
    }
}
