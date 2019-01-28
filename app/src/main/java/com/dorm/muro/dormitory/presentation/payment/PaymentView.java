package com.dorm.muro.dormitory.presentation.payment;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;


public interface PaymentView extends MvpView {
    void showProgressGroup();
    void hideProgressGroup();
    void hidePaymentGroup();
    void showPaymentGroup();
    void showDatePickedDialog();
    void stopPayment();

    void showPrice(int priceTitle, boolean isSet, int... nums);
    void showRange(int string, Object... params);
    void showFio(int stringRes, @Nullable String fio);
    void showContract(int stringRes, @Nullable String contract);

    @StateStrategyType(SkipStrategy.class)
    void startPayment();

    @StateStrategyType(SkipStrategy.class)
    void showErrorToast(int stringResource);

    @StateStrategyType(SkipStrategy.class)
    void loadQuery(String query);

    void incrementStep();

    @StateStrategyType(SkipStrategy.class)
    void showCVVInputDialog();

    @StateStrategyType(SkipStrategy.class)
    void showSMSCodeInputDialog();

    @StateStrategyType(SkipStrategy.class)
    void showCompleteDialog();
}
