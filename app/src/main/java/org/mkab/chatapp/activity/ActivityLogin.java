package org.mkab.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ActivityLogin extends AppCompatActivity {

    private User user;
    Firebase refUser;

    DataContext db = new DataContext(this, null, null, 1);

    EditText et_Email, et_Password;
    Button btn_Login;
    ProgressDialog pd;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);

        et_Email = (EditText) findViewById(R.id.et_Email);
        et_Password = (EditText) findViewById(R.id.et_Password);

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {

        } else {
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }
        }
    }

    public void btnLoginClick(View view) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } else if (et_Email.getText().toString().equals("")) {
            et_Email.setError("Email cannot be empty");

        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Password cannot be empty");
        } else {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
            email = Tools.encodeString(et_Email.getText().toString());
            LoginTask t = new LoginTask();
            t.execute();
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(StaticInfo.BaseEndPoint)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            IFireBaseAPI api = retrofit.create(IFireBaseAPI.class);
            // Call<String> call = api.getAllUsersAsJsonString();
            Call<String> call = api.getSingleUserByEmail(StaticInfo.UsersURL + "/" + email + ".json");
            try {
                return call.execute().body();
            } catch (Exception e) {
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                return "null";
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {

            try {
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }
                if (!jsonString.trim().equals("null")) {
                    JSONObject userObj = new JSONObject(jsonString);
                    String pass = et_Password.getText().toString();
                    if (userObj.getString("Password").equals(pass)) {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("Email", email);
                        editor.putString("FirstName", userObj.getString("FirstName"));
                        editor.putString("LastName", userObj.getString("LastName"));
                        editor.putString("Majlish", userObj.getString("Majlish"));
                        editor.putInt("NotificationCount", 1);
                        editor.putInt("ChatCount", 0);
                        editor.commit();

                        startActivity(new Intent(ActivityLogin.this, MainUIActivity.class));
                        finish();

                    } else {
                        Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ActivityLogin.this, "Login Error, Please try again!!!", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {

                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }

                e.printStackTrace();
            }

        }

    }

    public void btnSignUpClick(View view) {

        startActivity(new Intent(this, ActivityRegister.class));
    }

    public void btnSignUpAsDoctorClick(View view) {

        startActivity(new Intent(this, ActivityAddDoctors.class));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
