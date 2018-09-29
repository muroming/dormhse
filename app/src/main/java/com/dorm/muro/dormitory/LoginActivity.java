package com.dorm.muro.dormitory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.MainActivity.SHARED_PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    public static final String IS_LOGGED = "LOGIN_STATUS";

    @BindView(R.id.et_login_mail) EditText mLoginEditText;
    @BindView(R.id.et_login_password) EditText mPasswordEditText;
    @BindView(R.id.vf_login_flipper) ViewFlipper mViewFlipper;
    @BindView(R.id.ll_login_header) LinearLayout mLoginHeader;
    @BindView(R.id.tv_login_register_stage1) TextView mRegisterStage1;
    @BindView(R.id.tv_login_register_stage2) TextView mRegisterStage2;
    @BindView(R.id.tv_register_already_have) TextView mAlreadyHaveAccount;
    @BindView(R.id.tv_forgot_check_email_callback) TextView mCheckEmailCallback;
    @BindView(R.id.et_forgot_mail) EditText mForgotMail;
    @BindView(R.id.btn_forgot_button)  Button mForgotPasswordButton;
    @BindView(R.id.tv_forgot_email_display) TextView mEmailDisplay;
    @BindView(R.id.tv_forgot_title) TextView mForgotTitle;
    @BindView(R.id.tv_forgot_unsuccessful_back) TextView mForgotBack;

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
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mViewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mViewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);


        //Building alreadyHave TextView Spannable text
        String text = getString(R.string.register_already_have_account);
        String keyword = getString(R.string.register_already_have_keyword);
        SpannableString alreadyHave = new SpannableString(text);
        int startIndex = text.indexOf(keyword);
        int lastIndex = alreadyHave.length();

        //If already have an account then proceed to the main page of the form
        ClickableSpan keywordClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mViewFlipper.setDisplayedChild(0);
                mLoginHeader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        alreadyHave.setSpan(keywordClick, startIndex, lastIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        alreadyHave.setSpan(new ForegroundColorSpan(getColor(R.color.clickableText)), startIndex, lastIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        alreadyHave.setSpan(Typeface.BOLD, startIndex, lastIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mAlreadyHaveAccount.setText(alreadyHave);
        mAlreadyHaveAccount.setMovementMethod(LinkMovementMethod.getInstance());


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
        mRegisterStage1.setBackgroundColor(getResources().getColor(R.color.registerStage));
    }

    @OnClick(R.id.btn_register_next)
    public void proceedToRegisterSecondPage() {
        mViewFlipper.setDisplayedChild(2);
        mRegisterStage1.setBackgroundColor(0);
        mRegisterStage2.setBackgroundColor(getResources().getColor(R.color.registerStage));
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
                        signIn();
                    }
                }, 2000
        );
    }

    //TODO: Make check email query
    public void forgotAction() {
        registerProcessDialog = new ProgressDialog(this);
        registerProcessDialog.setMessage("Checking email");
        registerProcessDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                registerProcessDialog.dismiss();

                String mail = mForgotMail.getText().toString();

                boolean testBoolean = new Random().nextBoolean();
                if (testBoolean) {
                    String text = getString(R.string.forgot_password_callback_success);
                    SpannableString ss = new SpannableString(text);
                    ss.setSpan(new ForegroundColorSpan(getColor(R.color.successGreen)), 0, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    mCheckEmailCallback.setText(ss);
                } else {
                    String text = getString(R.string.forgot_password_callback_fail);
                    SpannableString ss = new SpannableString(text);
                    ss.setSpan(new ForegroundColorSpan(getColor(R.color.failRed)), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    mCheckEmailCallback.setText(ss);
                }

                mEmailDisplay.setText(mail);
                showForgotEmailCallback(testBoolean);
            }
        }, 2000);
    }

    private void showForgotEmailCallback(boolean isSuccessful) {
        mCheckEmailCallback.setVisibility(View.VISIBLE);
        mEmailDisplay.setVisibility(View.VISIBLE);

        mForgotMail.setVisibility(View.INVISIBLE);
        mForgotTitle.setVisibility(View.INVISIBLE);

        if (isSuccessful) {
            mForgotPasswordButton.setText(getString(R.string.forgot_password_back));
            mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewFlipper.setDisplayedChild(0);
                }
            });
        } else {
            mForgotBack.setVisibility(View.VISIBLE);
            mForgotPasswordButton.setText(getString(R.string.forgot_password_try_again));
            mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showForgotPasswordForm();
                }
            });
        }
    }

    private void hideForgotEmailCallback() {
        mCheckEmailCallback.setVisibility(View.INVISIBLE);
        mEmailDisplay.setVisibility(View.INVISIBLE);
        mForgotBack.setVisibility(View.INVISIBLE);
        mForgotMail.setVisibility(View.VISIBLE);
        mForgotTitle.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_login_forgot_password)
    public void showForgotPasswordForm() {
        mViewFlipper.setDisplayedChild(3);

        hideForgotEmailCallback();
        mForgotPasswordButton.setText(getString(R.string.forgot_password_button));

        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotAction();
            }
        });
    }

    @OnClick(R.id.tv_forgot_unsuccessful_back)
    public void goBackTextViewClick(){
        mViewFlipper.setDisplayedChild(0);
    }

    @Override
    public void onBackPressed() {
        int childId = mViewFlipper.getDisplayedChild();
        switch (childId) {
            case 0: {
                finish();
            }
            case 1: {
                mViewFlipper.showPrevious();
                mLoginHeader.setVisibility(View.INVISIBLE);
                break;
            }
            case 2: {
                proceedToRegisterFirstPage();
                break;
            }
            case 3: {
                if (mCheckEmailCallback.getVisibility() == View.VISIBLE) { //If callback is visible, return back to input eail
                    hideForgotEmailCallback();
                    showForgotPasswordForm();
                } else { //Else proceed to first page
                    mViewFlipper.setDisplayedChild(0);
                }
                break;
            }
        }
    }

    private boolean checkUser() {
        //TODO: create check user method
        Toast.makeText(getApplicationContext(), "create check user method", Toast.LENGTH_SHORT).show();
        return true;
    }
}
