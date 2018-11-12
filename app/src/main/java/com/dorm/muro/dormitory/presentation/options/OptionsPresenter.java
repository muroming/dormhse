package com.dorm.muro.dormitory.presentation.options;

import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;

//import static com.dorm.muro.dormitory.presentation.login.LoginActivity.IS_LOGGED;

@InjectViewState
public class OptionsPresenter extends MvpPresenter<OptionsView> {
    private static final int PASSWORD = 0;
    private static final int EMAIL = 1;
    private static final int CONTRACT = 3;

    //TODO inject preferences
    private SharedPreferences preferences;
    //TODO remove this
    private boolean notificationsEnabled = true;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        //Todo remove this
        getViewState().setNotificationSwitch(notificationsEnabled);
    }

    void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    void onExitClicked() {
//        preferences.edit().remove(IS_LOGGED).apply();
        getViewState().proceedToLoginScreen();
    }

    void onSwitchNotifications(){
        notificationsEnabled = !notificationsEnabled;
//        preferences.edit().putBoolean(NOTIFICATIONS, notificationsEnabled).apply();
        getViewState().setNotificationSwitch(notificationsEnabled);
    }

    void onDialogCancel() {
        getViewState().closeDialog();
    }

    void onChangePasswordClicked() {
        getViewState().showChangeDialog(R.string.settings_new_password, R.string.settings_confirm_password,
                R.string.settings_password_title, PASSWORD);
    }

    void onChangePersonalDataClicked() {
        getViewState().showPersonalDialog(R.string.settings_cardholder_name, R.string.settings_card_date,
                R.string.settings_confirm_password, R.string.settings_change_personal_title);
    }

    void onPersonlChange(String name, String cardNum, String password) {
        //Todo implement logic
    }

    void onChangeMailClicked() {
        getViewState().showChangeDialog(R.string.settings_new_mail, R.string.settings_confirm_password,
                R.string.settings_mail_title, EMAIL);
    }

    void onChangeContractClicked() {
        getViewState().showChangeDialog(R.string.settings_new_contract, R.string.settings_confirm_password,
                R.string.settings_contract_title, CONTRACT);
    }

    public void onChangeInfo(String oldInfo, String newInfo, int code) {
        switch (code) {
            case PASSWORD: {

                break;
            }
            case EMAIL: {

                break;
            }
            case CONTRACT: {

                break;
            }
        }
    }
}
