package com.gki.managerment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gki.managerment.constant.SharedPreferenceConstant;
import com.gki.managerment.http.AppContants;
import com.gki.managerment.util.SharedPreferencesUtils;
import com.gki.v107.net.ApiTool;

public class Setting extends BaseActivity implements OnClickListener {
    private Button btnBack, btnSave;
    private EditText et_servicePath,et_imagePath,et_odataPath,et_odataName,et_odataPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        et_servicePath = (EditText) findViewById(R.id.et_servicePath);
        et_imagePath = (EditText) findViewById(R.id.et_imagePath);
        et_odataPath = (EditText) findViewById(R.id.et2_odataPath);
        et_odataName = (EditText) findViewById(R.id.et2_odataName);
        et_odataPassword = (EditText) findViewById(R.id.et2_odataPassword);

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
            default:
                break;
        }
    }

    private void saveSetting() {
        boolean needShowToast = false;
        if (!TextUtils.isEmpty(et_servicePath.getText().toString()) && !TextUtils.isEmpty(et_odataPath.getText().toString())) {
            needShowToast = true;
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.SERVICE_PATH, et_servicePath.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.IMAGE_PATH, et_imagePath.getText().toString());
            SharedPreferencesUtils.setParam(this,SharedPreferenceConstant.ODATA_PATH,et_odataPath.getText().toString());
            SharedPreferencesUtils.setParam(this,SharedPreferenceConstant.ODATA_USERNAME,et_odataName.getText().toString());
            SharedPreferencesUtils.setParam(this,SharedPreferenceConstant.ODATA_PASSWORD,et_odataPassword.getText().toString());
            AppContants.ServicePath = et_servicePath.getText().toString();
            AppContants.ImagePath = et_imagePath.getText().toString();
            ApiTool.currentApiUrl = et_odataPath.getText().toString();
            ApiTool.currentAuthName = et_odataName.getText().toString();
            ApiTool.currentAuthPsw = et_odataPassword.getText().toString();
        }
        if (needShowToast) {
            ShowTipMessage("保存成功！");
        }
    }

    private void showSetting() {
        et_servicePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.SERVICE_PATH, AppContants.ServicePath).toString());
        et_imagePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.IMAGE_PATH, AppContants.ImagePath).toString());
        et_odataPath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_PATH,ApiTool.currentApiUrl).toString());
        et_odataName.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_USERNAME,ApiTool.currentAuthName).toString());
        et_odataPassword.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.ODATA_PASSWORD,ApiTool.currentAuthPsw).toString());
    }
}
