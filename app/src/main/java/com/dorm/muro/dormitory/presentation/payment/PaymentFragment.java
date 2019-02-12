package com.dorm.muro.dormitory.presentation.payment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.dagger.Injector;

import java.util.Calendar;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Provider;

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

    @Inject
    @InjectPresenter
    PaymentPresenter presenter;

    @ProvidePresenter
    PaymentPresenter providePaymentPresenter() {
        return presenter;
    }

    private String[] progressTitles;


    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Injector.getInstance().getPresenterComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        presenter.loadInfo();
        progressTitles = getResources().getStringArray(R.array.payment_progress);

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
                if (url.equals(PAYMENT_URL)) {
                    presenter.startLoadFirstStep();
                } else {
                    if (url.contains("checkout")) {  //Skip final page reviewing payment info
                        mPaymentWebView.evaluateJavascript(SKIP_CHECKOUT, null);
                    } else {  //Input card info and confirm card
                        if (url.contains("securepayments.sberbank.ru")) {
                            presenter.loadCardInfo();
                        } else {
                            if (url.contains("pay.hse.ru") && url.contains("complete")) {
                                presenter.onPaymentFinishedSuccessfully();
                            }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && presenter != null) {
            //Setup user
            presenter.loadInfo();
        }
    }

    //fill all forms and proceed to confirm page
    @Override
    public void loadQuery(String query) {
        mPaymentWebView.evaluateJavascript(query, null);
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
        mProgressGroup.setVisibility(View.GONE);
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
        mProgressGroup.setVisibility(View.GONE);
        mProgressGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePaymentGroup() {
        mPaymentGroup.setVisibility(View.GONE);
        mPaymentGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showPaymentGroup() {
        mPaymentGroup.setVisibility(View.GONE);
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

    @Override
    public void showCVVInputDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        EditText cvv = new EditText(getContext());
        layout.setPadding(getResources().getDimensionPixelSize(R.dimen.room_dialog_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.room_dialog_vertical_margin),
                getResources().getDimensionPixelSize(R.dimen.room_dialog_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.room_dialog_vertical_margin));

        cvv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cvv.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        cvv.setMaxLines(1);
        cvv.setHint(R.string.payment_cvv_hint);
        layout.addView(cvv);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(layout)
                .setPositiveButton(R.string.proceed, (dialog, which) -> {
                    presenter.onCVVEntered(cvv.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (d, w) -> {
                    d.dismiss();
                    presenter.stopPayment();
                });

        builder.create().show();
    }

    @Override
    public void showSMSCodeInputDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        EditText sms = new EditText(getContext());
        layout.setPadding(getResources().getDimensionPixelSize(R.dimen.room_dialog_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.room_dialog_vertical_margin),
                getResources().getDimensionPixelSize(R.dimen.room_dialog_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.room_dialog_vertical_margin));

        sms.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sms.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        sms.setMaxLines(1);
        sms.setHint(R.string.payment_sms_hint);
        layout.addView(sms);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(layout)
                .setPositiveButton(R.string.proceed, (dialog, which) -> {
                    presenter.onSMSEntered(sms.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (d, w) -> {
                    d.dismiss();
                    presenter.stopPayment();
                });

        builder.create().show();
    }

    @Override
    public void showCompleteDialog() {

    }

    @OnClick(R.id.btn_payment_cancel)
    public void onStopClicked(View v) {
        presenter.stopPayment();
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

    @Override
    public void incrementStep() {
        progressPhrase.setText(progressTitles[new Random().nextInt(progressTitles.length)]);
    }

    @JavascriptInterface
    public void nextStep() {
        presenter.incrementStep();
    }

    @JavascriptInterface
    public void inputCVV() {
        presenter.inputCVV();
    }

    @JavascriptInterface
    public void inputSMSCode() {
        presenter.inputSMS();
    }

    @JavascriptInterface
    public void onPaymentComplete() {
        presenter.paymentComplete();
    }
}
