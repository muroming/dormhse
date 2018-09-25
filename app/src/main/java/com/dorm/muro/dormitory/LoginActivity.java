package com.dorm.muro.dormitory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.MainActivity.SHARED_PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    public static final String IS_LOGGED = "LOGIN_STATUS";

    @BindView(R.id.et_login_mail)
    public EditText mLoginEditText;

    @BindView(R.id.et_login_password)
    public EditText mPasswordEditText;

    private SharedPreferences preferences;
    private Intent mainActivityIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        if(preferences.getBoolean(IS_LOGGED, false)){
            startActivity(mainActivityIntent);
        }
    }

    @OnClick(R.id.btn_login_login)
    public void signUp() {
        //TODO: create sign up method
        Toast.makeText(getApplicationContext(), "Create sign up method", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.tv_login_create_account)
    public void signIn(){
        if(checkUser()){
            preferences.edit()
                    .putBoolean(IS_LOGGED, true)
                    .apply();
            startActivity(mainActivityIntent);
        }
    }

    private boolean checkUser(){
        //TODO: create check user method
        Toast.makeText(getApplicationContext(), "create check user method", Toast.LENGTH_SHORT).show();
        return mLoginEditText.getText().toString().equals("login") && mPasswordEditText.getText().toString().equals("pass");
    }
}
