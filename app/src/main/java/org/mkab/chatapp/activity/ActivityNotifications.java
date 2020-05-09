package org.mkab.chatapp.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.mkab.chatapp.adapter.NotficationListAdapter;
import org.mkab.chatapp.R;
import org.mkab.chatapp.model.NotificationModel;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;

import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityNotifications extends BaseActivity {

    ListView lv_NotificationList;
    User user;
    List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        Firebase.setAndroidContext(this);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("NotificationCount", 0);
        editor.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        lv_NotificationList = (ListView) findViewById(R.id.lv_NoticicationList);
        notificationList = new ArrayList<>();
        user = LocalUserService.getLocalUserFromPreferences(this);
        Firebase reqRef = new Firebase(StaticInfo.EndPoint + "/friendrequests/" + user.Email);
        reqRef.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = dataSnapshot.getValue(Map.class);
                        String firstName = map.get("FirstName").toString();
                        String lastName = map.get("LastName").toString();
                      //  String majlish = map.get("Majlish").toString();
                        final String key = dataSnapshot.getKey();
                        NotificationModel not = new NotificationModel();
                        not.FirstName = firstName;
                        not.LastName = lastName;
                       // not.Majlish = majlish;
                        not.NotificationType = 1; // friend request
                        notificationList.add(not);
                        not.EmailFrom = key;
                        not.FriendRequestFireBaseKey = dataSnapshot.getKey();
                        not.NotificationMessage = Tools.toProperName(firstName) + " " + Tools.toProperName(lastName);
                        ListAdapter adp = new NotficationListAdapter(ActivityNotifications.this, notificationList);
                        lv_NotificationList.setAdapter(adp);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String friendEmail = dataSnapshot.getKey();
                        int index = -1;
                        for (int i = 0; i < notificationList.size(); i++) {
                            NotificationModel item = notificationList.get(i);
                            if (item.EmailFrom.equals(friendEmail))
                                index = i;
                        }
                        notificationList.remove(index);
                        ListAdapter adp = new NotficationListAdapter(ActivityNotifications.this, notificationList);
                        lv_NotificationList.setAdapter(adp);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
