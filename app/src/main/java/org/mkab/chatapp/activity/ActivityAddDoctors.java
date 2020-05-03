package org.mkab.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.service.IFireBaseAPI;
import org.mkab.chatapp.service.Tools;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;

public class ActivityAddDoctors extends AppCompatActivity {

    EditText et_Email, et_Password, et_FirstName, et_Majlish, et_LastName;
    Button btn_Register;
    ProgressDialog pd;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_entry);

        Firebase.setAndroidContext(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        et_Email = (EditText) findViewById(R.id.et_Email_Rigister);
        et_Password = (EditText) findViewById(R.id.et_Password_Rigister);
        et_FirstName = (EditText) findViewById(R.id.et_FirstName_Rigister);
        et_Majlish = (EditText) findViewById(R.id.et_Majlish_Rigister);
        et_LastName = (EditText) findViewById(R.id.et_LastName_Rigister);

    }

    public void btn_RegClick(View view) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } else if (et_FirstName.getText().toString().equals("")) {
            et_FirstName.setError("Enter Fullname");
        } else if (et_Majlish.getText().toString().equals("")) {
            et_LastName.setError("Enter Your Designation");
        } else if (et_LastName.getText().toString().equals("") || !et_LastName.getText().toString().startsWith("01") || et_LastName.getText().toString().length() != 11) {
            et_LastName.setError("Enter Valid Mobile No");
        } else if (et_Email.getText().toString().equals("") || !Tools.isValidEmail(et_Email.getText().toString())) {
            et_Email.setError("Enter Valid Email");
        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Enter Password");
        } else {
            email = Tools.encodeString(et_Email.getText().toString());
            RegisterUserTask t = new RegisterUserTask();
            t.execute();
        }
    }

    public class RegisterUserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getSingleUserByEmail(StaticInfo.UsersURL + "/" + email + ".json");
            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                if (jsonString.trim().equals("null")) {
                    Firebase firebase = new Firebase(StaticInfo.UsersURL);
                    firebase.child(email).child("FirstName").setValue(et_FirstName.getText().toString()); // Full Name
                    firebase.child(email).child("Majlish").setValue(et_Majlish.getText().toString());
                    firebase.child(email).child("LastName").setValue(et_LastName.getText().toString()); // Mobile No
                    firebase.child(email).child("Email").setValue(email);
                    firebase.child(email).child("Password").setValue(et_Password.getText().toString());
                    DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                    Date date = new Date();
                    firebase.child(email).child("Status").setValue(dateFormat.format(date));

                    Firebase doctorsFb = new Firebase(StaticInfo.DoctorsURL);
                    doctorsFb.child(email).child("FirstName").setValue(et_FirstName.getText().toString()); // Full Name
                    doctorsFb.child(email).child("Majlish").setValue(et_Majlish.getText().toString());
                    doctorsFb.child(email).child("LastName").setValue(et_LastName.getText().toString()); // Mobile No
                    doctorsFb.child(email).child("Email").setValue(email);
                    doctorsFb.child(email).child("OnDuty").setValue("0");
                    doctorsFb.child(email).child("Password").setValue(et_Password.getText().toString());
                    DateFormat doctorsDateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                    Date doctorsDate = new Date();
                    doctorsFb.child(email).child("Status").setValue(doctorsDateFormat.format(doctorsDate));

                    Toast.makeText(getApplicationContext(), "Signup as Doctor Successful", Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Email", email);
                    editor.putString("FirstName", et_FirstName.getText().toString());
                    editor.putString("LastName", et_LastName.getText().toString());
                    editor.putString("Majlish", et_Majlish.getText().toString());
                    editor.commit();

                    Intent i = new Intent(getApplicationContext(), MainUIActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                }
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            } catch (Exception e) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                e.printStackTrace();
            }
        }
    }

}
