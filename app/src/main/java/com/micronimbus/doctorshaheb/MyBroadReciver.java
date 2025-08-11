package com.micronimbus.doctorshaheb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("medicineName", medicineName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
