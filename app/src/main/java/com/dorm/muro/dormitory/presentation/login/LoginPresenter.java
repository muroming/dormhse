package com.dorm.muro.dormitory.presentation.login;


import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;

import static com.dorm.muro.dormitory.Constants.*;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SharedPreferences preferences;

    void onSignInClicked(String login, String password) {
        UserSessionManager.getInstance().authenticate(login, password)
                .addOnCompleteListener(authentication -> {
                    if (authentication.isSuccessful()) {
                        getViewState().signIn();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(USER_PASSWORD, password);
                        editor.putString(USER_EMAIL, login);
                        editor.apply();
                    } else {
                        if (authentication.getException() instanceof FirebaseNetworkException) {
                            getViewState().showToast(R.string.network_exception);
                        } else {
                            getViewState().showToast(R.string.wrong_login_password);
                        }
                    }
                });
    }

    void setPreferences(SharedPreferences preferences) {
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


    void finishRegistration(String email, String password, String name, String surname, String patronymic, String contractId) {
        getViewState().showProgressDialog(R.string.confirming_registration_progress_title);
        UserSessionManager.getInstance().registerNewUser(email, password).addOnCompleteListener(registration -> {
            if (registration.isSuccessful()) {
                Map<String, Object> initialValues = new HashMap<>(3);
                initialValues.put(USER_NAME_FIELD, name);
                initialValues.put(USER_SURNAME_FIELD, surname);
                initialValues.put(USER_PATRONYMIC_FIELD, patronymic);
                initialValues.put(USER_CONTRACT_ID_FIELD, contractId);

                UserSessionManager.getInstance().updateUserField(initialValues).addOnCompleteListener(
                        initialization -> {
                            if (initialization.isSuccessful()) {
                                getViewState().hideProgressDialog();
                                getViewState().signIn();
                            } else {
                                Exception exception = initialization.getException();
                                getViewState().hideProgressDialog();

                                if (exception instanceof FirebaseNetworkException)
                                    getViewState().showToast(R.string.network_exception);
                            }
                        }
                );
            } else {
                Exception exception = registration.getException();
                getViewState().hideProgressDialog();

                if (exception instanceof FirebaseNetworkException)
                    getViewState().showToast(R.string.network_exception);

                if (exception instanceof FirebaseAuthWeakPasswordException)
                    getViewState().showToast(R.string.registration_weak_password);

                if (exception instanceof FirebaseAuthUserCollisionException)
                    getViewState().showToast(R.string.email_already_in_use);

                if (exception instanceof FirebaseAuthInvalidCredentialsException)
                    getViewState().showToast(R.string.wrong_email);
            }
        });
    }

    void forgotPasswordAction(String mail) {
        Pattern pattern = Pattern.compile("[a-z0-9A-Z]+@[a-z0-9A-Z]+\\.[a-zA-Z]{2,6}", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(mail).find()) {  // Check if email matches pattern. If true, proceed, else show warning
            getViewState().showProgressDialog(R.string.sending_restore_email_progress_title);
            UserSessionManager.getInstance().sendRestoreEmail(mail).addOnCompleteListener(task -> {
                getViewState().hideProgressDialog();
                getViewState().showForgotEmailCallback(task.isSuccessful(), mail);
            });
        } else {
            getViewState().showToast(R.string.wrong_email);
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
