package com.dorm.muro.dormitory.paymentnotifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import static com.dorm.muro.dormitory.Constants.*;

public class PaymentNotificationService extends IntentService {

    private static final int NOTIFICATION_ID = 3;

    public PaymentNotificationService() {
        super("PaymentNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getBoolean(NOTIFICATIONS, true))
            return;

        Context context = getApplicationContext();
        Intent startingIntent = new Intent(context, MainActivity.class);
        startingIntent.putExtra(TARGET_FRAGMENT, PaymentFragment.class.getSimpleName());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.notification_payment_title))
                .setContentText(context.getString(R.string.notification_payment_text))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_card)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(NOTIFICATION_ID, notification);
    }
}
