package com.dorm.muro.dormitory;

import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends AppCompatActivity implements DatePickerDialog.DialogResultListener {
    @BindView(R.id.et_settings_contract)
    EditText mContractInfo;

    @BindView(R.id.et_settings_fio)
    EditText mFIO;

    @BindView(R.id.et_settings_cost_per_month)
    EditText mCostPerMonth;

    @BindView(R.id.bn_settings_months_select)
    Button mMonthsToPay;

    private SharedPreferences preferences;
    private Toast savedToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

        mContractInfo.setText(preferences.getString(PaymentFragment.CONTRACT_ID, ""));
        mFIO.setText(preferences.getString(PaymentFragment.USER_FIO, ""));
        mCostPerMonth.setText(preferences.getString(PaymentFragment.MONTHLY_COST, ""));
        mMonthsToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerDialog();
                datePicker.show(getSupportFragmentManager(), "DatePickerDialog");
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
                .apply();
        savedToast.show();
    }

    @Override
    public void onDateSelected(DialogFragment dialog) {
        String res = ((DatePickerDialog) dialog).dateSelected;
        mMonthsToPay.setText(res);
    }
}
