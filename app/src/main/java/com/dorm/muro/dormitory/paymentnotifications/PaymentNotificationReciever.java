package com.dorm.muro.dormitory.paymentnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PaymentNotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, PaymentNotificationService.class);
        context.startService(intent1);
    }
}
