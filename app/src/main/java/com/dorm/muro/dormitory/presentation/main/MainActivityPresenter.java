package com.dorm.muro.dormitory.presentation.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {
    void showNearbyPlaces() {
        getViewState().showNearbyPlaces();
    }

    void showScheduleFragment() {
        getViewState().showScheduleFragment();
    }

    void showPaymentFragment() {
        getViewState().showPaymentFragment();
    }

    void showSettingsFragment() {
        getViewState().showSettingsFragment();
    }
}
