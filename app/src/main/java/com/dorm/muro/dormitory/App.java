package com.dorm.muro.dormitory;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.dorm.muro.dormitory.dagger.Injector;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    public static final String CHANNEL_ID = "DORMITORY_CHANNEL";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Injector.init(getApplicationContext());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }




}
