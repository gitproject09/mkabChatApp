package org.mkab.chatapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.mkab.chatapp.R;
import org.mkab.chatapp.interfaces.DialogClickListener;


public class FinishDialogChooser extends AppCompatDialog implements View.OnClickListener {

    public String message;
    public String yesText;
    public String noText;
    public Window window;
    public Button yes, no, cross;
    public TextView tvDialogTitle;
    public DialogClickListener dialogClickListener;

    public FinishDialogChooser(Context activity, String message, String yesText, String noText, DialogClickListener dialogClickListener) {
        super(activity);
        // TODO Auto-generated constructor stub

        this.dialogClickListener = dialogClickListener;
        this.message = message;
        this.yesText = yesText;
        this.noText  = noText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.custom_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        window = this.getWindow();

        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        yes             = findViewById(R.id.gallery);
        no              = findViewById(R.id.camera);
        cross           = findViewById(R.id.profile_image_center);
        tvDialogTitle   = findViewById(R.id.txt_dialog);

        tvDialogTitle.setText(message);
        yes.setText(yesText);
        no.setVisibility(View.GONE);

        yes.setOnClickListener(this);
        //no.setOnClickListener(this);
        cross.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.gallery: // yes button clicked
                dialogClickListener.onYesClick(v);
                break;

            case R.id.camera: // No button clicked
                dialogClickListener.onNoClick(v);
                break;

            case R.id.profile_image_center: // Cross button clicked
                dialogClickListener.onCrossClick(v);
                break;

            default:
                break;
        }
        dismiss();
    }
}