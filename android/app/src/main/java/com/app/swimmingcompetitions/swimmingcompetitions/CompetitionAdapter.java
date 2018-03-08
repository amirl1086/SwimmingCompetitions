package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CompetitionAdapter extends ArrayAdapter<Competition> {

    private Context mContext;
    private List<Competition> competitions = new ArrayList<>();

    public CompetitionAdapter(@NonNull Context context, @LayoutRes ArrayList<Competition> list) {
        super(context, 0, list);
        this.mContext = context;
        competitions = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.competition_list_item, parent, false);
        }
        Competition currentCompetition = competitions.get(position);

        TextView competitionName = listItem.findViewById(R.id.competition_list_item_name);
        competitionName.setText(currentCompetition.getName());

        TextView competitionDate = listItem.findViewById(R.id.competition_list_item_date);
        Calendar calendar = dateUtils.dateToCalendar(currentCompetition.getActivityDate());
        competitionDate.setText(dateUtils.getCompleteHebrewDate(calendar));

        TextView competitionAges = listItem.findViewById(R.id.competition_list_item_ages);
        competitionAges.setText(currentCompetition.getAgesString());

        TextView competitionStyle = listItem.findViewById(R.id.competition_list_item_style);
        competitionStyle.setText(currentCompetition.getSwimmingStyle());

        TextView competitionLength = listItem.findViewById(R.id.competition_list_item_length);
        competitionLength.setText(currentCompetition.getLength());

        return listItem;
    }
}
