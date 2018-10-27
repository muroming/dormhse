package com.dorm.muro.dormitory.presentation.options;

import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dorm.muro.dormitory.DatePickerFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends AppCompatActivity implements DatePickerFragment.DialogResultListener {
    @BindView(R.id.et_settings_contract)
    EditText mContractInfo;

    @BindView(R.id.et_settings_fio)
    EditText mFIO;

    @BindView(R.id.et_settings_cost_per_month)
    EditText mCostPerMonth;

    @BindView(R.id.bn_settings_select_date_from)
    Button mMonthsToPayFrom;

    @BindView(R.id.bn_settings_select_date_to)
    Button mMonthsToPayTo;

    @BindView(R.id.et_settings_card_number)
    EditText mCardNumber;

    @BindView(R.id.et_settings_cardholder_name)
    EditText mCardHolderName;

    @BindView(R.id.et_settings_card_date)
    EditText mCardDate;

    private SharedPreferences preferences;
    private Toast savedToast;
    public static final String FROM = "FROM";
    public static final String TO = "TO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

        mContractInfo.setText(preferences.getString(PaymentFragment.CONTRACT_ID, ""));
        mFIO.setText(preferences.getString(PaymentFragment.USER_FIO, ""));
        if (preferences.contains(PaymentFragment.MONTHLY_COST)) {
            mCostPerMonth.setText(String.valueOf(preferences.getFloat(PaymentFragment.MONTHLY_COST, 0)));
        }
        mCardNumber.setText(preferences.getString(PaymentFragment.CARD_NUMBER, ""));
        mCardHolderName.setText(preferences.getString(PaymentFragment.CARDHOLDER_NAME, ""));
        if (preferences.contains(PaymentFragment.CARD_MONTH) && preferences.contains(PaymentFragment.CARD_YEAR)) {
            mCardDate.setText(getString(R.string.settings_card_date_format, preferences.getString(PaymentFragment.CARD_MONTH, ""), preferences.getString(PaymentFragment.CARD_YEAR, "")));
        }
        mMonthsToPayTo.setText(preferences.getString(PaymentFragment.MONTHS_TO, getString(R.string.select)));
        mMonthsToPayFrom.setText(preferences.getString(PaymentFragment.MONTHS_FROM, getString(R.string.select)));
        mMonthsToPayFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle args = new Bundle(1);
                args.putString(MainActivity.DIALOG_TAG, FROM);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "DatePickerFragment");
            }
        });

        mMonthsToPayTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                Bundle args = new Bundle(1);
                args.putString(MainActivity.DIALOG_TAG, TO);
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "DatePickerFragment");
            }
        });

        savedToast = Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPause() {
        super.onPause();

        preferences.edit()
                .putString(PaymentFragment.CONTRACT_ID, mContractInfo.getText().toString())
                .putString(PaymentFragment.USER_FIO, mFIO.getText().toString())
                .putFloat(PaymentFragment.MONTHLY_COST, Float.parseFloat(mCostPerMonth.getText().toString()))
                .putString(PaymentFragment.MONTHS_TO, mMonthsToPayTo.getText().toString())
                .putString(PaymentFragment.MONTHS_FROM, mMonthsToPayFrom.getText().toString())
                .putString(PaymentFragment.CARD_NUMBER, mCardNumber.getText().toString())
                .putString(PaymentFragment.CARDHOLDER_NAME, mCardHolderName.getText().toString())
                .apply();
        if (mCardDate.getText().toString().contains("/")) {
            preferences.edit()
                    .putString(PaymentFragment.CARD_MONTH, mCardDate.getText().toString().split("/")[0])
                    .putString(PaymentFragment.CARD_YEAR, mCardDate.getText().toString().split("/")[1])
                    .apply();
        }
        savedToast.show();
    }

    @Override
    public void onDateSelected(int year, int month, String dialogTag) {
        switch (dialogTag) {
            case FROM: {
                mMonthsToPayFrom.setText(getString(R.string.settings_card_date_format, String.valueOf(month), String.valueOf(year)));
                break;
            }
            case TO: {
                mMonthsToPayTo.setText(getString(R.string.settings_card_date_format, String.valueOf(month), String.valueOf(year)));
                break;
            }
        }
    }
}
