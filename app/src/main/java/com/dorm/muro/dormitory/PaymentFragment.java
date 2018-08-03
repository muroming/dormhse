package com.dorm.muro.dormitory;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import butterknife.OnClick;


public class PaymentFragment extends Fragment {

    @BindView(R.id.wv_payment_webview)
    WebView mPaymentWebView;

    @BindView(R.id.pb_payment_loading_progress)
    ProgressBar mLoadingProgressBar;

    public static final String USER_FIO = "USER_FIO";
    public static final String CONTRACT_ID = "CONTRACT_ID";
    private final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    private final String FIRST_QUERY = "var a=document.getElementById('fio').value='%s';" +
            "var b=document.getElementById('order').value='%s';";
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

        //TODO: redo fill form
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.loadUrl(PAYMENT_URL);
        mPaymentWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mPaymentWebView.setVisibility(View.VISIBLE);
                mLoadingProgressBar.setVisibility(View.GONE);
                fillData(view);
            }
        });


        return view;
    }

    private void fillData(WebView view) {
        if (preferences.contains(USER_FIO) && preferences.contains(CONTRACT_ID)) {
            String fio = preferences.getString(USER_FIO, "");
            String contractId = preferences.getString(CONTRACT_ID, "");
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


    void createPaymentNotification() {
        Intent actionIntent = new Intent(getContext(), MainActivity.class);
        //ToDo: add extras to intent to open this fragment
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, actionIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.settings_icon)
                .setContentTitle(getString(R.string.notification_payment_title))
                .setContentText(getString(R.string.notification_payment_context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat compat = NotificationManagerCompat.from(getContext());
        compat.notify(MainActivity.NOTIFICATION_ID, builder.build());
    }


}
