package org.mkab.chatapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.mkab.chatapp.R;
import org.mkab.chatapp.activity.ActivityChat;
import org.mkab.chatapp.activity.ActivityNotifications;
import org.mkab.chatapp.activity.MainUIActivity;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.utils.LogUtil;

import java.util.Map;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();
    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private NotificationManager mNotificationManager;
    private Handler handler;
    private int count = 0;
    private static int stateService = Constants.STATE_SERVICE.NOT_CONNECTED;
    User user;
    Firebase firebaseReference;
    ChildEventListener childEventListener;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        stateService = Constants.STATE_SERVICE.NOT_CONNECTED;

        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        firebaseReference = new Firebase(StaticInfo.NotificationEndPoint + "/" + user.Email);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Email != null) {
                    LogUtil.printInfoMessage(MyService.class.getSimpleName(), "MyService", "Ekhane Ashe?");
                    Map map = dataSnapshot.getValue(Map.class);
                    String mess = map.get("Message").toString();
                    LogUtil.printInfoMessage(MyService.class.getSimpleName(), "MyService", "Message : " + mess);
                    String senderEmail = map.get("SenderEmail").toString();
                    String senderFullName = Tools.toProperName(map.get("FirstName").toString());// + " " + Tools.toProperName(map.get("LastName").toString());

                    int notificationType = 1; // Message
                    notificationType = map.get("NotificationType") == null ? 1 : Integer.parseInt(map.get("NotificationType").toString());
                    // check if user is on chat activity with senderEmail
                    //notifyUser(senderEmail, senderFullName, mess, notificationType);
                    if (!StaticInfo.UserCurrentChatFriendEmail.equals(senderEmail)) {
                        notifyUser(senderEmail, senderFullName, mess, notificationType);
                        // remove notification
                        firebaseReference.child(dataSnapshot.getKey()).removeValue();
                    } else {
                        firebaseReference.child(dataSnapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildChanged", "data : " + s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildRemoved", "data : " + dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildMoved", "data : " + s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onCancelled", "data : " + firebaseError.getMessage());
            }
        };
    }

    @Override
    public void onDestroy() {
        stateService = Constants.STATE_SERVICE.NOT_CONNECTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction() == Constants.ACTION.START_ACTION) {
                LogUtil.printInfoMessage(TAG, "onStartCommand", "Start Action Called : starts foreground intent");
                startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

               /* boolean isServiceStart = LocalUserService.getServiceStatus(getApplicationContext());

                if(isServiceStart){
                    LogUtil.printInfoMessage(TAG, "isServiceStart", "No need to create firebaseNotificationMethod");
                } else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("ServiceStart", true);
                    editor.commit();

                }*/
                //firebaseNotificationMethod();

                firebaseReference.addChildEventListener(childEventListener);
                connect();
            } else  if (intent.getAction() == Constants.ACTION.STOP_ACTION) {
                LogUtil.printWarningMessage(TAG, "onStartCommand", "Stop Action Called");
                stopForeground(true);
                stopSelf();

                firebaseReference.removeEventListener(childEventListener);

                return START_NOT_STICKY;
            }
            return START_STICKY;
        } else {
            LogUtil.printWarningMessage(TAG, "onStartCommand", "Intent Null : Stop forground");
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.printWarningMessage(MyService.class.getSimpleName(), "onTaskRemoved", "Called");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE);
    }

    private void firebaseNotificationMethod() {
        LogUtil.printInfoMessage(MyService.class.getSimpleName(), "firebaseNotificationMethod", "Start");
       // Firebase.setAndroidContext(getApplicationContext());
        user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        firebaseReference = new Firebase(StaticInfo.NotificationEndPoint + "/" + user.Email);

        firebaseReference.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (LocalUserService.getLocalUserFromPreferences(getApplicationContext()).Email != null) {
                            LogUtil.printInfoMessage(MyService.class.getSimpleName(), "MyService", "Ekhane Ashe?");
                            Map map = dataSnapshot.getValue(Map.class);
                            String mess = map.get("Message").toString();
                            LogUtil.printInfoMessage(MyService.class.getSimpleName(), "MyService", "Message : " + mess);
                            String senderEmail = map.get("SenderEmail").toString();
                            String senderFullName = Tools.toProperName(map.get("FirstName").toString());// + " " + Tools.toProperName(map.get("LastName").toString());

                            int notificationType = 1; // Message
                            notificationType = map.get("NotificationType") == null ? 1 : Integer.parseInt(map.get("NotificationType").toString());
                            // check if user is on chat activity with senderEmail
                            //notifyUser(senderEmail, senderFullName, mess, notificationType);
                            if (!StaticInfo.UserCurrentChatFriendEmail.equals(senderEmail)) {
                                notifyUser(senderEmail, senderFullName, mess, notificationType);
                                // remove notification
                                firebaseReference.child(dataSnapshot.getKey()).removeValue();
                            } else {
                                firebaseReference.child(dataSnapshot.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildChanged", "data : " + s);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildRemoved", "data : " + dataSnapshot.toString());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onChildMoved", "data : " + s);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        LogUtil.printInfoMessage(MyService.class.getSimpleName(), "onCancelled", "data : " + firebaseError.getMessage());
                    }
                }
        );
    }

    private void notifyUser(String friendEmail, String senderFullName, String mess, int notificationType) {

        // notification builder
        NotificationCompat.Builder not;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            not = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            not = new NotificationCompat.Builder(this);
        }

        //NotificationCompat.Builder not = new NotificationCompat.Builder(getApplicationContext());
        not.setAutoCancel(true);
        not.setSmallIcon(R.mipmap.ic_launcher_round);
        not.setTicker("New Message");
        not.setWhen(System.currentTimeMillis());
        not.setContentText(mess);
        Intent i;
        // 1) Message 3) Contact Request Accepted
        if (notificationType == 1 || notificationType == 3) {
            i = new Intent(getApplicationContext(), ActivityChat.class);
            DataContext db = new DataContext(getApplicationContext(), null, null, 1);
            User frnd = db.getFriendByEmailFromLocalDB(friendEmail);
            if (frnd.FirstName != null) {
                not.setContentTitle(frnd.FirstName);// + " " + frnd.LastName);
                i.putExtra("FriendFullName", frnd.FirstName);// + " " + frnd.LastName);
            } else {
                not.setContentTitle(senderFullName);
                i.putExtra("FriendFullName", senderFullName);
            }
            if (notificationType == 1) {
                int currentChatNumber = LocalUserService.getChatCount(getApplicationContext());

                SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("ChatCount", currentChatNumber + 1);
                editor.commit();
            }

            if (notificationType == 3) {
                int currentNotiNumber = LocalUserService.getNotificationCount(getApplicationContext());
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("NotificationCount", currentNotiNumber + 1);
                editor.commit();
            }
        }
        // Contact Request
        else if (notificationType == 2) {

            int currentNotiNumber = LocalUserService.getNotificationCount(getApplicationContext());

            SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("NotificationCount", currentNotiNumber + 1);
            editor.commit();

            i = new Intent(getApplicationContext(), ActivityNotifications.class);
            not.setContentTitle(senderFullName);
        } else {
            i = null;
        }
        i.putExtra("FriendEmail", friendEmail);
        int uniqueID = Tools.createUniqueIdPerUser(friendEmail);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        not.setContentIntent(pendingIntent);
        not.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.text_name_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            nm.createNotificationChannel(channel);
        }

        nm.notify(uniqueID, not.build());

    }

    // its connected, so change the notification text
    private void connect() {
        // after 10 seconds its connected
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.d(TAG, "Bluetooth Low Energy device is connected!!");
                        // Toast.makeText(getApplicationContext(),"Connected!",Toast.LENGTH_SHORT).show();
                        stateService = Constants.STATE_SERVICE.CONNECTED;
                        startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                    }
                }, 10000);

    }


    private Notification prepareNotification() {
        // handle build version above android oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            CharSequence name = getString(R.string.text_name_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            channel.enableVibration(false);
            mNotificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainUIActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // if min sdk goes below honeycomb
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // make a stop intent
        Intent stopIntent = new Intent(this, MyService.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.service_notification);
        remoteViews.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);

        // if it is connected
        switch (stateService) {
            case Constants.STATE_SERVICE.NOT_CONNECTED:
                remoteViews.setTextViewText(R.id.tv_state, "Help Desk DISCONNECTED");
                remoteViews.setTextColor(R.id.tv_state, getResources().getColor(android.R.color.holo_red_light));
                break;
            case Constants.STATE_SERVICE.CONNECTED:
                remoteViews.setTextColor(R.id.tv_state, getResources().getColor(android.R.color.black));
                remoteViews.setTextViewText(R.id.tv_state, "Help Desk CONNECTED");
                break;
        }

        // notification builder
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setVisibility(Notification.VISIBILITY_SECRET);
        }

        return notificationBuilder.build();
    }

    private void firebaseEventnMethod() {

        firebaseReference.addChildEventListener(childEventListener);

    }
}
