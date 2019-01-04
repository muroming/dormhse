package com.dorm.muro.dormitory.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.network.authentication.UserSessionManager;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.network.PaymentFCM;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dorm.muro.dormitory.Constants.*;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView {

    public static final String CHANNEL_ID = "DORMITORY_CHANNEL";
    public static final String DIALOG_TAG = "DIALOG_TAG";

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @InjectPresenter
    MainActivityPresenter presenter;


    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (UserSessionManager.getInstance().getCurrentUser() == null) {
            startActivity(getTargetIntent(LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter.showTodoFragment();

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_nearby:
                    presenter.showTodoFragment();
                    return true;
                case R.id.navigation_schedule:
                    presenter.showScheduleFragment();
                    return true;
                case R.id.navigation_payment:
                    presenter.showPaymentFragment();
                    return true;
                case R.id.navigation_settings:
                    presenter.showSettingsFragment();
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String fragmentToOpen = getIntent().getStringExtra(PaymentFCM.TARGET_FRAGMENT);

        if (fragmentToOpen != null && fragmentToOpen.equals(PaymentFragment.class.getSimpleName())) {
            presenter.showPaymentFragment();
            navigation.setSelectedItemId(R.id.navigation_payment);
        }
    }

    private Intent getTargetIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void showFragment(Fragment fragment, int titleRes) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_fragment_container, fragment)
                .commit();

        showTitle(titleRes);
    }

    public void setUpButton(int id) {
        getSupportActionBar().setHomeAsUpIndicator(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideUpButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void showTitle(int titleRes) {
        SpannableString string = new SpannableString(getString(titleRes));
        string.setSpan(new ForegroundColorSpan(getColor(R.color.black)), 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(string);
    }
}
