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

public class Setting extends BaseActivity implements OnClickListener {
    private Button btnBack, btnSave;
    private EditText et_servicePath,et_imagePath;

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
        if (!TextUtils.isEmpty(et_servicePath.getText().toString())) {
            needShowToast = true;
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.SERVICE_PATH, et_servicePath.getText().toString());
            SharedPreferencesUtils.setParam(this, SharedPreferenceConstant.IMAGE_PATH, et_imagePath.getText().toString());
            AppContants.ServicePath = et_servicePath.getText().toString();
            AppContants.ImagePath = et_imagePath.getText().toString();
        }
        if (needShowToast) {
            ShowTipMessage("保存成功！");
        }
    }

    private void showSetting() {
        et_servicePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.SERVICE_PATH, AppContants.ServicePath).toString());
        et_imagePath.setText(SharedPreferencesUtils.getParam(this, SharedPreferenceConstant.IMAGE_PATH, AppContants.ImagePath).toString());
    }
}
