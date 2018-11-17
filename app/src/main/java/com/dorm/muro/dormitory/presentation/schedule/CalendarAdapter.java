package com.dorm.muro.dormitory.presentation.schedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dorm.muro.dormitory.R;

import org.joda.time.DateTimeComparator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends ArrayAdapter<ScheduleCell> {
    private LayoutInflater inflater;
    private ArrayList<ScheduleCell> days;
    private int currentMonth;
    private WeakReference<Context> context;
    private ScheduleFragmentView callback;

    private boolean isCurrentDaySet;

    CalendarAdapter(Context context, ScheduleFragmentView callback) {
        super(context, R.layout.schedule_calendar_day);
        this.context = new WeakReference<>(context);
        this.callback = callback;
        days = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setDays(ArrayList<ScheduleCell> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public void setDateRangeDuty(Date startDate, Date endDate, ROOM_NUM roomNum) {
        int startInd = -1;
        for (int i = 0; i < days.size(); i++) {
            ScheduleCell cell = days.get(i);
            if (startInd == -1 && cell.getDate().equals(startDate)) {
                startInd = i;
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.START);
                continue;
            }
            if (startInd != -1) {
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
            }
            if (cell.getDate().equals(endDate)) {
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.END);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void updateStartDuty(Date prevStart, Date newStart) {
        if (newStart.getTime() < prevStart.getTime()) {  // If clicked behind start add new cells
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() > newStart.getTime()) {
                    cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
                    if (cell.getDate().equals(prevStart)) {
                        break;
                    }
                }
            }
        } else {  // Clicked after start remove
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() >= prevStart.getTime()) {
                    if (cell.getDate().equals(newStart)) {
                        break;
                    }
                    cell.setState(ScheduleFragment.CELL_STATE.NONE);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateEndDuty(Date prevEnd, Date newEnd) {
        if (newEnd.getTime() > prevEnd.getTime()) {  // If clicked after end add new cells
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() >= prevEnd.getTime()) {
                    if (cell.getDate().equals(newEnd)) {
                        break;
                    }
                    cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
                }
            }
        } else {  // Clicked before start remove
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() > newEnd.getTime()) {
                    cell.setState(ScheduleFragment.CELL_STATE.NONE);
                    if (cell.getDate().equals(prevEnd)) {
                        break;
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateDate(ScheduleCell cell, ROOM_NUM roomNum) {
        for (ScheduleCell c : days) {
            if (c.equals(cell)) {
                c.setState(cell.getState());
                c.setRoomNum(roomNum);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // day in question
        ScheduleCell date = days.get(position);
        Calendar c = Calendar.getInstance();
        c.setTime(date.getDate());


        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.schedule_calendar_day, parent, false);


        // set text
        TextView text = view.findViewById(R.id.tv_schedule_day);
        text.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

        // Set current day background
        if (!isCurrentDaySet) {
            Date today = new Date();
            if (DateTimeComparator.getDateOnlyInstance().compare(c.getTime(), today) == 0) {
                text.setBackground(context.get().getDrawable(R.drawable.current_date_selector));
                isCurrentDaySet = true;
            } else {
                text.setBackgroundResource(0);
            }
        }

        //if month equals to current month then set text color to black
        if (currentMonth == c.get(Calendar.MONTH)) {
            text.setTextColor(Color.BLACK);
        } else { //else grey
            text.setTextColor(Color.GRAY);
        }

        // Set color of duty range
        int color = -1;
        switch (date.getRoomNum()) {
            case FIRST: {
                color = ContextCompat.getColor(context.get(), R.color.first_room_duty);
                break;
            }
            case SECOND: {
                color = ContextCompat.getColor(context.get(), R.color.second_room_duty);
                break;
            }
            case THIRD: {
                color = ContextCompat.getColor(context.get(), R.color.third_room_duty);
                break;
            }
            case FORTH: {
                color = ContextCompat.getColor(context.get(), R.color.forth_room_duty);
                break;
            }
        }

        switch (date.getState()) {
            case START: {
                view.setBackgroundResource(R.drawable.duty_range_start);
                break;
            }

            case END: {
                view.setBackgroundResource(R.drawable.duty_range_end);
                break;
            }

            case MEDIUM: {
                view.setBackgroundColor(color);
                break;
            }
            case NONE: {
                view.setBackgroundResource(0);
            }
        }

        // If start or end then set
        if (date.getState() != ScheduleFragment.CELL_STATE.NONE && date.getState() != ScheduleFragment.CELL_STATE.MEDIUM) {
            Drawable background = view.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(color);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(color);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(color);
            }
        }


        // Set view on click listener
        view.setOnClickListener(v -> {
            if (c.get(Calendar.MONTH) == currentMonth)
                callback.onDateClicked(date);
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        isCurrentDaySet = false;
    }
}
