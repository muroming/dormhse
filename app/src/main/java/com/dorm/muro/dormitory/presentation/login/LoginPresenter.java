package com.dorm.muro.dormitory.presentation.login;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.network.authentication.UserSessionManager;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Random;
import java.util.regex.Pattern;

import io.reactivex.MaybeObserver;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.schedulers.Schedulers;

import static com.dorm.muro.dormitory.Constants.IS_LOGGED;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    //todo inject
    private SharedPreferences preferences;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    void onSignInClicked(String login, String password) {
                UserSessionManager.getInstance().authenticate(login, password)
                        .subscribeOn(Schedulers.io())
                        .map(authResult -> authResult.getUser() != null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MaybeObserver<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Boolean isLogged) {
                                if (isLogged) {
                                    getViewState().signIn();
                                } else {
                                    getViewState().showWrongLoginPass();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof FirebaseNetworkException)
                                    getViewState().showOnException();
                                if (e instanceof FirebaseAuthInvalidCredentialsException)
                                    getViewState().showWrongLoginPass();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
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
                () -> {
                    getViewState().hideProgressDialog();
                    getViewState().signIn();
                }, 2000
        );
    }

    void forgotPasswordAction(final String mail) {
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]+@[a-z0-9A-Z]+\\.[a-zA-Z]{2,6}", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(mail).find()) {  // Check if email matches pattern. If true, proceed, else show warning
            getViewState().showProgressDialog("Checking Email");
            new Handler().postDelayed(() -> {
                boolean test = new Random().nextBoolean();
                getViewState().hideProgressDialog();
                getViewState().showForgotEmailCallback(test, mail);
            }, 1000);
        } else {
            getViewState().showWrongEmail();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
