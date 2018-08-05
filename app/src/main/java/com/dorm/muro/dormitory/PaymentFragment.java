package com.dorm.muro.dormitory;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PaymentFragment extends Fragment {

    @BindView(R.id.wv_payment_webview)
    WebView mPaymentWebView;

    @BindView(R.id.pb_payment_loading_progress)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.srl_payment_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String USER_FIO = "USER_FIO";
    public static final String CONTRACT_ID = "CONTRACT_ID";
    public static final String MONTHLY_COST = "MONTHLY_COST";
    public static final String SELCTED_MONTHS = "SELECTED_MONTHS";
    private final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    private final String FIRST_QUERY = "var a=document.getElementById('fio').value='%s';" +
            "var b=document.getElementById('order').value='%s';" +
            "var c=document.getElementsByClassName(\"pay_button\")[0].click();";

    private SharedPreferences preferences;

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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                mPaymentWebView.setVisibility(View.INVISIBLE);
                loadPage();
            }
        });

        loadPage();

        return view;
    }

    private void loadPage(){
        //TODO: redo fill form
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.loadUrl(PAYMENT_URL);
        mPaymentWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mPaymentWebView.setVisibility(View.VISIBLE);
                mLoadingProgressBar.setVisibility(View.GONE);
                fillStartPageAndProceed(view);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fillStartPageAndProceed(WebView view) {
        if (preferences.contains(USER_FIO) && preferences.contains(CONTRACT_ID)) {
            String fio = preferences.getString(USER_FIO, "");
            String cId = preferences.getString(CONTRACT_ID, "");
            String contractId = cId.split("\\\\")[0] + "\\\\" + cId.split("\\\\")[1];
            String query = String.format(FIRST_QUERY, fio, contractId);
            view.evaluateJavascript(query, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.no_payment_credits_set), Toast.LENGTH_LONG).show();
        }
    }
}
