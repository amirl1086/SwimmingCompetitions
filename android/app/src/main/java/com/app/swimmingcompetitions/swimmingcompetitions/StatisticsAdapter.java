package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private List<Competition> competitions;

    public StatisticsAdapter(Context context, int resource, ArrayList<Competition> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        competitions = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.statistics_list_item, parent, false);
        }
        Competition currentCompetition = competitions.get(position);

        TextView competitionName = listItem.findViewById(R.id.competition_list_item_name);
        competitionName.setText(currentCompetition.getName());

        TextView competitionDate = listItem.findViewById(R.id.competition_list_item_date);
        Calendar calendar = dateUtils.dateToCalendar(new Date(currentCompetition.getActivityDate()));
        competitionDate.setText(dateUtils.getCompleteDate(calendar));


        return listItem;
    }
}
