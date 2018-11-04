package com.dorm.muro.dormitory.presentation.schedule;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */

enum ROOM_NUM {
    FIRST(0), SECOND(1), THIRD(2), FORTH(3);
    private final int value;

    ROOM_NUM(int v) {
        value = v;
    }

    public int get() {
        return value;
    }
}

public class ScheduleFragment extends MvpAppCompatFragment implements ScheduleFragmentView {

    private static final int DAYS_COUNT = 42;

    public enum CELL_STATE {START, MEDIUM, END, NONE}

    // current displayed month
    private Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    // internal components
    @BindView(R.id.iv_schedule_prev_month)
    ImageView btnPrev;

    @BindView(R.id.iv_schedule_next_month)
    ImageView btnNext;

    @BindView(R.id.tv_schedule_date_title)
    TextView txtDate;

    @BindView(R.id.gv_schedule_grid)
    GridView grid;

    //Grid adapter
    private CalendarAdapter gridAdapter;

    // Calendar State
    private ScheduleCell rangeStartDate;
    private ROOM_NUM currentRoom;


    @BindViews({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    List<TextView> mRooms;

    @BindView(R.id.tv_schedule_commentary)
    TextView mRoomCommentary;

    private int currentSelectedRoom;

    @InjectPresenter
    ScheduleFragmentPresenter presenter;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);

        gridAdapter = new CalendarAdapter(getContext(), this);
        grid.setAdapter(gridAdapter);
        presenter.onRoomClicked(ROOM_NUM.FIRST);

        //as default set calender month to current
        Calendar c = (Calendar) currentDate.clone();
        updateCalendar(c.get(Calendar.MONTH));

        addListeners();

        currentSelectedRoom = 0;
        return view;
    }

    private void addListeners() {
        btnNext.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, 1);
            updateCalendar(currentDate.get(Calendar.MONTH));
        });

        btnPrev.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            updateCalendar(currentDate.get(Calendar.MONTH));
        });
    }

    @OnClick({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    public void onRoomClicked(TextView room) {
        int roomNum = mRooms.indexOf(room);
        presenter.onRoomClicked(ROOM_NUM.values()[roomNum]);
    }

    @Override
    public void setRoomSelected(ROOM_NUM roomNum) {
        mRooms.get(currentSelectedRoom).setBackgroundResource(0);
        mRooms.get(currentSelectedRoom).setTextColor(Color.BLACK);

        TextView mRoom = mRooms.get(roomNum.get());

        mRoom.setBackgroundResource(R.drawable.selected_room);
        GradientDrawable background = (GradientDrawable) mRoom.getBackground();

        switch (roomNum) {
            case FIRST: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.first_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.first_room_selection_text, null));
                break;
            }
            case SECOND: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.second_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.second_room_selection_text, null));
                break;
            }
            case THIRD: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.third_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.third_room_selection_text, null));
                break;
            }
            case FORTH: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.forth_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.forth_room_selection_text, null));
                break;
            }

        }

        setCurrentRoom(roomNum);
        currentSelectedRoom = roomNum.get();
        mRoomCommentary.setText("Set commentary for room" + mRoom.getText().toString());
    }

    private void updateCalendar(int month) {
        ArrayList<ScheduleCell> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        calendar.set(Calendar.MONTH, month);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(new ScheduleCell(CELL_STATE.NONE, calendar.getTime(), ROOM_NUM.FIRST));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridAdapter.setCurrentMonth(month);
        gridAdapter.setDays(cells);

        calendar.set(Calendar.MONTH, month);
        txtDate.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    }


    public void addDuty(Date start, Date end, ROOM_NUM roomNum) {
        gridAdapter.setDateRangeDuty(start, end, roomNum);
    }

    @Override
    public void showDutyRange(ScheduleCell start, ScheduleCell end, ROOM_NUM roomNum) {
        addDuty(start.getDate(), end.getDate(), roomNum);
    }

    @Override
    public void setCurrentRoom(ROOM_NUM currentRoom) {
        this.currentRoom = currentRoom;
    }

    @Override
    public void onDateClicked(ScheduleCell date) {
        date.setRoomNum(currentRoom);
        if (rangeStartDate != null) {
            if (!rangeStartDate.equals(date)) {
                date.setState(CELL_STATE.END);
                addDuty(rangeStartDate.getDate(), date.getDate(), currentRoom);
            } else {
                date.setState(CELL_STATE.NONE);
                gridAdapter.updateDate(date, currentRoom);
            }
            rangeStartDate = null;
        } else {
            rangeStartDate = date;
            date.setState(CELL_STATE.START);
            gridAdapter.updateDate(date, currentRoom);
        }
    }
}
