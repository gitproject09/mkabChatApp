package org.mkab.chatapp.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.mkab.chatapp.R;
import org.mkab.chatapp.fragment.ChatListFragment;
import org.mkab.chatapp.fragment.ContactsListFragment;
import org.mkab.chatapp.fragment.DoctorsListFragment;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.service.Constants;
import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.MyService;
import org.mkab.chatapp.utils.LogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainUIActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private User user;
    Firebase refUser;
    private DataContext db;
    String chatTab = "";

    ArrayList<String> numbers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        Firebase.setAndroidContext(this);

        db = new DataContext(this, null, null, 1);

        user = LocalUserService.getLocalUserFromPreferences(this);
        //🄌 ➊ ➋ ➌ ➍ ➎ ➏ ➐ ➑ ➒ ➓
        // ⓵ ⓶ ⓷ ⓸ ⓹ ⓺ ⓻ ⓼ ⓽ ⓾
        //http://xahlee.info/comp/unicode_circled_numbers.html
        /*numbers.add("⓿");
        numbers.add("❶");
        numbers.add("❷");
        numbers.add("❸");
        numbers.add("❹");
        numbers.add("❺");
        numbers.add("❻");
        numbers.add("❼");
        numbers.add("❽");
        numbers.add("❾");
        numbers.add("❾+");*/

        numbers.add("⓿");
        numbers.add("⓵");
        numbers.add("⓶");
        numbers.add("⓷");
        numbers.add("⓸");
        numbers.add("⓹");
        numbers.add("⓺");
        numbers.add("⓻");
        numbers.add("⓼");
        numbers.add("⓽");
        numbers.add("⓽+");

        chatTab = getResources().getString(R.string.heading_chats);

        int currentChatNumber = LocalUserService.getChatCount(this);
        if (currentChatNumber > 0) {
            //   currentChatNumber = currentChatNumber - 1;
            if (currentChatNumber > 9) {
                chatTab = chatTab + " ⓽+";
            } else {
                chatTab = chatTab + " " + numbers.get(currentChatNumber);
            }

        }

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new DoctorsListFragment(),
                    new ChatListFragment(),
                    new ContactsListFragment()
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_doctors),
                    chatTab,
                    getString(R.string.heading_contacts)/*,
                    getString(R.string.heading_my_top_posts)*/
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        int limit = (mPagerAdapter.getCount() > 1 ? mPagerAdapter.getCount() - 1 : 1);
        mViewPager.setOffscreenPageLimit(limit);

        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainUIActivity.this, NewPostActivity.class));
                startActivity(new Intent(MainUIActivity.this, ActivityAddContact.class));
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d("currentTab", "Ekhon ase : " + tab.getPosition());

                if (tab.getPosition() == 0) {

                } else if (tab.getPosition() == 1) {
                    tab.setText(getString(R.string.heading_chats));
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("ChatCount", 0);
                    editor.commit();
                } else {

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // check if user exists in local db
        if (user.Email == null) {
            // send to activitylogin
//            Intent intent = new Intent(this, ActivityLogin.class);
//            startActivityForResult(intent, 100);
//
        } else {
            // startJobDispatcherService(ActivityMain.this);
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }

        }

        if (isMyServiceRunning(MyService.class)) {
            LogUtil.printInfoMessage(MainUIActivity.class.getSimpleName(), "onStart", "MyService : already running on Main UI Activity");
        } else {

            /*Intent stopIntent = new Intent(MainUIActivity.this, MyService.class);
            stopService(stopIntent);*/

            Intent startIntent = new Intent(MainUIActivity.this, MyService.class);
            startIntent.setAction(Constants.ACTION.START_ACTION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(startIntent);
            } else {
                startService(startIntent);
            }
            LogUtil.printWarningMessage(MainUIActivity.class.getSimpleName(), "onStart", "MyService : Start in Main UI Activity");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // set last seen
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        if (refUser != null) {
            refUser.child("Status").setValue(dateFormat.format(date));
        }

        // Tools.clearNotifications(this);
        /*Intent stopIntent = new Intent(MainUIActivity.this, MyService.class);
        stopService(stopIntent);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem alertMenuItem = menu.findItem(R.id.menu_refresh);
        LinearLayout rootView = (LinearLayout) alertMenuItem.getActionView();
        ImageView ivNoti = rootView.findViewById(R.id.ivNoti);
        TextView tvNotiCounter = rootView.findViewById(R.id.tvNotiCount);

        int currentNotiNumber = LocalUserService.getNotificationCount(getApplicationContext());
        LogUtil.printInfoMessage(MainUIActivity.class.getSimpleName(), "onPrepareOptionsMenu", "currentNotiCount : " + currentNotiNumber);
        if (currentNotiNumber > 1) {
            currentNotiNumber = currentNotiNumber - 1;
            tvNotiCounter.setVisibility(View.VISIBLE);
            tvNotiCounter.setText("" + currentNotiNumber);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNotiCounter.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), ActivityNotifications.class));
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout?")
                    .setMessage("Are you sure to logout, you will no longer receive notifications.")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // set last seen
                            DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                            Date date = new Date();
                            refUser.child("Status").setValue(dateFormat.format(date));
                            if (LocalUserService.deleteLocalUserFromPreferences(getApplicationContext())) {
                                db.deleteAllFriendsFromLocalDB();
                                db.deleteAllDoctorsFromLocalDB();

                                Intent stopIntent = new Intent(getApplicationContext(), MyService.class);
                                stopIntent.setAction(Constants.ACTION.STOP_ACTION);
                                stopService(stopIntent);
                            }
                            Toast.makeText(getApplicationContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ActivityProfile.class));
        }

        if (id == R.id.menu_addContacts) {
            startActivity(new Intent(this, ActivityAddContact.class));
            return true;
        }

        if (id == R.id.menu_notification) {
            startActivity(new Intent(this, ActivityNotifications.class));
            return true;
        }

        if (id == R.id.menu_refresh) {
            startActivity(new Intent(this, ActivityNotifications.class));
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
