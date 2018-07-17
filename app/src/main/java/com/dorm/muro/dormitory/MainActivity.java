package com.dorm.muro.dormitory;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "DORMITORY_CHANNEL";
    public static final int NOTIFICATION_ID = 1;
    public static final String APP_SECTION_TITLE = "SECTION_TITLE";

    private Fragment paymentFragment;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.tv_app_section_title)
    TextView sectionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(savedInstanceState != null){
            sectionTitle.setText(savedInstanceState.getString(APP_SECTION_TITLE));
        }

        createNotificationChannel();

        paymentFragment = new PaymentFragment();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(APP_SECTION_TITLE, sectionTitle.getText().toString());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sectionTitle.setText("Home");
                    return true;
                case R.id.navigation_dashboard:
                    sectionTitle.setText("Dash");
                    return true;
                case R.id.navigation_notifications:
                    sectionTitle.setText("Payment");
                    fragmentTransaction.add(R.id.fl_main_fragment_container, paymentFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

}
