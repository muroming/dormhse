package com.dorm.muro.dormitory.presentation.payment;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface PaymentView extends MvpView {
    void showPaymentProgressDialog();
    void hidePaymentProgressDialog();
    void incrementPaymentStep();
    void stopPayment();

    @StateStrategyType(SkipStrategy.class)
    void startPayment();

    @StateStrategyType(SkipStrategy.class)
    void showErrorToast(int stringResource);
}
