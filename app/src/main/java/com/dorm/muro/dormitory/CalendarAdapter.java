package com.dorm.muro.dormitory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, ArrayList<Date> days)
    {
        super(context, R.layout.schedule_calendar_day, days);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        // day in question
        Date date = getItem(position);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // today
        Date today = new Date();

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.schedule_calendar_day, parent, false);

        // set text
            ((TextView)view).setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

        return view;
    }
}
