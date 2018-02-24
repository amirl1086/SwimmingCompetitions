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

/**
 * Created by amirl on 2/24/2018.
 */

public class PersonalResultAdapter extends ArrayAdapter<PersonalResult> {
    private Context mContext;
    private ArrayList<PersonalResult> personalResults = new ArrayList<>();

    public PersonalResultAdapter(@NonNull Context context, @LayoutRes ArrayList<PersonalResult> list) {
        super(context, 0, list);
        this.mContext = context;
        this.personalResults = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        DateUtils dateUtils = new DateUtils();

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.competition_list_item, parent, false);
        }
        PersonalResult currentResult = personalResults.get(position);








        /*TextView competitionName = listItem.findViewById(R.id.competition_list_item_name);
        competitionName.setText(currentResult.getName());

        TextView competitionDate = listItem.findViewById(R.id.competition_list_item_date);
        Calendar calendar = dateUtils.dateToCalendar(currentResult.getActivityDate());
        competitionDate.setText(dateUtils.getCompleteHebrewDate(calendar));

        TextView competitionAges = listItem.findViewById(R.id.competition_list_item_ages);
        competitionAges.setText(currentResult.getAgesString());

        TextView competitionStyle = listItem.findViewById(R.id.competition_list_item_style);
        competitionStyle.setText(currentResult.getSwimmingStyle());

        TextView competitionLength = listItem.findViewById(R.id.competition_list_item_length);
        competitionLength.setText(currentResult.getLength().toString());*/

        return listItem;
    }
}
