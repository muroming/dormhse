package com.dorm.muro.dormitory.presentation.schedule;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Calendar;
import java.util.TimeZone;

@InjectViewState
public class ScheduleFragmentPresenter extends MvpPresenter<ScheduleFragmentView> {

    // Calendar State
    private ScheduleCell rangeStartDate;
    private ROOM_NUM currentRoom;


    // current displayed month
    private Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    public void onRoomClicked(ROOM_NUM roomNum) {
        getViewState().setRoomSelected(roomNum);
        currentRoom = roomNum;
    }

    public void onShowNextMonth(){
        currentDate.add(Calendar.MONTH, 1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    public void onShowPrevMonth(){
        currentDate.add(Calendar.MONTH, -1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    public void onDateClicked(ScheduleCell date){
        date.setRoomNum(currentRoom);
        if (rangeStartDate != null) {  // If first date is selected
            if (!rangeStartDate.equals(date)) { // If we selected different date then create duty
                if(rangeStartDate.getDate().getTime() < date.getDate().getTime()) { // Manage date order
                    date.setState(ScheduleFragment.CELL_STATE.END);
                    getViewState().showDutyRange(rangeStartDate, date, currentRoom);
                } else {
                    date.setState(ScheduleFragment.CELL_STATE.START);
                    rangeStartDate.setState(ScheduleFragment.CELL_STATE.END);
                    getViewState().showDutyRange(date, rangeStartDate, currentRoom);
                }
            } else { // Else unselect first date
                date.setState(ScheduleFragment.CELL_STATE.NONE);
                getViewState().updateDate(date, currentRoom);
            }
            rangeStartDate = null;
        } else { // Else set date as first
            rangeStartDate = date;
            date.setState(ScheduleFragment.CELL_STATE.START);
            getViewState().updateDate(date, currentRoom);
        }
    }
}
