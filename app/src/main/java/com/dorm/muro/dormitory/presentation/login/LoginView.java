package com.dorm.muro.dormitory.presentation.login;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface LoginView extends MvpView{
    void signIn();

    @StateStrategyType(SkipStrategy.class)
    void showWrongLoginPass();

    @StateStrategyType(SkipStrategy.class)
    void showWrongEmail();

    void registrationSuccess();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showMainScreen();

    void showForgotSceen();

    void proceedToFirstPage();

    void proceedToSecondPage();

    void showProgressDialog(String msg);

    void hideProgressDialog();

    void hideForgotEmailCallback();

    void showForgotEmailCallback(boolean isSuccessful, String mail);
}
