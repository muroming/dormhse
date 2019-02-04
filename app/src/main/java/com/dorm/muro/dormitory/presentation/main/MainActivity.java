package com.dorm.muro.dormitory.presentation.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.Group;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.dorm.muro.dormitory.paymentnotifications.PaymentNotificationReciever;
import com.dorm.muro.dormitory.paymentnotifications.PaymentNotificationService;
import com.dorm.muro.dormitory.presentation.options.OptionsFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;
import com.dorm.muro.dormitory.presentation.todo.TodoFragment;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.dorm.muro.dormitory.Constants.CONTRACT_ID;
import static com.dorm.muro.dormitory.Constants.SHARED_PREFERENCES;
import static com.dorm.muro.dormitory.Constants.TARGET_FRAGMENT;
import static com.dorm.muro.dormitory.Constants.USER_EMAIL;
import static com.dorm.muro.dormitory.Constants.USER_FIO;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView, Animation.AnimationListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @InjectPresenter
    MainActivityPresenter presenter;

    @BindView(R.id.splash)
    View mSplashGroup;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.vp_main_framgent_pager)
    ViewPager mViewPager;

    private ActionBar mBar;

    private Disposable userLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserSessionManager.getInstance().getCurrentUser() == null) {
            startActivity(getTargetIntent(LoginActivity.class));
            finish();
            return;
        }

        mBar = getSupportActionBar();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupViewPagerAdapter();

//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, PaymentNotificationReciever.class);
//        i.putExtra(TARGET_FRAGMENT, PaymentFragment.class.getSimpleName());
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pi);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_todo:
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

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        userLoading = UserSessionManager.getInstance().getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> {
                    preferences.edit()
                            .putString(USER_FIO, info[0])
                            .putString(CONTRACT_ID, info[1])
                            .putString(USER_EMAIL, info[2])
                            .apply();

                    new Handler().postDelayed(() -> {
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.splash_slide_up);
                        animation.setAnimationListener(MainActivity.this);
                        mSplashGroup.startAnimation(animation);
                    }, 1500);
                });
    }

    private void setupViewPagerAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(0, new TodoFragment(), getString(R.string.fragment_todo_title));
        adapter.addFragment(1, new ScheduleFragment(), getString(R.string.fragment_schedule_title));
        adapter.addFragment(2, new PaymentFragment(), getString(R.string.fragment_payment_title));
        adapter.addFragment(3, new OptionsFragment(), getString(R.string.fragment_options_title));

        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                int id = 0;

                switch (i) {
                    case 0: {
                        id = R.id.navigation_todo;
                        break;
                    }
                    case 1: {
                        id = R.id.navigation_schedule;
                        break;
                    }
                    case 2: {
                        id = R.id.navigation_payment;
                        break;
                    }
                    case 3: {
                        id = R.id.navigation_settings;
                    }
                }

                navigation.setSelectedItemId(id);
                setTitle(mViewPager.getAdapter().getPageTitle(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String fragmentToOpen = getIntent().getStringExtra(TARGET_FRAGMENT);

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
    public void showPage(int pageNum, int titleRes) {
        mViewPager.setCurrentItem(pageNum, true);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (userLoading != null) {
            userLoading.dispose();
            userLoading = null;
        }
    }

    @Override
    public void showSplash() {
        mSplashGroup.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
        navigation.setVisibility(View.INVISIBLE);
        mBar.hide();
    }

    @Override
    public void hideSplash() {
        mSplashGroup.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        navigation.setVisibility(View.VISIBLE);
        mBar.show();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        presenter.hideSplash();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
