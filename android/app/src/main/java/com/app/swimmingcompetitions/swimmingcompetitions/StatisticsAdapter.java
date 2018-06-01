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

public class StatisticsAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private List<Statistic> statistics;

    public StatisticsAdapter(Context context, int resource, ArrayList<Statistic> list) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.statistics = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.statistics_list_item, parent, false);
        }
        Statistic currentStatistics = this.statistics.get(position);

        TextView competitionName = listItem.findViewById(R.id.competition_name);
        competitionName.setText(currentStatistics.getCompetition().getName());

        TextView competitionDate = listItem.findViewById(R.id.competition_date);
        competitionDate.setText(dateUtils.getFullDate(currentStatistics.getCompetition().getActivityDate()) + ", ");

        System.out.println("score: " + currentStatistics.getScore());

        /*TextView scoreResult = listItem.findViewById(R.id.statistics_score_result);
        scoreResult.setText(currentStatistics.getScore());*/

        return listItem;
    }
}
