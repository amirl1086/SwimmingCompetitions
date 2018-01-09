package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class CreateNewCompetitionActivity extends AppCompatActivity {

    private ArrayList<String> iterations;
    private ArrayAdapter listAdapter;

    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_competition);

        dateView = (TextView) findViewById(R.id.competition_date);

        numberPicker = findViewById(R.id.meters_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(1000);

        ListView listView = (ListView) findViewById(R.id.iterations_list) ;
        String[] items = {"apple", "banana", "grape", "avielIsGay"};

        iterations = new ArrayList<String>(Arrays.asList(items));
        listAdapter = new ArrayAdapter<String>(this, R.layout.iteration_list_item, R.id.iteration_item, items);
        listView.setAdapter(listAdapter);

        //ArrayAdapter adapter = new ArrayAdapter<String>(this, an)
    }

    public void setDate(View view) {
        showDialog(R.integer.dialog_id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.integer.dialog_id) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    public void openNewIterationActivity(View view) {
        Intent intent = new Intent(this, NewIterationActivity.class);
        startActivity(intent);
    }

    public class Competition {

    }

    public class Iteration {

    }
}
