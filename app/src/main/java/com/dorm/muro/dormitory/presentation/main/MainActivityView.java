package com.dorm.muro.dormitory.presentation.main;

import android.support.v4.app.Fragment;

import com.arellomobile.mvp.MvpView;

public interface MainActivityView extends MvpView {
    void showPage(int pageNum, int titleRes);
}
