package com.gki.managerment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gki.managerment.bean.ConditionEntity;
import com.gki.managerment.constant.SharedPreferenceConstant;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.http.AppContants;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.SharedPreferencesUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.UserTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ChangePassword extends BaseActivity implements OnClickListener {
    private Button btnBack, btnSave;
    private EditText et_oldPassword,et_newPassword,et_confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btn_back):
                ChangePassword.this.finish();
                break;
            case R.id.btn_save:
                savePassword();
                break;
            default:
                break;
        }
    }

    private void savePassword() {
        boolean isCanUpdate = true;
        if (StringUtils.isEmpty(et_oldPassword.getText().toString()))
        {
            ShowTipMessage("请输入【原始密码】！");
            isCanUpdate = false;
        }
        if (StringUtils.isEmpty(et_newPassword.getText().toString()))
        {
            ShowTipMessage("请输入【新密码】！");
            isCanUpdate = false;
        }
        if (StringUtils.isEmpty(et_confirmPassword.getText().toString()))
        {
            ShowTipMessage("请输入【确认密码】！");
            isCanUpdate = false;
        }
        if (!et_confirmPassword.getText().toString().equals(et_newPassword.getText().toString()))
        {
            ShowTipMessage("新旧密码不一致！");
            isCanUpdate = false;
        }
        if (isCanUpdate) {
            updateData();
        }
    }

    private void updateData() {
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }
            @Override
            protected void onErrorHandle(Context context, Exception error) {
                ShowTipMessage("修改失败!",error.getMessage());
                dismissLoadingDialog();
            }
            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                if (StringUtils.isNotEmpty(s) && s.equals("true")) {
                    ShowTipMessage("修改成功!",s);
                    return;
                }
                else
                {
                    ShowTipMessage("修改失败!",s);
                    return;
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return new getService().UpdatePswd_User(LoginUser.getUser().User_ID,et_oldPassword.getText().toString(),et_confirmPassword.getText().toString());
            }
        }).execute();
    }
}
