package com.dorm.muro.dormitory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.MainActivity.SHARED_PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    public static final String IS_LOGGED = "LOGIN_STATUS";

    @BindView(R.id.et_login_mail)
    public EditText mLoginEditText;

    @BindView(R.id.et_login_password)
    public EditText mPasswordEditText;

    @BindView(R.id.vf_login_flipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.ll_login_header)
    LinearLayout mLoginHeader;

    @BindView(R.id.tv_login_register_stage1)
    TextView mRegisterStage1;

    @BindView(R.id.tv_login_register_stage2)
    TextView mRegisterStage2;

    private SharedPreferences preferences;
    private Intent mainActivityIntent;
    private ProgressDialog registerProcessDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mViewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mViewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        if (preferences.getBoolean(IS_LOGGED, false)) {
            startActivity(mainActivityIntent);
        }
    }

    @OnClick(R.id.btn_login_login)
    public void signIn() {
        if (checkUser()) {
            preferences.edit()
                    .putBoolean(IS_LOGGED, true)
                    .apply();
            startActivity(mainActivityIntent);
        }
    }

    @OnClick(R.id.tv_login_create_account)
    public void proceedToRegisterFirstPage() {
        mViewFlipper.setDisplayedChild(1);
        mLoginHeader.setVisibility(View.VISIBLE);
        mRegisterStage2.setBackgroundColor(0);
        mRegisterStage1.setBackgroundColor(getResources().getColor(R.color.register_stage));
    }

    @OnClick(R.id.btn_register_next)
    public void proceedToRegisterSecondPage() {
        mViewFlipper.setDisplayedChild(2);
        mRegisterStage1.setBackgroundColor(0);
        mRegisterStage2.setBackgroundColor(getResources().getColor(R.color.register_stage));
    }

    @OnClick(R.id.btn_register_finish)
    public void finishRegistration() {
        registerProcessDialog = new ProgressDialog(this);
        registerProcessDialog.setMessage("Confirming registration");
        registerProcessDialog.show();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        registerProcessDialog.dismiss();
                    }
                }, 2000
        );
    }

    @Override
    public void onBackPressed() {
        int childId = mViewFlipper.getDisplayedChild();
        switch (childId){
            case 0:{
                finish();
            }
            case 1:{
                mViewFlipper.showPrevious();
                mLoginHeader.setVisibility(View.INVISIBLE);
                break;
            }
            case 2:{
                proceedToRegisterFirstPage();
                break;
            }
        }
    }

    private boolean checkUser() {
        //TODO: create check user method
        Toast.makeText(getApplicationContext(), "create check user method", Toast.LENGTH_SHORT).show();
        return mLoginEditText.getText().toString().equals("login") && mPasswordEditText.getText().toString().equals("pass");
    }
}
