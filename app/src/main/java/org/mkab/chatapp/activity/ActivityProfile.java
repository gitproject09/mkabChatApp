package org.mkab.chatapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.User;

import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.Tools;

public class ActivityProfile extends BaseActivity {

    DataContext db = new DataContext(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("My Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        User user = LocalUserService.getLocalUserFromPreferences(this);
        TextView tv_UserFullName = (TextView) findViewById(R.id.tv_UserFullName);
        tv_UserFullName.setText(Tools.toProperName(user.FirstName) + " " + Tools.toProperName(user.LastName));
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


