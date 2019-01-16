package com.dorm.muro.dormitory.presentation.resetPassword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.google.firebase.FirebaseNetworkException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.et_new_password)
    EditText mNewPassword;
    @BindView(R.id.et_new_password_repeat)
    EditText mNewPasswordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle(R.string.activity_reset_password);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_reset_password)
    public void onResetClicked(View button){
        String password = mNewPassword.getText().toString(), passwordRepeat = mNewPasswordRepeat.getText().toString();

        if (password.isEmpty()) {
            mNewPassword.setError(getString(R.string.field_must_to_fill));
        }
        if(passwordRepeat.isEmpty()) {
            mNewPasswordRepeat.setError(getString(R.string.field_must_to_fill));
        }
        if (password.isEmpty() || passwordRepeat.isEmpty()) {
            return;
        }

        if (!password.equals(passwordRepeat)) {
            showToast(R.string.reset_pass_not_watch);
            return;
        }
        UserSessionManager.getInstance().updateUserPassword(password).addOnCompleteListener(reset -> {
           if(reset.isSuccessful()) {
               Toast.makeText(this, getString(R.string.reset_pass_successful), Toast.LENGTH_SHORT).show();
               MainActivity.start(this);
               finish();
           } else {
               Exception e = reset.getException();

               if (e instanceof FirebaseNetworkException)
                   showToast(R.string.network_exception);


           }
        });
    }

    private void showToast(int text) {
        Toast.makeText(this, getString(text), Toast.LENGTH_SHORT).show();
    }
}
