package org.mkab.chatapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       context.startService(new Intent(context, AppService.class));
    }
}
