package com.gki.managerment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.gki.managerment.constant.SharedPreferenceConstant;
import com.gki.managerment.http.AppContants;
import com.gki.managerment.util.SharedPreferencesUtils;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.net.ApiTool;

import okhttp3.HttpUrl;

public class Setting extends BaseActivity implements OnClickListener {
    private Button btnBack, btnSave, btnDefault;
    private EditText et_servicePath, et_imagePath, et_odataPath, et_odataName, et_odataPassword, et_odataDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        btnDefault = (Button) findViewById(R.id.btn_default);
        btnDefault.setOnClickListener(this);
        et_servicePath = (EditText) findViewById(R.id.et_servicePath);
        et_imagePath = (EditText) findViewById(R.id.et_imagePath);
        et_odataPath = (EditText) findViewById(R.id.et2_odataPath);
        et_odataName = (EditText) findViewById(R.id.et2_odataName);
        et_odataPassword = (EditText) findViewById(R.id.et2_odataPassword);
        et_odataDomain = (EditText) findViewById(R.id.et2_odataDomain);

        showSetting();
    }

    public <T> void jump(Class<T> MyClass) {
        Intent intent = new Intent();
        intent.setClass(this, MyClass);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btn_back):
                Setting.this.finish();
                break;
            case R.id.btn_save:
                saveSetting();
                break;
            case R.id.btn_default:
                restoreSetting();
                break;
            default:
                break;
        }
    }

    private void restoreSetting() {
        et_servicePath.setText(AppContants.DEFAULT_SERVICE_PATH);
        et_imagePath.setText(AppContants.DEFAULT_IMAGE_PATH);
        et_odataPath.setText(ApiTool.DEFAULT_API_URL);
        et_odataName.setText(ApiTool.DEFAULT_AUTH_NAME);
        et_odataPassword.setText(ApiTool.DEFAULT_AUTH_PSW);
        et_odataDomain.setText(ApiTool.DEFAULT_AUTH_DOMAIN);
    }

    private void saveSetting() {
        boolean needShowToast = false;
        String odataUrl = et_odataPath.getText().toString().trim();
        if (!TextUtils.isEmpty(et_servicePath.getText().toString())
                && !TextUtils.isEmpty(et_odataPath.getText().toString())) {
            if (URLUtil.isHttpUrl(odataUrl) && HttpUrl.parse(odataUrl) != null) {
                if (!odataUrl.endsWith("/")) odataUrl = odataUrl + "/";
                ApiTool.currentApiUrl = odataUrl;
                SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.ODATA_PATH, ApiTool.currentApiUrl);
            } else {
                ToastUtil.show(Setting.this, "Odata地址错误");
                return;
            }
            needShowToast = true;
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.SERVICE_PATH, et_servicePath.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.IMAGE_PATH, et_imagePath.getText().toString());

            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.ODATA_USERNAME, et_odataName.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.ODATA_PASSWORD, et_odataPassword.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.ODATA_DOMAIN, et_odataDomain.getText().toString());
            AppContants.ServicePath = et_servicePath.getText().toString();
            AppContants.ImagePath = et_imagePath.getText().toString();
            ApiTool.currentAuthName = et_odataName.getText().toString();
            ApiTool.currentAuthPsw = et_odataPassword.getText().toString();
            ApiTool.currentAuthDomain = et_odataDomain.getText().toString();


            et_odataPath.setText(ApiTool.currentApiUrl);

        }
        if (needShowToast) {
            ShowTipMessage("保存成功！");
        }
    }

    private void showSetting() {
        et_servicePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.SERVICE_PATH, AppContants.ServicePath).toString());
        et_imagePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.IMAGE_PATH, AppContants.ImagePath).toString());
        et_odataPath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_PATH, ApiTool.currentApiUrl).toString());
        et_odataName.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_USERNAME, ApiTool.currentAuthName).toString());
        et_odataPassword.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_PASSWORD, ApiTool.currentAuthPsw).toString());
        et_odataDomain.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_DOMAIN, ApiTool.currentAuthDomain).toString());
    }
}
