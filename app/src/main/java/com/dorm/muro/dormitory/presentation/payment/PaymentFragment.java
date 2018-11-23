package com.dorm.muro.dormitory.presentation.payment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
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
import com.dorm.muro.dormitory.R;

import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.Constants.*;


public class PaymentFragment extends MvpAppCompatFragment implements PaymentView {

    @BindView(R.id.wv_payment)
    WebView mPaymentWebView;

    @BindView(R.id.paymentGroup)
    Group mPaymentGroup;

    @BindView(R.id.progressGroup)
    Group mProgressGroup;

    @BindView(R.id.tv_payment_phrase)
    TextView progressPhrase;

    @BindView(R.id.tv_price)
    TextView mPrice;

    @BindView(R.id.btn_payment_range)
    Button mPaymentRange;

    @BindView(R.id.tv_payment_fio)
    TextView mPaymentFio;

    @BindView(R.id.tv_payment_contract)
    TextView mPaymentContract;


    @InjectPresenter
    PaymentPresenter presenter;

    private SharedPreferences preferences;
    private String[] progressTitles;


    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        presenter.setPreferences(preferences);
        progressTitles = getResources().getStringArray(R.array.payment_progress);

        //Setup user
        presenter.loadInfo();

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
                if (url.contains("checkout")) {  //Skip final page reviewing payment info
                    mPaymentWebView.evaluateJavascript(SKIP_CHECKOUT, null);
                } else {  //Input card info and confirm payment
                    presenter.loadCardInfo();
                }

            }
        });

        if (savedInstanceState != null) {
            mPaymentWebView.restoreState(savedInstanceState);
        }

        return view;
    }


    //fill all forms and proceed to confirm page
    @Override
    public void loadQuery(String query) {
        mPaymentWebView.evaluateJavascript(query, null);
    }

    @Override
    public void showCreditCardLayout() {

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
    public void showProgressGroup() {
        mProgressGroup.setVisibility(View.VISIBLE);

        progressPhrase.setText(progressTitles[new Random().nextInt(progressTitles.length)]);
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
    public void showErrorToast(int stringResource) {
        Toast.makeText(getContext(), getResources().getString(stringResource), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgressGroup() {
        mProgressGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePaymentGroup() {
        mPaymentGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showPaymentGroup() {
        mPaymentGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDatePickedDialog() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> presenter.onDateSelected(year, month),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    @Override
    public void showPrice(int priceRes, boolean isSet, int... nums) {
        if (isSet) {
            mPrice.setText(getString(priceRes, nums[0], nums[1]));
        } else {
            mPrice.setText(getString(priceRes));
        }
    }

    @Override
    public void showRange(int string, Object... params) {
        mPaymentRange.setText(getString(string, params));
    }

    @Override
    public void showFio(int stringRes, @Nullable String fio) {
        if (fio == null)
            mPaymentFio.setText(stringRes);
        else
            mPaymentFio.setText(fio);
    }

    @Override
    public void showContract(int stringRes, @Nullable String contract) {
        if (contract == null)
            mPaymentContract.setText(stringRes);
        else
            mPaymentContract.setText(contract);
    }

    @OnClick(R.id.btn_pay_button)
    public void onStartPay(View v) {
        presenter.onPayButtonClicked();
    }

    @OnClick(R.id.btn_payment_range)
    public void onSelectRange(View v) {
        presenter.onPaymentRangeClicked();
    }

    @JavascriptInterface
    public void handleErrorMsg() {
        presenter.onPayerWrongDataError();
    }

    @JavascriptInterface
    public void incrementStep() {

    }
}
