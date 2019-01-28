package com.dorm.muro.dormitory.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.dorm.muro.dormitory.paymentnotifications.PaymentNotificationService;
import com.dorm.muro.dormitory.presentation.options.OptionsFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;
import com.dorm.muro.dormitory.presentation.todo.TodoFragment;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView {

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @InjectPresenter
    MainActivityPresenter presenter;


    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.vp_main_framgent_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (UserSessionManager.getInstance().getCurrentUser() == null) {
            startActivity(getTargetIntent(LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupViewPagerAdapter();

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
        String fragmentToOpen = getIntent().getStringExtra(PaymentNotificationService.TARGET_FRAGMENT);

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
}
