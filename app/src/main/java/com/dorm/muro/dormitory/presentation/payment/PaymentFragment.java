package com.dorm.muro.dormitory.presentation.payment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.dorm.muro.dormitory.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PaymentFragment extends MvpAppCompatFragment implements PaymentView {

    @BindView(R.id.wv_payment)
    WebView mPaymentWebView;

    @InjectPresenter
    PaymentPresenter presenter;

    public static final String USER_FIO = "USER_FIO";
    public static final String CONTRACT_ID = "CONTRACT_ID";
    public static final String MONTHLY_COST = "MONTHLY_COST";
    public static final String MONTHS_FROM = "MONTHS_FROM";
    public static final String MONTHS_TO = "MONTHS_TO";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String CARDHOLDER_NAME = "CARDHOLDER_NAME";
    public static final String CARD_YEAR = "CARD_YEAR";
    public static final String CARD_MONTH = "CARD_MONTH";
    private final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    private final String FIRST_QUERY = "(function(){\n" +
            "var check = document.getElementById('amount');\n" +
            "var alertMsg = document.getElementsByClassName('error-message')[0];\n" +
            "document.getElementById('fio').value='%s';\n" +
            "document.getElementById('order').value='%s';\n" +
            "document.getElementsByClassName('pay_button')[0].click();\n" +
            "function checkFlag() {\n" +
            "if(check.offsetParent == null) {\n" +
            "if(alertMsg.innerText != \"\"){\n" +
            "window.CallBack.handleErrorMsg();\n" +
            "}else{ \n" +
            "window.setTimeout(checkFlag, 100);}\n" +
            "} else {\n" +
            "window.CallBack.incrementStep();\n" +
            "var a=document.getElementById('desination').value='%s';\n" +
            "document.getElementById('amount').value='%s';\n" +
            "var c=document.getElementById('kop').value='%s';\n" +
            "var d=document.getElementsByClassName('pay_button')[0].click();return 'ok';}}\n" +
            "checkFlag();\n" +
            "})();";
    private final String ENTER_CARD_AND_CONFIRM_INFO = "var a=document.getElementById(\"iPAN_sub\").value='%s';" +
            "var b=document.getElementById(\"input-month\").value=%s;" +
            "var c=document.getElementById(\"input-year\").value=%s;" +
            "var d=document.getElementById(\"iTEXT\").value='%s';";

    private SharedPreferences preferences;
    private String[] progressTitles;
    private AlertDialog paymentDialog;
    private ProgressBar progressBar;
    private TextView progressPhrase;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        progressTitles = getResources().getStringArray(R.array.payment_progress);

        //Setup dialog
        setupDialog(inflater);

        //Setup user
        setupUserInfo(view);

        // Set JS interaction
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.addJavascriptInterface(this, "CallBack");

        //Block image loading to load faster
        mPaymentWebView.getSettings().setBlockNetworkImage(true);

        //Set paying logic
        mPaymentWebView.setWebViewClient(new WebViewClient() {
            //Intercept .css files to load faster
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains(".css")) {
                    return new WebResourceResponse("text/css", "UTF-8", getActivity().getResources().openRawResource(R.raw.raw_css));
                } else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            public void onPageFinished(WebView view, String url) {
                if (url.equalsIgnoreCase(PAYMENT_URL)) {  //Still on the first page, input payment info
                    fillStartPageAndProceed(view);
                } else {
                    if (url.contains("checkout")) {  //Skip final page reviewing payment info
                        mPaymentWebView.setVisibility(View.VISIBLE);
                    } else {  //Input card info and confirm payment
                        if (preferences.contains(CARDHOLDER_NAME) && preferences.contains(CARD_NUMBER) && preferences.contains(CARD_YEAR) && preferences.contains(CARD_MONTH)) {
                            String cardholderName = preferences.getString(CARDHOLDER_NAME, "");
                            String cardNumber = preferences.getString(CARD_NUMBER, "");
                            String cardYear = preferences.getString(CARD_YEAR, "");
                            String cardMonth = preferences.getString(CARD_MONTH, "");
                            String query = String.format(ENTER_CARD_AND_CONFIRM_INFO, cardNumber, cardMonth, cardYear, cardholderName);
                            view.evaluateJavascript(query, null);
                        } else {
                            presenter.onPayerDataNotSetError();
                        }
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            mPaymentWebView.restoreState(savedInstanceState);
        }

        return view;
    }

    private void setupDialog(LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = inflater.inflate(R.layout.payment_progress_dialog, null);
        progressPhrase = v.findViewById(R.id.progress_phrase);
        progressBar = v.findViewById(R.id.progress);

        builder
                .setTitle(getResources().getString(R.string.payment_title))
                .setView(v)
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> presenter.onStopPayClicked());

        paymentDialog = builder.create();
    }

    private void setupUserInfo(View view) {
        String notSet = getResources().getString(R.string.payment_not_set);
        ((TextView) view.findViewById(R.id.tv_payment_fio)).setText(preferences.getString(USER_FIO, notSet));
        ((TextView) view.findViewById(R.id.tv_payment_contract)).setText(preferences.getString(CONTRACT_ID, notSet));

        String from = preferences.getString(MONTHS_FROM, null), to = preferences.getString(MONTHS_TO, null);
        if(from != null && to != null) {
            int yearsTo = Integer.parseInt(to.split("/")[0]), monthsTo = Integer.parseInt(to.split("/")[1]);
            int yearsFrom = Integer.parseInt(from.split("/")[0]), monthsFrom = Integer.parseInt(from.split("/")[1]);

            int totalMonths = (yearsTo - yearsFrom) * 12 + monthsTo - monthsFrom;
            ((Button) view.findViewById(R.id.btn_payment_range)).setText(getResources().getString(R.string.payment_range, totalMonths, to));
        } else {
            ((Button) view.findViewById(R.id.btn_payment_range)).setText(notSet);
        }
    }


    //fill all forms and proceed to confirm page
    private void fillStartPageAndProceed(WebView view) {
        if (preferences.contains(USER_FIO) && preferences.contains(CONTRACT_ID) && preferences.contains(MONTHLY_COST) && preferences.contains(MONTHS_FROM) && preferences.contains(MONTHS_TO)) {
            String fio = preferences.getString(USER_FIO, "");
            String contractId = preferences.getString(CONTRACT_ID, "");
            contractId = processContractId(contractId);
            String monthsFrom = preferences.getString(MONTHS_FROM, "");
            String monthsTo = preferences.getString(MONTHS_TO, "");
            String range = "с " + monthsFrom + " по " + monthsTo;
            String mFrom = monthsFrom.split("/")[0], yFrom = monthsFrom.split("/")[1], mTo = monthsTo.split("/")[0], yTo = monthsTo.split("/")[1];
            int totalMonths = (Integer.parseInt(yTo) - Integer.parseInt(yFrom)) * 12 + Integer.parseInt(mTo) - Integer.parseInt(mFrom);
            double price = totalMonths * preferences.getFloat(MONTHLY_COST, 0);
            String query = String.format(FIRST_QUERY, fio, contractId, range, (int) price, String.format("%.2f", price - ((int) price)));
            view.evaluateJavascript(query, null);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_payment_credits_set), Toast.LENGTH_LONG).show();
        }
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


    @OnClick(R.id.btn_pay_button)
    public void onPayButtonClicked() {
        presenter.onPayButtonClicked();
    }

    @Override
    public void startPayment() {
        mPaymentWebView.loadUrl(PAYMENT_URL);
    }

    @Override
    public void showPaymentProgressDialog() {
        progressPhrase.setText(progressTitles[new Random().nextInt(progressTitles.length)]);
        progressBar.setProgress(50, true);
    }

    @Override
    public void hidePaymentProgressDialog() {
        paymentDialog.dismiss();
    }

    @Override
    public void stopPayment() {
        mPaymentWebView.stopLoading();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mPaymentWebView.saveState(outState);
    }

    @Override
    public void incrementPaymentStep() {
        progressBar.setProgress(100, true);
    }

    @Override
    public void showErrorToast(int stringResource) {
        Toast.makeText(getContext(), getResources().getString(stringResource), Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void handleErrorMsg() {
        presenter.onPayerWrongDataError();
    }

    @JavascriptInterface
    public void incrementStep() {
        presenter.onIncrementPaymentStep();
    }
}
