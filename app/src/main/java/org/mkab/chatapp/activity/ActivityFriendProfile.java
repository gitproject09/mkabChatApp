package org.mkab.chatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;

import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.IFireBaseAPI;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ActivityFriendProfile extends AppCompatActivity {

    String friendEmail;
    TextView tv_FriendFullName;
    User user, f;
    ProgressDialog pd;
    Button btn_AddFriend;
    DataContext db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Firebase.setAndroidContext(this);
        btn_AddFriend = (Button) findViewById(R.id.btn_AddFriend);
        pd = new ProgressDialog(this);

        pd.setMessage("Loading...");
        f = new User();
        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("Email");
        tv_FriendFullName = (TextView) findViewById(R.id.tv_FriendFullName_L_FriendProfile);

        user = LocalUserService.getLocalUserFromPreferences(this);

        db = new DataContext(this, null, null, 1);

        // check if already friends otherwise get info from server
        User friend = db.getFriendByEmailFromLocalDB(friendEmail);
        if (friend.Email == null) {
            ImageButton btn_EditName = (ImageButton) findViewById(R.id.btn_EditName);
            btn_EditName.setVisibility(View.INVISIBLE);
            FindFriendsTask t = new FindFriendsTask();
            t.execute();
        } else {
            tv_FriendFullName.setText(Tools.toProperName(friend.FirstName) + " " + Tools.toProperName(friend.LastName));
            btn_AddFriend.setEnabled(false);
            btn_AddFriend.setText("Connected");

        }
    }


    public class FindFriendsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllUsersAsJsonString();

            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonListString) {
            try {
                JSONObject jsonObjectList = new JSONObject(jsonListString);
                JSONObject item = jsonObjectList.getJSONObject(friendEmail);
                f.Email = item.getString("Email");
                f.FirstName = item.getString("FirstName");
                f.LastName = item.getString("LastName");
                f.Majlish = item.getString("Majlish");
                tv_FriendFullName.setText(Tools.toProperName(f.FirstName) + " " + Tools.toProperName(f.LastName));
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
            }
        }
    }

    public void btn_SendFriendRequestClick(View view) {
        Firebase firebase = new Firebase(StaticInfo.BaseEndPoint + "/friendrequests");
        Firebase notifRef = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
        Map<String, String> map = new HashMap<>();
        map.put("FirstName", user.FirstName);
        map.put("LastName", user.LastName);
        map.put("Majlish", user.Majlish);
        firebase.child(Tools.encodeString(f.Email)).child(Tools.encodeString(user.Email)).setValue(map);
        btn_AddFriend.setEnabled(false);
        btn_AddFriend.setText("Request Sent");

        map.put("SenderEmail", user.Email);
        map.put("Message", "Pending contact request");
        map.put("NotificationType", "2");

        notifRef.push().setValue(map);

    }

    public void btn_EditNameClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set display name");
        final EditText et = new EditText(this);
        et.setText(tv_FriendFullName.getText());
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(et);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = et.getText().toString();
                db.setPreferedDisplayName(friendEmail, newName);
                tv_FriendFullName.setText(newName);
                setResult(Activity.RESULT_OK);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

}
