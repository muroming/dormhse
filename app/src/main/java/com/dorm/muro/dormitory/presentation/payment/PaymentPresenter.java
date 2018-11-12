package com.dorm.muro.dormitory.presentation.payment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;

@InjectViewState
public class PaymentPresenter extends MvpPresenter<PaymentView> {

    public void onPayButtonClicked() {
        getViewState().showPaymentProgressDialog();
    }

    public void onStopPayClicked() {
        getViewState().stopPayment();
        getViewState().hidePaymentProgressDialog();
    }

    public void onIncrementPaymentStep() {
        getViewState().incrementPaymentStep();
    }

    public void onPayerWrongDataError() {
        getViewState().showErrorToast(R.string.payment_user_not_found);
        getViewState().hidePaymentProgressDialog();
        getViewState().stopPayment();
    }

    public void onPayerDataNotSetError() {
        getViewState().showErrorToast(R.string.no_payment_credits_set);
    }
}
