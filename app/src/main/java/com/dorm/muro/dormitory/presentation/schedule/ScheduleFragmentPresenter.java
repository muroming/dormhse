package com.dorm.muro.dormitory.presentation.schedule;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

@InjectViewState
public class ScheduleFragmentPresenter extends MvpPresenter<ScheduleFragmentView> {
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void onRoomClicked(ROOM_NUM roomNum) {
        getViewState().setRoomSelected(roomNum);
    }
}
