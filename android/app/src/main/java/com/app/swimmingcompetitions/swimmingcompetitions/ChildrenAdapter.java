package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

public class ChildrenAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private List<Competition> competitions = new ArrayList<>();

    public ChildrenAdapter(Context context, int resource, ArrayList<Competition> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        competitions = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.competition_list_item, parent, false);
        }

        return listItem;
    }
}
