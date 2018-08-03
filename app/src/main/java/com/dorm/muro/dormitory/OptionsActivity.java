package com.dorm.muro.dormitory;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends AppCompatActivity {
    @BindView(R.id.et_settings_contract)
    EditText mContractInfo;

    @BindView(R.id.et_settings_fio)
    EditText mFIO;

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

        savedToast = Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT);
        mContractInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    preferences.edit().putString(PaymentFragment.CONTRACT_ID, ((EditText)v).getText().toString()).apply();
                    savedToast.show();
                }
            }
        });
        mFIO.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    preferences.edit().putString(PaymentFragment.USER_FIO, ((EditText) v).getText().toString()).apply();
                    savedToast.show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.edit()
                .putString(PaymentFragment.CONTRACT_ID, mContractInfo.getText().toString())
                .putString(PaymentFragment.USER_FIO, mFIO.getText().toString())
                .apply();
        savedToast.show();
    }
}
