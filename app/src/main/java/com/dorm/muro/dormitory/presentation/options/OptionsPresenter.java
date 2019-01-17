package com.dorm.muro.dormitory.presentation.options;

import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManagement;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;

import static com.dorm.muro.dormitory.Constants.*;


@InjectViewState
public class OptionsPresenter extends MvpPresenter<OptionsView> {
    private static final int PASSWORD = 0;
    private static final int EMAIL = 1;
    private static final int CONTRACT = 3;
    private static final int PERSONAL = 4;

    //TODO inject preferences
    private SharedPreferences preferences;
    private String mail, fio, contract;
    private int cardNum;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setNotificationSwitch(preferences.getBoolean(NOTIFICATIONS, true));
    }

    void setPreferences(SharedPreferences preferences) {
        //todo remove this like wtf????
        mail = preferences.getString(USER_EMAIL, "");
        fio = preferences.getString(USER_FIO, "");
        contract = preferences.getString(CONTRACT_ID, "");

        String s = preferences.getString(CARD_NUMBER, "");
        if(s.isEmpty()){
            cardNum = -1;
        } else {
            cardNum = Integer.parseInt(s.split(" ")[0]);
        }
        getViewState().setInfo(mail, contract, fio, cardNum);

        this.preferences = preferences;
    }

    void onExitClicked() {
        preferences.edit().remove(IS_LOGGED).apply();
        getViewState().proceedToLoginScreen();
    }

    void onSwitchNotifications(){
        boolean notificationsEnabled = preferences.getBoolean(NOTIFICATIONS, true);
        notificationsEnabled = !notificationsEnabled;
        preferences.edit().putBoolean(NOTIFICATIONS, notificationsEnabled).apply();
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
        getViewState().showBigChangeDialog(R.string.settings_cardholder_name, R.string.settings_card_number,
                R.string.settings_confirm_password, R.string.settings_change_personal_title, PERSONAL);
    }

    void onChangeMailClicked() {
        getViewState().showChangeDialog(R.string.settings_new_mail, R.string.settings_confirm_password,
                R.string.settings_mail_title, EMAIL);
    }

    void onChangeContractClicked() {
        getViewState().showBigChangeDialog(R.string.settings_new_contract, R.string.settings_change_cost, R.string.settings_confirm_password,
                R.string.settings_contract_title, CONTRACT);
    }

    void onChangeInfo(int code, String pass, String... newInfo) {
        if (pass.equals(preferences.getString(USER_PASSWORD, null))) {
            switch (code) {
                case PASSWORD: {
                    preferences.edit().putString(USER_PASSWORD, newInfo[0]).apply();
                    break;
                }
                case EMAIL: {
                    preferences.edit().putString(USER_EMAIL, newInfo[0]).apply();
                    break;
                }
                case CONTRACT: {
                    preferences.edit().putString(CONTRACT_ID, newInfo[0]).apply();
                    preferences.edit().putFloat(MONTHLY_COST, 830.30F).apply();
                    break;
                }
                case PERSONAL :{
                    preferences.edit().putString(USER_FIO, newInfo[0]).apply();
                    preferences.edit().putString(CARD_NUMBER, newInfo[1]).apply();
                    break;
                }
            }
            getViewState().setInfo(mail, contract, fio, cardNum);
            getViewState().closeDialog();
        } else {
            getViewState().showErrorToast(R.string.settings_wrong_pass);
        }
    }

    void exitRoomClicked() {
        getViewState().showRoomLeaveWarning();
    }

    void exitRoom() {
        String userKey = UserSessionManager.getInstance().getCurrentUser().getUid();
        String roomKey = preferences.getString(ROOM_KEY, "");
        if (!roomKey.isEmpty()) {
            ScheduleManagement.getInstance().leaveRoom(userKey, roomKey);
            preferences.edit().putBoolean(SIGNED_IN_ROOM, false).apply();
            preferences.edit().remove(ROOM_KEY).apply();
        }
        getViewState().closeDialog();
    }
}
