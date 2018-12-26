package com.dorm.muro.dormitory.presentation.options;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    @Override
    public void proceedToLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showChangeDialog(int h1, int h2, int title, int type) {
        String hint1 = getString(h1), hint2 = getString(h2);
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.settings_change_dialog, null);
        EditText et1 = layout.findViewById(R.id.et1), et2 = layout.findViewById(R.id.et2);
        et1.setHint(hint1);
        et2.setHint(hint2);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(title))
                .setView(layout)
                .setNegativeButton(getString(R.string.cancel), (d, w) -> presenter.onDialogCancel())
                .setPositiveButton(getString(R.string.save), (d, w) -> presenter.onChangeInfo(type,
                        et2.getText().toString(), et1.getText().toString()));

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showBigChangeDialog(int h1, int h2, int h3, int title, int code) {
        String hint1 = getString(h1), hint2 = getString(h2), hint3 = getString(h3);
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(getContext()).inflate(R.layout.settings_change_dialog, null);
        EditText et1 = layout.findViewById(R.id.et1), et2 = layout.findViewById(R.id.et2);
        et1.setHint(hint1);
        et2.setHint(hint2);

        //Setup third confirm password view
        EditText confirm = new EditText(getContext());
        confirm.setHint(h3);
        confirm.setLayoutParams(new ConstraintLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.settings_edit_text_width),
                getResources().getDimensionPixelSize(R.dimen.settings_edit_text_height)));
        ((ConstraintLayout.LayoutParams) confirm.getLayoutParams()).setMargins(0,
                getResources().getDimensionPixelSize(R.dimen.settings_edit_text_margins), 0, 0);
        ((ConstraintLayout.LayoutParams) confirm.getLayoutParams()).startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        ((ConstraintLayout.LayoutParams) confirm.getLayoutParams()).endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        ((ConstraintLayout.LayoutParams) confirm.getLayoutParams()).topToBottom = R.id.et2;

        layout.addView(confirm);

        //Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(title))
                .setView(layout)
                .setNegativeButton(getString(R.string.cancel), (d, w) -> presenter.onDialogCancel())
                .setPositiveButton(getString(R.string.save), null);

        dialog = builder.create();
        dialog.setOnShowListener(dialog -> ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> presenter.onChangeInfo(code, confirm.getText().toString(),
                        et1.getText().toString(), et2.getText().toString())));
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
