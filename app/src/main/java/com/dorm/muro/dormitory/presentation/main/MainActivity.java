package com.dorm.muro.dormitory.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;
import com.dorm.muro.dormitory.presentation.firstfragment.ShopsWorkingTimeFragment;
import com.dorm.muro.dormitory.presentation.options.OptionsActivity;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.service.PaymentFCM;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dorm.muro.dormitory.presentation.login.LoginActivity.IS_LOGGED;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "DORMITORY_CHANNEL";
    public static final String APP_SECTION_TITLE = "SECTION_TITLE";
    public static final String SHARED_PREFERENCES = "APP_DORMITORY_PREFS";
    public static final String DIALOG_TAG = "DIALOG_TAG";

    public static void start(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    private Fragment paymentFragment, scheduleFragment, workTimeFragment;
    SharedPreferences preferences;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.tv_app_section_title)
    TextView sectionTitle;

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

        Log.d("FB", FirebaseInstanceId.getInstance().getToken());

        if (savedInstanceState != null) {
            sectionTitle.setText(savedInstanceState.getString(APP_SECTION_TITLE));
        }


        paymentFragment = new PaymentFragment();
        scheduleFragment = new ScheduleFragment();
        workTimeFragment = new ShopsWorkingTimeFragment();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String fragmentToOpen = getIntent().getStringExtra(PaymentFCM.TARGET_FRAGMENT);

        if(fragmentToOpen != null && fragmentToOpen.equals(PaymentFragment.class.getSimpleName())) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_main_fragment_container, paymentFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(APP_SECTION_TITLE, sectionTitle.getText().toString());
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
            case R.id.menu_settings_item: {
                startActivity(getTargetIntent(OptionsActivity.class));
                return true;
            }
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

    private Intent getTargetIntent(Class targetClass){
        Intent intent = new Intent(this, targetClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sectionTitle.setText("Nearby Places");
                    fragmentTransaction.replace(R.id.fl_main_fragment_container, workTimeFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    sectionTitle.setText("Cleaning Schedule");
                    fragmentTransaction.replace(R.id.fl_main_fragment_container, scheduleFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    sectionTitle.setText("Payment");
                    fragmentTransaction.replace(R.id.fl_main_fragment_container, paymentFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

}
