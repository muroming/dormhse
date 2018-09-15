package com.dorm.muro.dormitory.presentation.schedule;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.ScheduleCalendar.ScheduleCalendarView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    @BindView(R.id.schedule_calendar)
    ScheduleCalendarView scheduleCalendarView;
    @BindViews({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    List<TextView> mRooms;
    @BindView(R.id.tv_schedule_commentary)
    TextView mRoomCommentary;

    private int currentSelectedRoom;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);

        currentSelectedRoom = 0;
        return view;
    }

    @OnClick({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    public void selectRoom(TextView mRoom) {
        mRooms.get(currentSelectedRoom).setBackgroundResource(0);
        mRooms.get(currentSelectedRoom).setTextColor(Color.BLACK);
        mRoom.setBackgroundResource(R.drawable.selected_room);
        mRoom.setTextColor(getResources().getColor(R.color.room_text));
        currentSelectedRoom = Integer.parseInt(mRoom.getText().toString()) - 1;
        mRoomCommentary.setText("Set commentary for room" + mRoom.getText().toString());
    }
}
