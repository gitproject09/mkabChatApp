package org.mkab.chatapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.mkab.chatapp.service.MyService;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       //context.startService(new Intent(context, AppService.class));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startForegroundService(new Intent(this, MyJobService.class));
            // ContextCompat.startForegroundService(this, new Intent(getApplicationContext(), MyJobService.class));
        } else {
            context.startService(new Intent(context, MyService.class));
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyService.class));
        } else {
            context.startService(new Intent(context, MyService.class));
        }
    }
}
