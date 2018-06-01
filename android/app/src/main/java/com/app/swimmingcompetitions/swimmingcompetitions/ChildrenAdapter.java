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

public class ChildrenAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private List<Participant> children = new ArrayList<>();

    public ChildrenAdapter(Context context, int resource, ArrayList<Participant> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.children = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.competition_list_item, parent, false);
        }
        Participant currentUser = children.get(position);

        TextView userName = listItem.findViewById(R.id.user_name);
        userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

        TextView userBirthDate = listItem.findViewById(R.id.user_birth_date);
        userBirthDate.setText(currentUser.getBirthDate());

        return listItem;
    }
}
