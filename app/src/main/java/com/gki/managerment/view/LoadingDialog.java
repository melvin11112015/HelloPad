package com.gki.managerment.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.gki.managerment.R;

/**
 * Created by summit on 2/15/16.
 */
public class LoadingDialog {
    private AlertDialog dialog;
    private View contentView;
    private WindowManager windowManager;
    //private TextView tvLoadingMsg;
    private int screenWidth;
    private int screenHeight;

    public LoadingDialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity).create();
        dialog.setCanceledOnTouchOutside(false);
        contentView = LayoutInflater.from(activity).inflate(R.layout.loading, null);
        windowManager = activity.getWindowManager();
        Configuration newConfig = activity.getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            screenWidth = windowManager.getDefaultDisplay().getHeight();
            screenHeight = windowManager.getDefaultDisplay().getWidth();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            screenWidth = windowManager.getDefaultDisplay().getWidth();
            screenHeight = windowManager.getDefaultDisplay().getHeight();
        }
    }
    public void show() {
        show(true);
    }
    public void show(boolean cancleable) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            final WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
            attrs.width = (int) (screenWidth * 0.18);
            attrs.height = (int) (screenHeight / 9.3);
            dialog.setCancelable(cancleable);
            dialog.setCanceledOnTouchOutside(cancleable);
            dialog.getWindow().setAttributes(attrs);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(contentView);
        }
    }
    public void dismiss() {
        if (dialog != null) {
            // tvLoadingMsg.setText(R.string.str_tima_loading_text);
            dialog.dismiss();
        }
    }
    public boolean isShowing() {
        return dialog.isShowing();
    }
    public void show(boolean cancelable, String message) {
        //tvLoadingMsg.setText(message == null ? "" : message);
        show(cancelable);
    }
    public void show(String message) {
        // tvLoadingMsg.setText(message == null ? "" : message);
        show(true);
    }
}