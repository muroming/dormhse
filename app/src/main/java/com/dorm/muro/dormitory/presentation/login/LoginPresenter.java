package com.dorm.muro.dormitory.presentation.login;

import android.content.SharedPreferences;
import android.os.Handler;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Random;
import java.util.regex.Pattern;

import static com.dorm.muro.dormitory.Constants.IS_LOGGED;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    //todo inject
    private SharedPreferences preferences;

    void onSignInClicked(String login, String password) {
        if (checkUser(login, password)) {
            preferences.edit().putBoolean(IS_LOGGED, true).apply();
            getViewState().signIn();
        } else {
            getViewState().showWrongLoginPass();
        }
    }

    private boolean checkUser(String login, String password) {
        //TODO: create check user method
        return login.equals("l") && password.equals("p");
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    void registerNextScreen() {
        getViewState().proceedToSecondPage();
    }

    void showRegisterForm() {
        getViewState().proceedToFirstPage();
    }

    void goToMainScreen() {
        getViewState().showMainScreen();
    }

    void showForgotPasswordForm() {
        getViewState().showForgotSceen();
    }


    void finishRegistration() {
        getViewState().showProgressDialog("Confirming Registration");
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        getViewState().hideProgressDialog();
                        getViewState().registrationSuccess();
                    }
                }, 2000
        );
    }

    void forgotPasswordAction(final String mail) {
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]+@[a-z0-9A-Z]+\\.[a-zA-Z]{2,6}", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(mail).find()) {  // Check if email matches pattern. If true, proceed, else show warning
            getViewState().showProgressDialog("Checking Email");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean test = new Random().nextBoolean();
                    getViewState().hideProgressDialog();
                    getViewState().showForgotEmailCallback(test, mail);
                }
            }, 1000);
        } else {
            getViewState().showWrongEmail();
        }
    }
}
