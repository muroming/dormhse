package com.dorm.muro.dormitory.presentation.login;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.dorm.muro.dormitory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends MvpAppCompatActivity implements LoginView {

    @InjectPresenter
    LoginPresenter presenter;

    private enum FlipDir {NEXT, PREV}

    @BindView(R.id.et_login_mail)
    EditText mLoginEditText;
    @BindView(R.id.et_login_password)
    EditText mPasswordEditText;
    @BindView(R.id.vf_login_flipper)
    ViewFlipper mViewFlipper;
    @BindView(R.id.ll_login_header)
    LinearLayout mLoginHeader;
    @BindView(R.id.tv_login_register_stage1)
    ImageView mRegisterStage1;
    @BindView(R.id.tv_login_register_stage2)
    ImageView mRegisterStage2;
    @BindView(R.id.tv_register_already_have)
    TextView mAlreadyHaveAccount;
    @BindView(R.id.tv_forgot_check_email_callback)
    TextView mCheckEmailCallback;
    @BindView(R.id.et_forgot_mail)
    EditText mForgotMail;
    @BindView(R.id.btn_forgot_button)
    Button mForgotPasswordButton;
    @BindView(R.id.tv_forgot_email_display)
    TextView mEmailDisplay;
    @BindView(R.id.tv_forgot_title)
    TextView mForgotTitle;
    @BindView(R.id.tv_forgot_unsuccessful_back)
    TextView mForgotBack;
    @BindView(R.id.et_register_mail)
    EditText mRegisterMail;
    @BindView(R.id.et_register_password)
    EditText mRegisterPassword;
    @BindView(R.id.et_register_second_name)
    EditText mRegisterSecondName;
    @BindView(R.id.et_register_name)
    EditText mRegisterName;
    @BindView(R.id.et_register_contract_id)
    EditText mRegisterContractId;

    private ProgressDialog pd;
    private FlipDir flipDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        //Setting flipping animations
        mViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        flipDir = FlipDir.NEXT;


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
                presenter.goToMainScreen();
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
    }

    @OnClick(R.id.btn_login_login)
    public void onLoginClicked() {
        presenter.onSignInClicked(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    @OnClick(R.id.tv_login_create_account)
    public void onCreateAccountClicked() {
        presenter.showRegisterForm();
    }

    @OnClick(R.id.btn_register_next)
    public void onRegisterNextClicked() {
        presenter.registerNextScreen();
    }

    @OnClick(R.id.btn_register_finish)
    public void finishRegistration() {
        presenter.finishRegistration(mRegisterMail.getText().toString(), mRegisterPassword.getText().toString(),
                mRegisterName.getText().toString(), mRegisterSecondName.getText().toString(), mRegisterContractId.getText().toString());
    }

    //TODO: Make check email query
    public void forgotAction() {
        presenter.forgotPasswordAction(mForgotMail.getText().toString());
    }

    public void showForgotEmailCallback(boolean isSuccessful, String mail) {
        SpannableString ss;

        if (isSuccessful) {
            String text = getString(R.string.forgot_password_callback_success);
            ss = new SpannableString(text);
            ss.setSpan(new ForegroundColorSpan(getColor(R.color.successGreen)), 0, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            mForgotPasswordButton.setText(getString(R.string.forgot_password_back));
            mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.goToMainScreen();
                }
            });
        } else {
            String text = getString(R.string.forgot_password_callback_fail);
            ss = new SpannableString(text);
            ss.setSpan(new ForegroundColorSpan(getColor(R.color.failRed)), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            mForgotBack.setVisibility(View.VISIBLE);
            mForgotPasswordButton.setText(getString(R.string.forgot_password_try_again));
            mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.showForgotPasswordForm();
                }
            });
        }

        mCheckEmailCallback.setVisibility(View.VISIBLE);
        mEmailDisplay.setVisibility(View.VISIBLE);

        mForgotMail.setVisibility(View.INVISIBLE);
        mForgotTitle.setVisibility(View.INVISIBLE);

        mCheckEmailCallback.setText(ss);
        mEmailDisplay.setText(mail);
    }

    public void hideForgotEmailCallback() {
        mCheckEmailCallback.setVisibility(View.INVISIBLE);
        mEmailDisplay.setVisibility(View.INVISIBLE);
        mForgotBack.setVisibility(View.INVISIBLE);
        mForgotMail.setVisibility(View.VISIBLE);
        mForgotTitle.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_login_forgot_password)
    public void onForgotPasswordClicked() {
        presenter.showForgotPasswordForm();
    }

    @OnClick(R.id.tv_forgot_unsuccessful_back)
    public void goBackTextViewClicked() {
        presenter.goToMainScreen();
    }

    @Override
    public void onBackPressed() {
        int childId = mViewFlipper.getDisplayedChild();
        changeFlipDirection();
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
                presenter.showRegisterForm();
                break;
            }
            case 3: {
                if (mCheckEmailCallback.getVisibility() == View.VISIBLE) { //If callback is visible, return back to input eail
                    hideForgotEmailCallback();
                    presenter.showForgotPasswordForm();
                } else { //Else proceed to first page
                    presenter.goToMainScreen();
                }
                break;
            }
        }
        changeFlipDirection();
    }

    private void changeFlipDirection() {
        switch (flipDir) {
            case NEXT: {
                mViewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                flipDir = FlipDir.PREV;
                break;
            }
            case PREV: {
                mViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                flipDir = FlipDir.NEXT;
                break;
            }
        }
    }


    @Override
    public void showProgressDialog(String msg) {
        pd = new ProgressDialog(this);
        pd.setMessage(msg);
        pd.show();
    }

    @Override
    public void proceedToFirstPage() {
        mViewFlipper.setDisplayedChild(1);
        mLoginHeader.setVisibility(View.VISIBLE);
        mRegisterStage2.setImageDrawable(getDrawable(R.drawable.ic_gray_step));
        mRegisterStage1.setImageDrawable(getDrawable(R.drawable.ic_step_1));
    }

    @Override
    public void proceedToSecondPage() {
        mViewFlipper.setDisplayedChild(2);
        mRegisterStage2.setImageDrawable(getDrawable(R.drawable.ic_step_2));
        mRegisterStage1.setImageDrawable(getDrawable(R.drawable.ic_gray_step));
    }

    @Override
    public void showMainScreen() {
        changeFlipDirection();
        mLoginHeader.setVisibility(View.INVISIBLE);
        mViewFlipper.setDisplayedChild(0);
        changeFlipDirection();
    }

    @Override
    public void hideProgressDialog() {
        pd.dismiss();
    }

    @Override
    public void signIn() {
        MainActivity.start(this);
        finish();
    }

    @Override
    public void showForgotSceen() {
        mViewFlipper.setDisplayedChild(3);

        hideForgotEmailCallback();
        mForgotPasswordButton.setText(getString(R.string.forgot_password_button));

        mForgotPasswordButton.setOnClickListener(v -> forgotAction());
    }

    @Override
    public void showToast(int text) {
        Toast.makeText(this, getString(text), Toast.LENGTH_SHORT).show();
    }
}
