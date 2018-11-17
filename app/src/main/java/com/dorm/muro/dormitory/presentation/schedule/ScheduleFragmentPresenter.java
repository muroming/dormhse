package com.dorm.muro.dormitory.presentation.schedule;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;

import java.util.Calendar;
import java.util.TimeZone;

@InjectViewState
public class ScheduleFragmentPresenter extends MvpPresenter<ScheduleFragmentView> {

    // Calendar State
    private ScheduleCell rangeStartDate;
    private ScheduleCell rangeEndDate;
    private ROOM_NUM currentRoom;


    // current displayed month
    private Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private boolean isEditing = false;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
        getViewState().setOptions(null, R.id.menu_add);
    }

    public void onRoomClicked(ROOM_NUM roomNum) {
        if (!isEditing) {
            getViewState().setRoomSelected(roomNum);
            currentRoom = roomNum;
        }
    }

    public void onShowNextMonth() {
        currentDate.add(Calendar.MONTH, 1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    public void onShowPrevMonth() {
        currentDate.add(Calendar.MONTH, -1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    public void onDateClicked(ScheduleCell date) {
        if (isEditing) { // If clicked while adding a duty
            date.setRoomNum(currentRoom);
            if (rangeStartDate != null) {  // If first date is selected
                if (!rangeStartDate.equals(date)) { // If we selected different from first date
                    if (rangeEndDate != null) { // If end date is selected
                        if (!rangeEndDate.equals(date)) { // And different from end date update range bounds
                            showSelectedRange(date);
                        } else {  // Else unselect end date
                            unselectDate(date);
                            rangeEndDate = null;
                        }
                    } else {  // Else set date as end
                        showSelectedRange(date);
                    }
                } else { // Else unselect first date
                    unselectDate(date);
                    rangeStartDate = null;
                }
            } else { // Else set date as first
                rangeStartDate = date;
                date.setState(ScheduleFragment.CELL_STATE.START);
                getViewState().updateDate(date, currentRoom);
            }
        }
    }

    private void showSelectedRange(@Nullable ScheduleCell date) {
        if (rangeEndDate == null) {  // Create range
            rangeEndDate = date;
            if (rangeStartDate.getDate().getTime() < rangeEndDate.getDate().getTime()) { // Manage date order
                rangeEndDate.setState(ScheduleFragment.CELL_STATE.END);
                getViewState().showDutyRange(rangeStartDate, rangeEndDate, currentRoom);
            } else {
                rangeEndDate.setState(ScheduleFragment.CELL_STATE.START);
                rangeStartDate.setState(ScheduleFragment.CELL_STATE.END);
                getViewState().showDutyRange(rangeEndDate, rangeStartDate, currentRoom);
            }
        } else {  // Update range
            long startDelta = Math.abs(date.getDate().getTime() - rangeStartDate.getDate().getTime()),
                    endDelta = Math.abs(date.getDate().getTime() - rangeEndDate.getDate().getTime());

            if (endDelta < startDelta) { // Update end date
                date.setState(ScheduleFragment.CELL_STATE.END);
                getViewState().updateEnd(date, rangeEndDate);
                rangeEndDate = date;
            } else {  //Update start date
                date.setState(ScheduleFragment.CELL_STATE.START);
                getViewState().updateStart(date, rangeStartDate);
                rangeStartDate = date;
            }
        }
    }

    private void unselectDate(ScheduleCell date) {
        date.setState(ScheduleFragment.CELL_STATE.NONE);
        getViewState().updateDate(date, currentRoom);
    }

    public void applyRange() {
        isEditing = false;
        rangeEndDate = null;
        rangeStartDate = null;
        getViewState().setOptions(null, R.id.menu_add);
        getViewState().setTitle(R.string.fragment_schedule_title);
    }

    public void startAddingDuty() {
        isEditing = true;
        getViewState().setOptions(R.drawable.ic_close, R.id.menu_apply);
        getViewState().setTitle(R.string.schedule_add_duty);
    }

    public void onUpButtonPressed() {
        if (isEditing) {
            isEditing = false;
            getViewState().setOptions(null, R.id.menu_add);
            getViewState().setTitle(R.string.fragment_schedule_title);
        }
    }
}
