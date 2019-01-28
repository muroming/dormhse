package com.dorm.muro.dormitory.presentation.main;

import android.support.v4.app.Fragment;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface MainActivityView extends MvpView {
    void showPage(int pageNum, int titleRes);
    @StateStrategyType(SkipStrategy.class)
    void showSplash();
    @StateStrategyType(SkipStrategy.class)
    void hideSplash();
}
