package com.dorm.muro.dormitory.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.presentation.options.OptionsFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;
import com.dorm.muro.dormitory.presentation.firstfragment.ShopsWorkingTimeFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.service.PaymentFCM;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dorm.muro.dormitory.Constants.*;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView {

    public static final String CHANNEL_ID = "DORMITORY_CHANNEL";
    public static final String APP_SECTION_TITLE = "SECTION_TITLE";
    public static final String DIALOG_TAG = "DIALOG_TAG";

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @InjectPresenter
    MainActivityPresenter presenter;

    SharedPreferences preferences;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        if (!preferences.getBoolean(IS_LOGGED, false)) {
            startActivity(getTargetIntent(LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_nearby:
                    presenter.showNearbyPlaces();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout_item: {
                preferences.edit().remove(IS_LOGGED).apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
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

        SpannableString string = new SpannableString(getString(titleRes));
        string.setSpan(new ForegroundColorSpan(getColor(R.color.black)), 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(string);
    }
}
