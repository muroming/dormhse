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

    void setDays(ArrayList<ScheduleCell> days) {
        this.days = days;
        notifyDataSetChanged();
    }



    void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    void setDateRangeDuty(Date startDate, Date endDate, ROOM_NUM roomNum) {
        int startInd = -1;
        for (int i = 0; i < days.size(); i++) {
            ScheduleCell cell = days.get(i);
            if (startInd == -1 && cell.equals(startDate)) {
                startInd = i;
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.START);
                continue;
            }
            if (startInd != -1) {
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
            }
            if (cell.equals(endDate)) {
                cell.setRoomNum(roomNum);
                cell.setState(ScheduleFragment.CELL_STATE.END);
                break;
            }
        }

        notifyDataSetChanged();
    }

    void updateStartDuty(Date prevStart, Date newStart, ROOM_NUM roomNum) {
        if (newStart.getTime() < prevStart.getTime()) {  // If clicked behind start add new cells
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() > newStart.getTime()) {
                    cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
                    cell.setRoomNum(roomNum);
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

    void updateEndDuty(Date prevEnd, Date newEnd, ROOM_NUM roomNum) {
        if (newEnd.getTime() > prevEnd.getTime()) {  // If clicked after end add new cells
            for (int i = 0; i < days.size(); i++) {
                ScheduleCell cell = days.get(i);
                if (cell.getDate().getTime() >= prevEnd.getTime()) {
                    if (cell.getDate().equals(newEnd)) {
                        break;
                    }
                    cell.setState(ScheduleFragment.CELL_STATE.MEDIUM);
                    cell.setRoomNum(roomNum);
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

    void updateDate(ScheduleCell cell) {
        for (ScheduleCell c : days) {
            if (c.equals(cell)) {
                cell.setState(cell.getState());
                cell.setRoomNum(cell.getRoomNum());
                break;
            }
        }
        notifyDataSetChanged();
    }

    void deleteRange(Date start, Date end) {
        boolean flag = false;
        for (int i = 0; i < days.size(); i++) {
            ScheduleCell cell = days.get(i);
            if (flag) {
                cell.setState(ScheduleFragment.CELL_STATE.NONE);
            }
            if (cell.equals(start)) {
                cell.setState(ScheduleFragment.CELL_STATE.NONE);
                flag = true;
            }
            if (cell.equals(end)) {
                cell.setState(ScheduleFragment.CELL_STATE.NONE);
                break;
            }
        }

        notifyDataSetChanged();
    }

    ScheduleCell getRangeStart(ScheduleCell dateClicked) {
        ScheduleCell rangeStart = null;
        for (int i = 0; i < days.size(); i++) {
            ScheduleCell cell = days.get(i);
            if (cell.getState() == ScheduleFragment.CELL_STATE.START)
                rangeStart = cell;

            if (cell == dateClicked) {
                break;
            }
        }
        return rangeStart;
    }

    ScheduleCell getRangeEnd(ScheduleCell dateClicked) {
        ScheduleCell rangeEnd = null;
        for (int i = 0; i < days.size(); i++) {
            ScheduleCell cell = days.get(i);
            if (cell.getState() == ScheduleFragment.CELL_STATE.END && cell.getDate().getTime() >= dateClicked.getDate().getTime()) {
                rangeEnd = cell;
                break;
            }
        }
        return rangeEnd;
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
        int color = date.getColor() != 0 ? ContextCompat.getColor(context.get(), date.getColor()) : 0;

        // Set background
        if (date.getState() != ScheduleFragment.CELL_STATE.MEDIUM)
            view.setBackgroundResource(date.getDrawable());
        else
            view.setBackgroundColor(color);

        if (date.getState() != ScheduleFragment.CELL_STATE.NONE) {
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
                if (date.getState() == ScheduleFragment.CELL_STATE.NONE) {  //Clicked on empty cell
                    callback.onDateClicked(date, null, null);
                } else {
                    callback.onDateClicked(date, getRangeStart(date), getRangeEnd(date));
                }
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        isCurrentDaySet = false;
    }
}
