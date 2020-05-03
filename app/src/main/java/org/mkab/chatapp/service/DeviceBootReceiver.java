package org.mkab.chatapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.mkab.chatapp.utils.LogUtil;

public class DeviceBootReceiver extends BroadcastReceiver {

    Context mContext = null;

    /**
     * Receives When device is rooted or restarted
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            LogUtil.printInfoMessage(DeviceBootReceiver.class.getSimpleName(), "onReceive", "Called");

            /*Intent startServiceIntent = new Intent(mContext, MyJobService.class);
            startServiceIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(startServiceIntent);*/
        }
    }
}