package com.dorm.muro.dormitory.presentation.options;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface OptionsView extends MvpView {
    void showChangeDialog(int h1, int h2, int title, int type);
    void showBigChangeDialog(int h1, int h2, int h3, int title, int code);
    void closeDialog();
    void showErrorToast(int stringRes);

    @StateStrategyType(SkipStrategy.class)
    void proceedToLoginScreen();

    @StateStrategyType(SkipStrategy.class)
    void setNotificationSwitch(boolean isEnabled);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setInfo(String mail, String contractId, String fio, int cardNum);  //todo add card drawable

    void showRoomLeaveWarning();
}
