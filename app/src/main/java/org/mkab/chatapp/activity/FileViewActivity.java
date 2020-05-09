package org.mkab.chatapp.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.mkab.chatapp.R;
import org.mkab.chatapp.interfaces.DialogClickListener;
import org.mkab.chatapp.service.Tools;
import org.mkab.chatapp.utils.FinishDialogChooser;
import org.mkab.chatapp.utils.LogUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileViewActivity extends BaseActivity {

    private Toolbar toolbar;

    private WebView webView;
    private ImageView imageView;
    //private ImageView ivDownload;
    FinishDialogChooser customDialog;
    private int hasWriteExtStorePMS;
    private static final int REQUEST_EXTERNAL_STORAGE = 3;
    ProgressDialog pDialog;
    private String ROOT_URL = "https://raw.githubusercontent.com/gitproject09/Resources/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_view);

        //  toolbar = findViewById(R.id.toolbar_btn);
        // toolbar.setTitle(getIntent().getStringExtra("Title"));
        // setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));

        webView = findViewById(R.id.webView);
        imageView = findViewById(R.id.imageView);

        //  ivDownload = toolbar.findViewById(R.id.ivDownload);

        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra("Type").equalsIgnoreCase("html")) {
                // ivDownload.setVisibility(View.GONE);
            } else {
                // ivDownload.setVisibility(View.VISIBLE);
            }
            LogUtil.printInfoMessage(FileViewActivity.class.getSimpleName(), "FileLink",
                    ROOT_URL + "master/image/helpdesk/" + getIntent().getStringExtra("FileLink"));

            LogUtil.printInfoMessage(FileViewActivity.class.getSimpleName(), "FileName", getIntent().getStringExtra("FileLink"));

        }

       /* ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(FileViewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
                            downloadImageFile(getIntent().getStringExtra("FileLink"));
                        } else {
                            ActivityCompat.requestPermissions(FileViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                        }
                    } else {
                        downloadImageFile(getIntent().getStringExtra("FileLink"));
                    }
                }
            }
        });*/

        customDialog = new FinishDialogChooser(FileViewActivity.this, "Download Successful", "Ok", "", new DialogClickListener() {
            @Override
            public void onYesClick(View view) {
                customDialog.dismiss();
            }

            @Override
            public void onNoClick(View view) {
                customDialog.dismiss();
            }

            @Override
            public void onCrossClick(View view) {
                customDialog.dismiss();
            }
        });


        String title = getIntent().getStringExtra("Title");
        String type = getIntent().getStringExtra("Type");
        String fileUrl = ROOT_URL + "master/image/helpdesk/" + getIntent().getStringExtra("FileLink");

        if (type.equalsIgnoreCase("image")) {
            //load image
            imageView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            Picasso.get()
                    .load(fileUrl + "?raw=true")
                    .networkPolicy(Tools.isNetworkAvailable(this) ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.mkab_transparent)
                    .into(imageView);
        } else if (type.equalsIgnoreCase("pdf") || type.equalsIgnoreCase("html")) {

            String pdfUrl = ROOT_URL + getIntent().getStringExtra("FileLink");

            loadPdfFile(webView, title, type, pdfUrl);
        } else {
            loadPdfFile(webView, title, type, getIntent().getStringExtra("FileLink"));
        }

    }

    private void loadPdfFile(WebView wbView, String title, String type, String fileUrl) {

        WebSettings webSettings = wbView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadsImagesAutomatically(true);

        wbView.setWebViewClient(new MyCustomWebViewClient());
        wbView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        pDialog = new ProgressDialog(this);
        pDialog.setTitle(title);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        wbView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();
            }
        });

        if (type.equalsIgnoreCase("pdf")) {
            try {
                fileUrl = URLEncoder.encode(fileUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                fileUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + fileUrl;
            }

        }
        //  fileUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + fileUrl;
        //  fileUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + "https://raw.githubusercontent.com/gitproject09/Resources/e4ab168ba6fbfe78c7dd200cb1a27c09b54e2cda/image/helpdesk/doctors_instructions.pdf";

        wbView.loadUrl(fileUrl);

    }

    private class MyCustomWebViewClient extends WebViewClient {

        /**
         * Override Url Loading when load an url in WebView Client
         *
         * @param view
         * @param url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * When Back icon pressed or Menu clicked
     *
     * @param item
     * @return boolean true or false
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == REQUEST_EXTERNAL_STORAGE) {
                        Toast.makeText(FileViewActivity.this, "Permission granted please click again", Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("You need to allow storage permission");
                    alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
                break;
        }
    }

    private void downloadImageFile(String updateFileName) {

        showDownloadProgressDialog();

        String downloadApkUrl = Tools.BASE_URL + "informations/" + updateFileName;
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        // String destination = Environment.getExternalStoragePublicDirectory(".Alorsathi") + "/";
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "contents" + "/" + updateFileName + ".jpg";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadApkUrl));
        request.setDescription("Downloading");
        request.setTitle(getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {

            public void onReceive(Context ctxt, Intent intent) {

                hideProgressDialog();
                customDialog.show();

            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}
