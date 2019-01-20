package com.dorm.muro.dormitory.presentation.schedule;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.MvpView;

import java.util.Date;

interface ScheduleFragmentView extends MvpView {
    void showDutyRange(ScheduleCell start, ScheduleCell end, ROOM_NUM roomNum);

    void setRoomSelected(ROOM_NUM roomNum);

    void onDateClicked(ScheduleCell date, @Nullable ScheduleCell start, @Nullable ScheduleCell end);

    void updateDate(ScheduleCell cell, ROOM_NUM roomNum);

    void updateCalendar(int month);

    void updateStart(ScheduleCell newStart, ScheduleCell prevStart);

    void updateEnd(ScheduleCell newEnd, ScheduleCell prevEnd);

    void setOptions(@Nullable Integer upButton, int... menuId);

    void setTitle(int titleId);

    void deleteRange(ScheduleCell start, ScheduleCell end);

    void deleteRange(Date start, Date end);

    void showRangeDeleteSnackbar(ScheduleCell start, ScheduleCell end);

    void updateCalendar();

    void closeDialog();

    void showNoRoom();

    void showCalendar();

    void showToast(int text);
}
