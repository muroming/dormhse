package com.dorm.muro.dormitory.ScheduleCalendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dorm.muro.dormitory.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;
    private ArrayList<Date> days;
    private int currentMonth;

    public CalendarAdapter(Context context, ArrayList<Date> days)
    {
        super(context, R.layout.schedule_calendar_day, days);
        this.days = days;
        inflater = LayoutInflater.from(context);
    }

    public void setDays(ArrayList<Date> days) {
        this.days = days;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        // day in question
        Date date = days.get(position);
        Calendar c = Calendar.getInstance();
        c.setTime(date);


        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.schedule_calendar_day, parent, false);

        // set text
        TextView text = (TextView) view;
        text.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

        //if month equals to current month then set text color to black
        if(currentMonth == c.get(Calendar.MONTH)){
            text.setTextColor(Color.BLACK);
        } else { //else grey
            text.setTextColor(Color.GRAY);
        }

        return view;
    }
}
