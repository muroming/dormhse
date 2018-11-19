package com.dorm.muro.dormitory.presentation.schedule;


import com.dorm.muro.dormitory.R;

import java.util.Date;

public class ScheduleCell {
    private Date date;
    private ROOM_NUM roomNum;
    private ScheduleFragment.CELL_STATE state;

    ScheduleCell(Date date) {
        this.date = date;
        state = ScheduleFragment.CELL_STATE.NONE;
        roomNum = ROOM_NUM.FIRST;
    }

    public int getColor() {
        switch (roomNum) {
            case FIRST: {
                return R.color.first_room_duty;
            }
            case SECOND: {
                return R.color.second_room_duty;
            }
            case THIRD: {
                return R.color.third_room_duty;
            }
            case FORTH: {
                return R.color.forth_room_duty;
            }
        }
        return 0;
    }

    public int getDrawable() {
        switch (state) {
            case NONE: {
                return 0;
            }
            case START: {
                return R.drawable.duty_range_start;
            }
            case MEDIUM: {
                return R.drawable.duty_range_middle;
            }
            case END: {
                return R.drawable.duty_range_end;
            }
        }
        return 0;
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

    public ScheduleFragment.CELL_STATE getState() {
        return state;
    }

    public void setState(ScheduleFragment.CELL_STATE state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScheduleCell)) {
            return false;
        }
        try {
            return date.getTime() == ((ScheduleCell) obj).date.getTime();
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }
}