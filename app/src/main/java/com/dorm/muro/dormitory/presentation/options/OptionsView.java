package com.dorm.muro.dormitory.presentation.options;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface OptionsView extends MvpView {
    void showChangeDialog(int title, int code, int[] hints, String[] values);
    void closeDialog();
    void showErrorToast(int stringRes);

    @StateStrategyType(SkipStrategy.class)
    void proceedToLoginScreen();

    @StateStrategyType(SkipStrategy.class)
    void setNotificationSwitch(boolean isEnabled);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setInfo(String mail, String contractId, String cardholderName, String userName, int cardNum, boolean notifications);  //todo add card payment system(visa, mc, ...) drawable

    void showRoomLeaveWarning();
}
