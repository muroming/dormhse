package com.dorm.muro.dormitory.presentation.options;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.dorm.muro.dormitory.DatePickerFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsActivity extends MvpAppCompatFragment {
    private SharedPreferences preferences;
    public static final String FROM = "FROM";
    public static final String TO = "TO";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
