package com.dorm.muro.dormitory.presentation.schedule;

import com.arellomobile.mvp.MvpView;

interface ScheduleFragmentView extends MvpView {
    void showDutyRange(ScheduleCell start, ScheduleCell end, ROOM_NUM roomNum);

    void setRoomSelected(ROOM_NUM roomNum);

    void onDateClicked(ScheduleCell date);

    void updateDate(ScheduleCell cell, ROOM_NUM roomNum);

    void updateCalendar(int month);
}
