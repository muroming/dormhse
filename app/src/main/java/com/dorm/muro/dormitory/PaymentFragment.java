package com.dorm.muro.dormitory;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PaymentFragment extends Fragment {

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(view);

        return view;
    }

    void createPaymentNotification(){
        Intent actionIntent = new Intent(getContext(), MainActivity.class);
        //ToDo: add extras to intent
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, actionIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.settings_icon)
                .setContentTitle(getString(R.string.notification_payment_title))
                .setContentText(getString(R.string.notification_payment_context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat compat = NotificationManagerCompat.from(getContext());
        compat.notify(MainActivity.NOTIFICATION_ID, builder.build());
    }


}
