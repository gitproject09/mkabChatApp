package org.mkab.chatapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mkab.chatapp.R;
import org.mkab.chatapp.interfaces.DialogClickListener;
import org.mkab.chatapp.utils.FinishDialogChooser;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MessageSendActivity extends BaseActivity {

    public static final String EXTRA_SMS_ADDRESS = "sms_address";
    public static final String EXTRA_SMS_RECEIVER = "sms_receiver";
    private Button btnSendSMS;
    private EditText etToMessage, etMessage, etSender;
    private TextView receiverName;
    private final static int REQUEST_CODE_PERMISSION_SEND_SMS = 123;
    String phoneNumber;
    String receiver;
    private ProgressDialog progressDialog;
    FinishDialogChooser finishDialogChooser;

    public static MessageSendActivity instance;

    public static MessageSendActivity Instance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Send Message to Doctor");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        instance = this;

        phoneNumber = getIntent().getStringExtra(EXTRA_SMS_ADDRESS);
        receiver = getIntent().getStringExtra(EXTRA_SMS_RECEIVER);
        if (phoneNumber == null || receiver == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        etToMessage = (EditText) findViewById(R.id.etToMessage);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etSender = (EditText) findViewById(R.id.etSenderNumber);
        receiverName = (TextView) findViewById(R.id.receiverName);
        etMessage.requestFocus();

        btnSendSMS = (Button) findViewById(R.id.btnSendMessage);

        btnSendSMS.setEnabled(false);

        etToMessage.setText(phoneNumber);
        receiverName.setText(receiver);

        SharedPreferences prefs = getSharedPreferences("PHONE_NUMBER", MODE_PRIVATE);
        String senderPhoneNumber = prefs.getString("phoneNumber", "No phone no");//"No name defined" is the default value.
        Log.e("qaidIndex", "SenderNumber : " + senderPhoneNumber);
        if (!senderPhoneNumber.equals("No phone no")) {
            etSender.setText(senderPhoneNumber);
        }

        if (checkPermission(Manifest.permission.SEND_SMS)) {
            btnSendSMS.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(MessageSendActivity.this, new String[]{
                    (Manifest.permission.SEND_SMS)}, REQUEST_CODE_PERMISSION_SEND_SMS);
        }


        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMessage.getText().toString().trim();
                String destNumber = phoneNumber;
                String senderNumber = etSender.getText().toString().trim();

                if (msg.equals("")) {
                    etMessage.setError("Required");
                    return;
                }
                if (destNumber.equals("") || !destNumber.startsWith("01") && destNumber.length() != 11) {
                    etToMessage.setError("Invalid Number");
                    return;
                }
                if (senderNumber.equals("") || !senderNumber.startsWith("01") && senderNumber.length() != 11) {
                    etSender.setError("Invalid Number");
                    return;
                }

                SharedPreferences.Editor editor = getSharedPreferences("PHONE_NUMBER", MODE_PRIVATE).edit();
                editor.putString("phoneNumber", senderNumber);
                editor.commit();


              /*  Log.e("qaidIndex", "Retrieve Phone Number : " + phoneNumber);
                if (phoneNumber != null) {
                    String name = prefs.getString("phoneNumber", "No phone no");//"No name defined" is the default value.
                }*/

                //sendSMS(destNumber, msg, senderNumber);
                sendSMS(destNumber, msg, etSender.getText().toString().trim());

                /*SmsManager smsMan = SmsManager.getDefault();
                smsMan.sendTextMessage("88" + phoneNum, null, msg, null, null);
                Toast.makeText(MessageSendActivity.this, "SMS send to " + phoneNum, Toast.LENGTH_LONG).show();*/
            }
        });
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnSendSMS.setEnabled(true);
                }
                break;
        }
    }

    private void sendSMS(final String receiverNumber, final String message, final String senderNumber) {

        showLoader();
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        dismissLoader();
                        //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        finishDialogChooser = new FinishDialogChooser(MessageSendActivity.this, "SMS successfully sent to " + receiverNumber, "Ok", "No", new DialogClickListener() {
                            @Override
                            public void onYesClick(View view) {
                                finish();
                            }

                            @Override
                            public void onNoClick(View view) {
                                finishDialogChooser.dismiss();
                            }

                            @Override
                            public void onCrossClick(View view) {
                                finishDialogChooser.dismiss();
                            }
                        });
                        finishDialogChooser.show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        dismissLoader();
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        dismissLoader();
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        dismissLoader();
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        dismissLoader();
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        dismissLoader();
                        //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        //finish();
                        break;
                    case Activity.RESULT_CANCELED:
                        dismissLoader();
                       // Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        finishDialogChooser = new FinishDialogChooser(MessageSendActivity.this, "SMS not delivered", "Ok", "No", new DialogClickListener() {
                            @Override
                            public void onYesClick(View view) {
                                finishDialogChooser.dismiss();
                            }

                            @Override
                            public void onNoClick(View view) {
                                finishDialogChooser.dismiss();
                            }

                            @Override
                            public void onCrossClick(View view) {
                                finishDialogChooser.dismiss();
                                finish();
                            }
                        });
                        finishDialogChooser.show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        // sms.sendTextMessage("+88" + phoneNumber, null, message, sentPI, deliveredPI);
        //sms.sendTextMessage("88" + receiverNumber, "88" + senderNumber, message, sentPI, deliveredPI);
        sms.sendTextMessage("88" + receiverNumber, "88" + senderNumber, message, sentPI, deliveredPI);
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

    public void showLoader() {

        if (progressDialog != null) {
            progressDialog.setMessage("Sending...");
            progressDialog.show();
        }

    }

    public void dismissLoader() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
