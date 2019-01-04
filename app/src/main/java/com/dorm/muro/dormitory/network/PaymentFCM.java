package com.dorm.muro.dormitory.network;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PaymentFCM extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TARGET_FRAGMENT = "TARGET_FRAGMENT";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createPaymentNotification(remoteMessage.getNotification().getBody());
    }

    private void createPaymentNotification(String data) {
        Intent actionIntent = new Intent(this, MainActivity.class);
        actionIntent.putExtra(TARGET_FRAGMENT, PaymentFragment.class.getSimpleName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_payment_title))
                .setContentText(data)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat compat = NotificationManagerCompat.from(this);
        compat.notify(NOTIFICATION_ID, builder.build());
    }
}
