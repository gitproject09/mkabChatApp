package org.mkab.chatapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.mkab.chatapp.R;

public class DoctorDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";
    public static final String EXTRA_FROM = "coming_from";
    public static final String EXTRA_TITLE = "post_title";
    public static final String EXTRA_BODY = "post_body";
    public static final String EXTRA_MOBILE = "post_mobile";
    public static final String EXTRA_EMAIL = "post_email";

    private TextView mAuthorView, mTitleView, mBodyView, mMobile, mEmail;

    private ImageView imgCall;
    private ImageView imgMessage;
    private ImageView imgEmail;
    private ImageView imgChat;

    private String sMobileNum, sEmailAdd, sTitleName;
    private final static int REQUEST_CODE_PERMISSION_CALL_PHONE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTitleView = findViewById(R.id.post_title);
        mBodyView = findViewById(R.id.post_body);
        mEmail = findViewById(R.id.post_email);
        mMobile = findViewById(R.id.post_mobile);

        imgCall = findViewById(R.id.call);
        imgMessage = findViewById(R.id.message);
        imgEmail = findViewById(R.id.email);
        imgChat = findViewById(R.id.chat);

        imgCall.setOnClickListener(this);
        imgMessage.setOnClickListener(this);
        imgEmail.setOnClickListener(this);
        imgChat.setOnClickListener(this);


        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String body = getIntent().getStringExtra(EXTRA_BODY);
        String mobile = getIntent().getStringExtra(EXTRA_MOBILE);
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        String comingFrom = getIntent().getStringExtra(EXTRA_FROM);

        getSupportActionBar().setTitle(comingFrom);


        if(comingFrom.equalsIgnoreCase("DOCTOR_LIST")){
            imgChat.setVisibility(View.GONE);
        } else {
            imgChat.setVisibility(View.VISIBLE);
        }

        sTitleName = title;
        sMobileNum = mobile;
        sEmailAdd = email;

        mTitleView.setText(title);
        mBodyView.setText(body);
        mMobile.setText(mobile);
        mEmail.setText(email.replaceAll(",", "."));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.call:

                if (ActivityCompat.checkSelfPermission(DoctorDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DoctorDetailsActivity.this, new String[]{
                            (Manifest.permission.CALL_PHONE)}, REQUEST_CODE_PERMISSION_CALL_PHONE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + sMobileNum));
                    startActivity(intent);
                }
                break;

            case R.id.message:
                if (sMobileNum.equals("")) {
                    Toast.makeText(DoctorDetailsActivity.this, "No Mobile No found", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent msgIntent = new Intent(DoctorDetailsActivity.this, MessageSendActivity.class);
                    msgIntent.putExtra(MessageSendActivity.EXTRA_SMS_RECEIVER, sTitleName);
                    msgIntent.putExtra(MessageSendActivity.EXTRA_SMS_ADDRESS, sMobileNum);
                    startActivity(msgIntent);
                }
                break;

            case R.id.email:
                if (sEmailAdd.equals("")) {
                    Toast.makeText(DoctorDetailsActivity.this, "No Email Address found", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent emlInt = new Intent(DoctorDetailsActivity.this, MailSendActivity.class);
                    emlInt.putExtra(MailSendActivity.EXTRA_MAIL_ADDRESS, sEmailAdd.replaceAll(",", "."));
                    startActivity(emlInt);
                }
                break;

            case R.id.chat:
                Intent intend = new Intent(DoctorDetailsActivity.this, ActivityChat.class);
                intend.putExtra("FriendEmail", sEmailAdd);
                intend.putExtra("FriendFullName", sTitleName);
                startActivity(intend);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DoctorDetailsActivity.this, "Permission granted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + sMobileNum));
                    startActivity(intent);
                }
                break;
        }
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