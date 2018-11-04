package com.dorm.muro.dormitory.presentation.schedule;

import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;

import java.util.Date;

public class ScheduleCell {
    private ScheduleFragment.CELL_STATE state;
    private Date date;
    private ROOM_NUM roomNum;

    public ScheduleCell(ScheduleFragment.CELL_STATE state, Date date, ROOM_NUM roomNum) {
        this.state = state;
        this.date = date;
        this.roomNum = roomNum;
    }

    public ScheduleFragment.CELL_STATE getState() {
        return state;
    }

    public void setState(ScheduleFragment.CELL_STATE state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ROOM_NUM getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(ROOM_NUM roomNum) {
        this.roomNum = roomNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScheduleFragment)) {
            return false;
        }
        try {
            return date.getTime() == ((ScheduleCell) obj).date.getTime();
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }
}
