package com.gki.v107.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.PadMessageAddon;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.util.Map;

public class BuildMessageActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_message);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.show();
            actionBar.setTitle("留言消息");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editText = (EditText) findViewById(R.id.et2_build_message);
        button = (Button) findViewById(R.id.button2_build_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString().trim();
                if(msg.isEmpty()){
                    ToastUtil.show(BuildMessageActivity.this,"请输入留言消息");
                    return;
                }
                PadMessageAddon addon = new PadMessageAddon();
                addon.setCreate_User(LoginUser.getUser().getUserId());
                addon.setMsg(msg);
                addon.setCreate_Name(LoginUser.getUser().getUserId());
                addon.setCreate_DateTime(DatetimeTool.getCurrentOdataDatetime());
                addon.setProdLineName(LoginUser.getUser().ProdLineName);
                addon.setProdLine(LoginUser.getUser().Prod_Line);
                ApiTool.addPadMessage(addon, new BaseOdataCallback<Map<String, Object>>() {
                    @Override
                    public void onDataAvailable(Map<String, Object> datas) {
                        ToastUtil.show(BuildMessageActivity.this,"提交成功");
                        finish();
                    }

                    @Override
                    public void onDataUnAvailable(String msg, int errorCode) {
                        ToastUtil.show(BuildMessageActivity.this,"提交失败,"+msg);
                    }
                });
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
