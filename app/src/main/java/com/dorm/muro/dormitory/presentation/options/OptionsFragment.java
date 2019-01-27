package com.dorm.muro.dormitory.presentation.options;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.Constants.SHARED_PREFERENCES;

public class OptionsFragment extends MvpAppCompatFragment implements OptionsView {

    @InjectPresenter
    OptionsPresenter presenter;

    private AlertDialog dialog;

    @BindView(R.id.notification_switch)
    Switch mSwitch;

    @BindView(R.id.mail)
    TextView mail;

    @BindView(R.id.contract)
    TextView contract;

    @BindView(R.id.fio)
    TextView fio;

    @BindView(R.id.card_data)
    TextView cardData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_options, container, false);
        ButterKnife.bind(this, v);
        presenter.setPreferences(getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE));
        return v;
    }

    @OnClick(R.id.change_password)
    void changePassword(View v) {
        presenter.onChangePasswordClicked();
    }

    @OnClick(R.id.mail_title)
    void changeMail(View v) {
        presenter.onChangeMailClicked();
    }

    @OnClick(R.id.contract_title)
    void changeContract(View v) {
        presenter.onChangeContractClicked();
    }

    @OnClick(R.id.exit_account)
    void exitAccount(View v) {
        presenter.onExitClicked();
    }

    @OnClick({R.id.notifications, R.id.notification_switch})
    void switchNotifications(View v) {
        presenter.onSwitchNotifications();
    }

    @OnClick(R.id.personal_data)
    void onPersonalDataClicked(View v) {
        presenter.onChangePersonalDataClicked();
    }

    @OnClick(R.id.exit_room)
    void exitRoom(View v) {
        presenter.exitRoomClicked();
    }

    @Override
    public void proceedToLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showChangeDialog(int title, int code, int[] hints, String[] values) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        EditText[] ets = new EditText[hints.length];
        for (int i = 0; i < hints.length; i++) {
            int hint = hints[i];
            EditText et = new EditText(getContext());
            et.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.settings_edit_text_width), ViewGroup.LayoutParams.WRAP_CONTENT));
            et.setHint(hint);
            et.setText(values[i]);
            ((LinearLayout.LayoutParams) et.getLayoutParams()).topMargin = getResources().getDimensionPixelSize(R.dimen.settings_edit_text_margins);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setMaxLines(1);

            if (hint == R.string.settings_card_number || hint == R.string.settings_change_cost) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            layout.addView(et);
            ets[i] = et;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(layout)
                .setTitle(title)
                .setNegativeButton(getString(R.string.cancel), (d, w) -> presenter.onDialogCancel())
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String password = ets[ets.length - 1].getText().toString();
                    String[] updates = new String[hints.length - 1];
                    for (int i = 0; i < ets.length - 1; i++) {
                        updates[i] = ets[i].getText().toString();
                    }
                    presenter.onChangeInfo(code, password, updates);
                });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showRoomLeaveWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.settings_exit_room_warning_title)
                .setMessage(R.string.settings_exit_room_warning_message)
                .setPositiveButton(R.string.select, (dialog1, which) -> {
                    presenter.exitRoom();
                })
                .setNegativeButton(R.string.cancel, ((dialog1, which) -> closeDialog()));

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void showErrorToast(int stringRes) {
        Toast.makeText(getContext(), getString(stringRes), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNotificationSwitch(boolean isEnabled) {
        mSwitch.setChecked(isEnabled);
    }

    @Override
    public void setInfo(String email, String contractId, String name, int cardNum) {
        if (email.isEmpty()) {
            mail.setText(R.string.field_not_set);
        } else {
            mail.setText(email);
        }

        if (contractId.isEmpty()) {
            contract.setText(R.string.field_not_set);
        } else {
            contract.setText(contractId);
        }

        if (name.isEmpty()) {
            fio.setText(R.string.field_not_set);
        } else {
            fio.setText(name);
        }
        if (cardNum == -1) {
            cardData.setText(R.string.field_not_set);
        } else {
            cardData.setText(getString(R.string.payment_card_tmp, cardNum));
        }
    }
}
