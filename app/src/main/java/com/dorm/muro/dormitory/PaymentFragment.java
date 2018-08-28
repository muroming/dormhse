package com.dorm.muro.dormitory;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PaymentFragment extends Fragment {

    @BindView(R.id.wv_payment_webview)
    WebView mPaymentWebView;

    @BindView(R.id.pb_payment_loading_progress)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.srl_payment_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_payment_progress)
    TextView mPaymentProgress;

    public static final String USER_FIO = "USER_FIO";
    public static final String CONTRACT_ID = "CONTRACT_ID";
    public static final String MONTHLY_COST = "MONTHLY_COST";
    public static final String MONTHS_FROM = "MONTHS_FROM";
    public static final String MONTHS_TO = "MONTHS_TO";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String CARDHOLDER_NAME = "CARDHOLDER_NAME";
    public static final String CARD_YEAR = "CARD_YEAR";
    public static final String CARD_MONTH = "CARD_MONTH";
    public static final String USER_NOT_FOUND_MESSAGE = "USER_NOT_FOUND";

    private final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    private final String FIRST_QUERY = "(function(){\n" +
            "var check = document.getElementById('amount');\n" +
            "var alertMsg = document.getElementsByClassName('error-message')[0];\n" +
            "document.getElementById('fio').value='%s';\n" +
            "document.getElementById('order').value='%s';\n" +
            "document.getElementsByClassName('pay_button')[0].click();\n" +
            "function checkFlag() {\n" +
            "if(check.offsetParent == null) {\n" +
            "if(alertMsg.innerHTLM != \"\"){\n" +
            "window.ErrorHandler.handleErrorMsg();\n" +
            "}else{ \n" +
            "window.setTimeout(checkFlag, 100);}\n" +
            "} else {\n" +
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

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        progressTitles = getResources().getStringArray(R.array.payment_progress);
        mPaymentProgress.setText(progressTitles[new Random().nextInt(progressTitles.length)]);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadingProgressBar.setVisibility(View.VISIBLE);
//                mPaymentWebView.setVisibility(View.INVISIBLE);
                mPaymentProgress.setVisibility(View.VISIBLE);
                mPaymentProgress.setText(progressTitles[new Random().nextInt(progressTitles.length)]);
                mPaymentWebView.reload();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.addJavascriptInterface(new JSInterface(getActivity()), "ErrorHandler");
        mPaymentWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equalsIgnoreCase(PAYMENT_URL)) {  //Still on the first page, input payment info
                    fillStartPageAndProceed(view);
                } else {
                    if (url.contains("checkout")) {  //Skip final page reviewing payment info
                        mPaymentWebView.setVisibility(View.VISIBLE);
                        mLoadingProgressBar.setVisibility(View.INVISIBLE);
                        mPaymentProgress.setVisibility(View.INVISIBLE);
                    } else {  //Input card info and confirm payment
                        if (preferences.contains(CARDHOLDER_NAME) && preferences.contains(CARD_NUMBER) && preferences.contains(CARD_YEAR) && preferences.contains(CARD_MONTH)) {
                            String cardholderName = preferences.getString(CARDHOLDER_NAME, "");
                            String cardNumber = preferences.getString(CARD_NUMBER, "");
                            String cardYear = preferences.getString(CARD_YEAR, "");
                            String cardMonth = preferences.getString(CARD_MONTH, "");
                            String query = String.format(ENTER_CARD_AND_CONFIRM_INFO, cardNumber, cardMonth, cardYear, cardholderName);
                            view.evaluateJavascript(query, null);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.no_payment_credits_set), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

        });

        mPaymentWebView.loadUrl(PAYMENT_URL);

        return view;
    }

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
            String query = String.format(FIRST_QUERY, fio, contractId, range, (int) price, price - ((int) price));
            view.evaluateJavascript(query, null);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_payment_credits_set), Toast.LENGTH_LONG).show();
        }
    }

    private String processContractId(String contractId) {
        String res = "";
        for (int i = 0; i < contractId.length(); i++) {
            if(contractId.charAt(i) == '\\'){
                res += "\\";
                res += "\\";
            }else{
                res += contractId.charAt(i);
            }
        }
        return res;
    }

    class JSInterface {
        Activity holderActivity;

        JSInterface(Activity holderActivity) {
            this.holderActivity = holderActivity;
        }

        @JavascriptInterface
        public void handleErrorMsg() {
            Toast.makeText(holderActivity, getString(R.string.payment_user_not_found), Toast.LENGTH_LONG).show();
        }
    }
}
