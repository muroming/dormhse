package com.dorm.muro.dormitory.presentation.main;

import com.arellomobile.mvp.MvpView;

public interface MainActivityView extends MvpView {
    void showNearbyPlaces();
    void showScheduleFragment();
    void showPaymentFragment();
}
