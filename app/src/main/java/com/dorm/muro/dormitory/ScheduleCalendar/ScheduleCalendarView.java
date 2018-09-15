package com.dorm.muro.dormitory.ScheduleCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dorm.muro.dormitory.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleCalendarView extends LinearLayout {
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    // internal components
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    //Grid adapter
    private CalendarAdapter gridAdapter;

    public ScheduleCalendarView(Context context) {
        super(context);
    }

    public ScheduleCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public ScheduleCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.schedule_calendar, this);

        initUI();
        addListeners();

        //as default set calender month to current
        Calendar c = (Calendar) currentDate.clone();
        updateCalendar(c.get(Calendar.MONTH));
    }

    private void addListeners() {
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar(currentDate.get(Calendar.MONTH));
            }
        });
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar(currentDate.get(Calendar.MONTH));
            }
        });
    }

    private void initUI() {
        btnPrev =  findViewById(R.id.iv_schedule_prev_month);
        btnNext = findViewById(R.id.iv_schedule_next_month);
        txtDate =  findViewById(R.id.tv_schedule_date_title);
        grid =  findViewById(R.id.gv_schedule_grid);
    }

    private void updateCalendar(int month) {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        calendar.set(Calendar.MONTH, month);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        if(gridAdapter == null) {
            gridAdapter = new CalendarAdapter(getContext(), cells);
            gridAdapter.setCurrentMonth(month);
            grid.setAdapter(gridAdapter);
        } else {
            gridAdapter.setDays(cells);
            gridAdapter.setCurrentMonth(month);
            gridAdapter.notifyDataSetChanged();
        }
        
        calendar.set(Calendar.MONTH, month);
        txtDate.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    }
}
