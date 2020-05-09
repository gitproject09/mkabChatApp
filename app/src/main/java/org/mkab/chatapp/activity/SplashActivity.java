package org.mkab.chatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.client.Firebase;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.utils.LogUtil;

public class SplashActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private User user;
    Firebase refUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        user = LocalUserService.getLocalUserFromPreferences(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //startActivity(new Intent(SplashActivity.this, ActivityMain.class));

                if (user.Email == null) {
                    startActivity(new Intent(SplashActivity.this, ActivityLogin.class));
                } else {
                    if (refUser == null) {
                        refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
                    }
                    LogUtil.printInfoMessage(SplashActivity.class.getSimpleName(), "CurrentUser", "email : " + user.Email);
                    startActivity(new Intent(SplashActivity.this, MainUIActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}