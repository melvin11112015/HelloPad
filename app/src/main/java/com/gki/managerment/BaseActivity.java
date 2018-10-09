package com.gki.managerment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.gki.managerment.util.StringUtils;
import com.gki.managerment.view.LoadingDialog;

/**
 * base activity
 * Created by summit on 2/15/16.
 */
public class BaseActivity extends FragmentActivity {
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        loadingDialog = new LoadingDialog(this);
        //检查网络
        CheckNet();
    }

    public void showLoadingDialog(boolean cancleable) {
        if (loadingDialog != null) {
            loadingDialog.show(cancleable);
        }
    }

    public void showLoadingDialog() {
        showLoadingDialog(true);
    }

    public void showLoadingDialog(boolean cancelable, String message) {
        if (loadingDialog != null) {
            loadingDialog.show(cancelable, message);
        }
    }

    public void showLoadingDialog(String message) {
        if (loadingDialog != null) {
            loadingDialog.show(message);
        }
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
        loadingDialog = null;
    }

    public <T> void OpenForm(Class<T> MyClass) {
        Intent intent = new Intent();
        intent.setClass(this, MyClass);
        startActivity(intent);
    }

    public void CheckNet()
    {
        //检查网络
        ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
        /*
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        //boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  //没有移动网络的平板会有问题
        //if(wifi|internet){
        if(wifi){
            //执行相关操作
        }else{
            Toast.makeText(getApplicationContext(),"网络故障，请确认您的WIFI是开启的并正确连接网络！", Toast.LENGTH_LONG).show();
        }
        */

        NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
        if(activeNetworkInfo==null||!activeNetworkInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "网络不可用！", Toast.LENGTH_SHORT).show();
        }else{//可能联网
            int networkType = activeNetworkInfo.getType();
            if(networkType != ConnectivityManager.TYPE_WIFI){
                Toast.makeText(getApplicationContext(), "网络故障，请确认您的WIFI是开启的并正确连接网络！"
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void ShowLongTipMessage(String strMsg)
    {
        Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_LONG).show();
    }

    protected void ShowTipMessage(String strMsg)
    {
        if (strMsg.indexOf("Error:") == 0)
        {
            ShowLongTipMessage(strMsg.replace("Error:",""));
        }
        else
        {
            Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void ShowTipMessage(String strMsg, String strReturnMsg)
    {
        //如果返回有错误，显示错误信息，否则显示预期信息
        if (StringUtils.isNotEmpty(strReturnMsg) && strReturnMsg.indexOf("Error:") == 0)
        {
            ShowTipMessage(strReturnMsg);
        }
        else
        {
            ShowLongTipMessage(strMsg);
        }
    }
}