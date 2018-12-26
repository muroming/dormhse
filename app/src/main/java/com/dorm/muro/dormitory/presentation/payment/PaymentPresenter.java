package com.dorm.muro.dormitory.presentation.payment;

import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;

import java.util.Calendar;

import static com.dorm.muro.dormitory.Constants.*;

@InjectViewState
public class PaymentPresenter extends MvpPresenter<PaymentView> {

    //todo inject
    private SharedPreferences preferences;

    void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    void onPayButtonClicked() {
        getViewState().hidePaymentGroup();
        getViewState().showProgressGroup();
        getViewState().startPayment();
    }

    void stopPayment() {
        getViewState().stopPayment();
        getViewState().hideProgressGroup();
        getViewState().showPaymentGroup();
    }

    void loadInfo() {
        String from = preferences.getString(MONTHS_FROM, null), to = preferences.getString(MONTHS_TO, null);
        float pricePerMonth = preferences.getFloat(MONTHLY_COST, -1);
        int totalMonths;
        if (from != null && to != null) {
            totalMonths = calcMonths(from, to);
            int rangeRes;
            switch (totalMonths % 10) {
                case 1: {
                    rangeRes = R.string.payment_range_one;
                    break;
                }
                case 3: {
                }
                case 2: {
                    rangeRes = R.string.payment_range_two_three;
                    break;
                }
                default: {
                    rangeRes = R.string.payment_range_other;
                }
            }
            getViewState().showRange(rangeRes, totalMonths, to.split(DATE_SPLITTER)[0].length() == 1 ? "0" + to : to);
            if (pricePerMonth != -1) {
                float price = totalMonths * pricePerMonth;
                int rub = (int) price;

                getViewState().showPrice(R.string.payment_price_tmp, true,
                        rub, (int) ((price - rub) * 100));
            } else {
                getViewState().showPrice(R.string.field_not_set, false);
            }
        } else {
            getViewState().showRange(R.string.field_not_set);
        }

        String fio = preferences.getString(USER_FIO, null), contract = preferences.getString(CONTRACT_ID, null);
        getViewState().showFio(R.string.field_not_set, fio);
        getViewState().showContract(R.string.field_not_set, contract);
    }

    void loadCardInfo() {
        if (preferences.contains(CARDHOLDER_NAME) && preferences.contains(CARD_NUMBER) && preferences.contains(CARD_YEAR) && preferences.contains(CARD_MONTH)) {
            String cardholderName = preferences.getString(CARDHOLDER_NAME, "");
            String cardNumber = preferences.getString(CARD_NUMBER, "");
            String cardYear = preferences.getString(CARD_YEAR, "");
            String cardMonth = preferences.getString(CARD_MONTH, "");
            String query = String.format(ENTER_CARD_AND_CONFIRM_INFO, cardNumber, cardMonth, cardYear, cardholderName);
            getViewState().loadQuery(query);
        } else {
            onPayerDataNotSetError();
        }
    }

    void onPayerWrongDataError() {
        getViewState().showErrorToast(R.string.payment_user_not_found);
        stopPayment();
    }

    void onPayerDataNotSetError() {
        getViewState().showErrorToast(R.string.no_payment_credits_set);
        stopPayment();
    }

    void onPaymentRangeClicked() {
        getViewState().showDatePickedDialog();
    }

    void onDateSelected(int year, int month) {
        Calendar c = Calendar.getInstance();
        String from = c.get(Calendar.MONTH) + 1 + DATE_SEPARATOR + c.get(Calendar.YEAR) % 100,
                to = month + 1 + DATE_SEPARATOR + year % 100;
        preferences.edit().putString(MONTHS_FROM, from).apply();
        preferences.edit().putString(MONTHS_TO, to).apply();
        loadInfo();
    }

    private int calcMonths(String from, String to) {
        int yearsTo = Integer.parseInt(to.split(DATE_SPLITTER)[1]), monthsTo = Integer.parseInt(to.split(DATE_SPLITTER)[0]);
        int yearsFrom = Integer.parseInt(from.split(DATE_SPLITTER)[1]), monthsFrom = Integer.parseInt(from.split(DATE_SPLITTER)[0]);

        return (yearsTo - yearsFrom) * 12 + monthsTo - monthsFrom;
    }

    void startLoadFirstStep() {
        if (preferences.contains(USER_FIO) && preferences.contains(CONTRACT_ID) && preferences.contains(MONTHLY_COST) && preferences.contains(MONTHS_FROM) && preferences.contains(MONTHS_TO)) {
            String fio = preferences.getString(USER_FIO, "");
            String contractId = preferences.getString(CONTRACT_ID, "");
            contractId = processContractId(contractId);
            String monthsFrom = preferences.getString(MONTHS_FROM, "");
            String monthsTo = preferences.getString(MONTHS_TO, "");
            String range = "с " + monthsFrom + " по " + monthsTo;
            String mFrom = monthsFrom.split(DATE_SEPARATOR)[0], yFrom = monthsFrom.split(DATE_SEPARATOR)[1], mTo = monthsTo.split(DATE_SEPARATOR)[0], yTo = monthsTo.split(DATE_SEPARATOR)[1];
            int totalMonths = (Integer.parseInt(yTo) - Integer.parseInt(yFrom)) * 12 + Integer.parseInt(mTo) - Integer.parseInt(mFrom);
            double price = totalMonths * preferences.getFloat(MONTHLY_COST, 0);
            String query = String.format(FIRST_QUERY, fio, contractId, range, (int) price, String.format("%.2f", price - ((int) price)));
            getViewState().loadQuery(query);
        } else {
            onPayerDataNotSetError();
        }
    }

    public void incrementStep() {
        getViewState().incrementStep();
    }

    //process contract ID if it has backslash
    private String processContractId(String contractId) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < contractId.length(); i++) {
            if (contractId.charAt(i) == '\\') {
                res.append("\\");
                res.append("\\");
            } else {
                res.append(contractId.charAt(i));
            }
        }
        return res.toString();
    }
}
